package com.madgag.gif.fmsware;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Write a GifImage to an outputstream.
 *
 * @see GifImage
 *
 * @author smorgrav
 * @author Kevin Weiner, FM Software
 * @version 2
 */
class GifEncoder {

    private static int COLOR_DEPTH = 8;

    private OutputStream out;

    static void encode(GifImage image, OutputStream out) throws IOException {
        GifEncoder encoder = new GifEncoder(out);
        encoder.encode(image);
    }

    static void encode(GifFrame frame, OutputStream out) throws IOException {
        GifEncoder encoder = new GifEncoder(out);
        encoder.encode(frame);
    }

    private GifEncoder(OutputStream out) {
        this.out = out;
    }

    private void encode(GifImage image) throws IOException {
        writeString(image.getVersion());
        writeLogicalScreenDescription(image);

        // Global color tabel is optional
        if (image.getGlobalColorTable() != null) {
            writeColorTable(image.getGlobalColorTable());
        }

        // Standard GIF spec is to run through the images once
        if (image.getLoopCount() != 1) {
            writeNetscapeExt(image);
        }


        for (GifFrame frame : image.getFrames()) {
            encode(frame);
        }

        // GIF Terminator
        out.write(0x3B);
    }

    void encode(GifFrame frame) throws IOException {
        if (frame.hasGraphicControlExt()) {
            writeGraphicCtrlExt(frame.getGraphicControlExt());
        }

        writeImageDesc(frame.getBitmap());

        // Write colortable only if it is local (not global)
        if (!frame.getBitmap().getColorTable().isGlobal()) {
            writeColorTable(frame.getBitmap().getColorTable());
        }

        writeBitmap(frame.getBitmap());
    }

    /**
     * Writes Graphic Control Extension
     */
    private void writeGraphicCtrlExt(GifGraphicControlExt gce) throws IOException {
        out.write(0x21); // extension code
        out.write(0xf9); // GCE label
        out.write(4); // data block size

        int dispose = gce.getDisposeValue();
        dispose <<= 2;
        int tranparency = gce.hasTransparency() ? 1 : 0;
        int packed = dispose | tranparency;
        out.write(packed);

        writeShort(gce.getDelay());
        out.write(gce.getTransparcyIndex()); // transparent color index
        out.write(0); // block terminator
    }

    /**
     * Writes Logical Screen Descriptor
     */
    private void writeLogicalScreenDescription(GifImage image) throws IOException {
        // logical screen size
        writeShort(image.getWidth());
        writeShort(image.getHeight());

        // packed fields see description
        int gctSize = 0;
        int gctEnabled = 0;
        int colorDepth = 0x70; //7 bits
        int colorTableSorted = 0;
        if (image.getGlobalColorTable() != null) {
            gctEnabled = 0x80;
            gctSize = (31 - Integer.numberOfLeadingZeros(image.getGlobalColorTable().getSize())) - 1;
        }

        int packed = gctEnabled | colorDepth | colorTableSorted | gctSize;
        out.write(packed);

        out.write(0); // background color index
        out.write(0); // pixel aspect ratio - assume 1:1
    }

    /**
     * Writes Netscape application extension to define
     * repeat count.
     */
    private void writeNetscapeExt(GifImage image) throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xff); // app extension label
        out.write(11); // block size
        writeString("NETSCAPE2.0"); // app id + auth code
        out.write(3); // sub-block size
        out.write(1); // loop sub-block id
        writeShort(image.getLoopCount()); // loop count (extra iterations, 0=repeat forever)
        out.write(0); // block terminator
    }

    /**
     * Writes Image Descriptor
     */
    private void writeImageDesc(GifBitmap bitmap) throws IOException {
        out.write(0x2c); // image separator
        writeShort(0); // image position x,y = 0,0
        writeShort(0); // TODO Add offset here - we have it so why not do it?
        writeShort(bitmap.getWidth()); // image size
        writeShort(bitmap.getHeight());

        int sortFlag = 0; // TODO
        int interlaceFlag = 0;
        if (bitmap.getColorTable().isGlobal()) {
            out.write(0);
        } else {
            int lctSize = (31 - Integer.numberOfLeadingZeros(bitmap.getColorTable().getSize())) - 1;
            out.write(0x80 | lctSize);
        }
    }

    /**
     * Writes color argbTable
     */
    private void writeColorTable(GifColorTable colorTable) throws IOException {
        byte[] colors = new byte[colorTable.getSize()*3];
        for (int i = 0; i < colorTable.getSize(); i++) {
            colors[i*3 + 0] = (byte)colorTable.getRed(i);
            colors[i*3 + 1] = (byte)colorTable.getGreen(i);
            colors[i*3 + 2] = (byte)colorTable.getBlue(i);
        }

        out.write(colors, 0, colors.length);
    }

    /**
     * Encodes and writes pixel data
     */
    private void writeBitmap(GifBitmap bitmap) throws IOException {
        int[] indexedPixels = bitmap.getColorIndices();
        byte[] byteArray = new byte[indexedPixels.length];
        for (int i = 0; i < byteArray.length; i++) {
            byteArray[i] = (byte)indexedPixels[i];
        }

        //
        // TODO interlace if nessesary
        //

        //
        // LZW compress and write out
        //
        LZWEncoder encoder =
                new LZWEncoder(bitmap.getWidth(), bitmap.getHeight(), byteArray, COLOR_DEPTH);
        encoder.encode(out);
    }

    /**
     * Write 16-bit value to output stream, LSB first
     */
    private void writeShort(int value) throws IOException {
        out.write(value & 0xff);
        out.write((value >> 8) & 0xff);
    }

    /**
     * Writes string to output stream
     */
    private void writeString(String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }
    }
}
