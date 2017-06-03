package com.madgag.gif.fmsware;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Decode an inputstream into a GifImage
 *
 * @see GifImage
 *
 * @author Torbjorn Smorgrav; Rewrite for usage without java.awt and make it more readable.
 * @author Kevin Weiner, FM Software; LZW decoder adapted from John Cristy's ImageMagick.
 * @version 2
 */
class GifDecoder {

    private static final int MAX_STACK_SIZE = 4096;

    private BufferedInputStream in;

    private byte[] block = new byte[255]; // Temporal working buffer

    /**
     * Reads GIF image from stream
     *
     * @param is BufferedInputStream for the GIF file
     * @return The internal GifImage data structure.
     */
    public static GifImage decode(InputStream is) {
        GifDecoder decoder = new GifDecoder(new BufferedInputStream(is));
        return decoder.decode();
    }

    /**
     * Reads GIF image from stream
     *
     * @param is InputStream for the GIF file
     * @return read status code (0 = no errors)
     */
    private GifDecoder(BufferedInputStream is) {
        this.in = is;
    }

    private GifImage decode() {
        GifImage image = readHeaderAndInitImage();
        readContentBlocks(image);
        return image;
    }

    /**
     * Read header information including, version, logical screen display and global color argbTable.
     *
     * Section 17,18 and 19 from https://www.w3.org/Graphics/GIF/spec-gif89a.txt
     */
    private GifImage readHeaderAndInitImage() {
        //
        // Magic number/GIF version (section 17)
        //
        String version = "";
        for (int i = 0; i < 6; i++) {
            version += (char) read();
        }
        if (!version.startsWith("GIF")) {
            throw new GifFormatException("This does not look like a GIF file. Magic number/id was: " + version);
        }

        //
        // Logical screen display info (LSD) (section 18)
        //
        int width = readShort();
        int height = readShort();

        int packed = read();
        boolean gctFlag = (packed & 0x80) != 0;
        int gctSize = 2 << (packed & 7);    // 6-8 : gct size
        int bgIndex = read(); // background color index
        int pixelAspect = read();

        //
        // Global color argbTable (section 19)
        //
        GifColorTable gct = null;
        if (gctFlag) {
            int[] gctArray = readColorTable(gctSize);
            gct = new GifColorTable(gctArray, true);
        }

        return new GifImage(version, width, height, gct, bgIndex, pixelAspect);
    }

    /**
     * Main file parser. Reads GIF content blocks.
     */
    private void readContentBlocks(GifImage image) {
        GifGraphicControlExt gce = GifGraphicControlExt.DEFAULT; // Use default if not specified
        boolean done = false;
        while (!done) {
            int code = read();
            switch (code) {
                case 0x2C: // image separator
                    readImage(image, gce);
                    gce = GifGraphicControlExt.DEFAULT; // Reset for next image
                    break;
                case 0x21: // extension
                    code = read();
                    switch (code) {
                        case 0xf9: // graphics control extension
                            gce = readGraphicControlExt();
                            break;
                        case 0xff: // application extension
                            readBlock();
                            String app = "";
                            for (int i = 0; i < 11; i++) {
                                app += (char) block[i];
                            }
                            if (app.equals("NETSCAPE2.0")) {
                                image.setLoopCount(readNetscapeExt());
                            } else
                                skip(); // not supported
                            break;
                        default:
                            skip(); // not supported
                    }
                    break;
                case 0x3b: // terminator
                    done = true;
                    break;
                case 0x00: // bad byte, but keep going and see what happens
                    break;
                default:
                    throw new GifFormatException("Uknown block code: " + code);
            }
        }
    }


    /**
     * Untangle the interlaced layout
     */
    private static byte[] interlaceRevert(byte[] interlacedPixels, int width, int height) {
        byte[] indexedPixels = new byte[interlacedPixels.length];
        int pass = 1;
        int inc = 8;
        int iline = 0;
        for (int i = 0; i < height; i++) {
            int line = i;

                if (iline >= height) {
                    pass++;
                    switch (pass) {
                        case 2:
                            iline = 4;
                            break;
                        case 3:
                            iline = 2;
                            inc = 4;
                            break;
                        case 4:
                            iline = 1;
                            inc = 2;
                    }
                }
                line = iline;
                iline += inc;

            if (line < height) {
                for (int sx = 0; sx < width; sx++) {
                    int index = line*width + sx;
                    indexedPixels[index] = interlacedPixels[index];
                }
            }
        }
        return indexedPixels;
    }

    /**
     * Decodes LZW image data into pixel array.
     * Adapted from John Cristy's ImageMagick.
     */
    private byte[] readAndLZWDecodePixels(int nofpixels) {
        int NullCode = -1;
        int npix = nofpixels;
        int available,
                clear,
                code_mask,
                code_size,
                end_of_information,
                in_code,
                old_code,
                bits,
                code,
                count,
                i,
                datum,
                data_size,
                first,
                top,
                bi,
                pi;

        byte[] pixels = new byte[npix];
        short[] prefix = new short[MAX_STACK_SIZE];
        byte[] suffix = new byte[MAX_STACK_SIZE];
        byte[] pixelStack = new byte[MAX_STACK_SIZE + 1];

        //  Initialize GIF data stream decoder.

        data_size = read();
        clear = 1 << data_size;
        end_of_information = clear + 1;
        available = clear + 2;
        old_code = NullCode;
        code_size = data_size + 1;
        code_mask = (1 << code_size) - 1;
        for (code = 0; code < clear; code++) {
            prefix[code] = 0;
            suffix[code] = (byte) code;
        }

        //  Decode GIF pixel stream.

        datum = bits = count = first = top = pi = bi = 0;

        for (i = 0; i < npix; ) {
            if (top == 0) {
                if (bits < code_size) {
                    //  Load bytes until there are enough bits for a code.
                    if (count == 0) {
                        // Read a new data block.
                        count = readBlock();
                        if (count <= 0)
                            break;
                        bi = 0;
                    }
                    datum += (((int) block[bi]) & 0xff) << bits;
                    bits += 8;
                    bi++;
                    count--;
                    continue;
                }

                //  Get the next code.

                code = datum & code_mask;
                datum >>= code_size;
                bits -= code_size;

                //  Interpret the code

                if ((code > available) || (code == end_of_information))
                    break;
                if (code == clear) {
                    //  Reset decoder.
                    code_size = data_size + 1;
                    code_mask = (1 << code_size) - 1;
                    available = clear + 2;
                    old_code = NullCode;
                    continue;
                }
                if (old_code == NullCode) {
                    pixelStack[top++] = suffix[code];
                    old_code = code;
                    first = code;
                    continue;
                }
                in_code = code;
                if (code == available) {
                    pixelStack[top++] = (byte) first;
                    code = old_code;
                }
                while (code > clear) {
                    pixelStack[top++] = suffix[code];
                    code = prefix[code];
                }
                first = ((int) suffix[code]) & 0xff;

                //  Add a new string to the string argbTable,

                if (available >= MAX_STACK_SIZE) {
                    pixelStack[top++] = (byte) first;
                    continue;
                }
                pixelStack[top++] = (byte) first;
                prefix[available] = (short) old_code;
                suffix[available] = (byte) first;
                available++;
                if (((available & code_mask) == 0)
                        && (available < MAX_STACK_SIZE)) {
                    code_size++;
                    code_mask += available;
                }
                old_code = in_code;
            }

            //  Pop a pixel off the pixel stack.

            top--;
            pixels[pi++] = pixelStack[top];
            i++;
        }

        for (i = pi; i < npix; i++) {
            pixels[i] = 0; // clear missing pixels
        }

        return pixels;
    }



    /**
     * Reads color argbTable as 256 RGB integer values
     *
     * @param ncolors int number of colors to read
     * @return int array containing 256 colors (packed ARGB with full alpha)
     */
    private int[] readColorTable(int ncolors) {
        int nbytes = 3 * ncolors;
        int[] tab;
        byte[] c = new byte[nbytes];
        int n = 0;
        try {
            n = in.read(c);
        } catch (IOException e) {
        }
        if (n < nbytes) {
            throw new GifFormatException("Colortable size (" + ncolors + ") less than actual bytes read (" + n + ")");
        } else {
            tab = new int[ncolors]; // max size to avoid bounds checks
            int i = 0;
            int j = 0;
            while (i < ncolors) {
                int r = ((int) c[j++]) & 0xff;
                int g = ((int) c[j++]) & 0xff;
                int b = ((int) c[j++]) & 0xff;
                tab[i++] = 0xff000000 | (r << 16) | (g << 8) | b;
            }
        }
        return tab;
    }

    /**
     * Reads Graphics Control Extension values
     */
    private GifGraphicControlExt readGraphicControlExt() {
        GifGraphicControlExt gce = new GifGraphicControlExt();
        read(); // block size

        int packed = read(); // packed fields
        gce.setDisposeFromValue((packed & 0x1c) >> 2);
        gce.setTransparent((packed & 1) != 0);
        gce.setUserInputFlag((packed & 2) != 0);

        gce.setDelay(readShort());
        gce.setTransparcyIndex((byte)read());

        read(); // block terminator

        return gce;
    }

    /**
     * Reads next frame image
     */
    private void readImage(GifImage image, GifGraphicControlExt gce) {
        //
        // Read frame dimensions and position
        //
        int ix = readShort();
        int iy = readShort();
        int iw = readShort();
        int ih = readShort();

        //
        // Get interlace and colortable info
        //
        int packed = read();
        boolean lctFlag = (packed & 0x80) != 0;
        boolean interlace = (packed & 0x40) != 0;
        int lctSize = 2 << (packed & 7);

        //
        // Read colortable if flag is set
        //
        GifColorTable colorTable = image.getGlobalColorTable();
        if (lctFlag) {
            int[] lct = readColorTable(lctSize); // read argbTable
            colorTable = new GifColorTable(lct, false);
        }

        //
        // Read in pixel data and lzw decode them
        //
        byte[] pixels = readAndLZWDecodePixels(iw*ih);
        skip();

        //
        // If interlaced - untangle that
        //
        if (interlace) {
            pixels = interlaceRevert(pixels, iw, ih);
        }

        //
        // Convert byte index to int as we don't have unsigned datatypes in java
        //
        int[] indexedPixels = new int[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            indexedPixels[i] = (int)pixels[i] & 0xff;
        }

        //
        // Wrap up
        //
        GifBitmap bitmap = new GifBitmap(iw, ih, ix, iy, colorTable, indexedPixels);
        GifFrame newFrame = new GifFrame(bitmap, gce, interlace);
        image.addFrame(newFrame);
    }

    /**
     * Reads Netscape extenstion to obtain iteration count
     */
    private int readNetscapeExt() {
        int loopCount = 1; //One iteration as default
        int blockSize = 0;
        do {
            blockSize = readBlock();
            if (block[0] == 1) {
                // loop count sub-block
                int b1 = ((int) block[1]) & 0xff;
                int b2 = ((int) block[2]) & 0xff;
                loopCount = (b2 << 8) | b1;
            }
        } while (blockSize > 0);
        return loopCount;
    }

    /**
     * Reads a single byte from the input stream.
     */
    private int read() {
        int curByte = 0;
        try {
            curByte = in.read();
        } catch (IOException e) {
            throw new GifFormatException("Unable to read next byte: " + e.getMessage());
        }
        return curByte;
    }

    /**
     * Reads next variable length block from input.
     *
     * @return number of bytes stored in "buffer"
     */
    private int readBlock() {
        int blockSize = read();
        int n = 0;
        if (blockSize > 0) {
            try {
                int count = 0;
                while (n < blockSize) {
                    count = in.read(block, n, blockSize - n);
                    if (count == -1)
                        break;
                    n += count;
                }
            } catch (IOException e) {
            }

            if (n < blockSize) {
                throw new GifFormatException("Blocksize (" + blockSize + ") less than actual bytes read (" + n + ")");
            }
        }
        return n;
    }

    /**
     * Reads next 16-bit value, LSB first
     */
    private int readShort() {
        return read() | (read() << 8);
    }

    /**
     * Skips variable length blocks up to and including
     * next zero length block.
     */
    private void skip() {
        int blockSize;
        do {
            blockSize = readBlock();
        } while ((blockSize > 0));
    }
}
