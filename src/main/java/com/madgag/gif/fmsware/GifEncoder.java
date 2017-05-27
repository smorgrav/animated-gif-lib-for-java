package com.madgag.gif.fmsware;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Class GifEncoder - Encodes a GIF file consisting of one or
 * more frames.
 * <pre>
 * Example:
 *    GifEncoder e = new GifEncoder();
 *    e.start(outputFileName);
 *    e.setDelay(1000);   // 1 frame per sec
 *    e.addFrame(image1);
 *    e.addFrame(image2);
 *    e.finish();
 * </pre>
 * No copyright asserted on the source code of this class.  May be used
 * for any purpose, however, refer to the Unisys LZW patent for restrictions
 * on use of the associated LZWEncoder class.  Please forward any corrections
 * to questions at fmsware.com.
 *
 * @author Kevin Weiner, FM Software
 * @version 1.03 November 2003
 */
class GifEncoder {

    private OutputStream out;
    private GifImage image;

    public static void encode(GifImage image, OutputStream out) throws IOException {
        GifEncoder encoder = new GifEncoder(image, out);
        encoder.encode();
    }

    private GifEncoder(GifImage image, OutputStream out) {
        this.out = out;
        this.image = image;
    }

    private void encode() throws IOException {
        writeString(image.version);
        writeLogicalScreenDescription();
        if (image.gct != null) {
            writeColorTable(image.gct);
        }
        if (image.loopCount != 0) {
            writeNetscapeExt();
        }

        for (GifFrame frame : image.frames) {
            writeGraphicCtrlExt(frame.getGraphicControlExt());
            writeImageDesc(frame.getBitmap());
            if (!frame.getBitmap().getColorTable().equals(image.gct)) {
                writeColorTable(frame.getBitmap().getColorTable());
            }
            writeBitmap(frame.getBitmap());
        }
    }

    /**
     * Writes Graphic Control Extension
     */
    private void writeGraphicCtrlExt(GifGraphicControlExt gce) throws IOException {
        out.write(0x21); // extension introducer
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
    private void writeLogicalScreenDescription() throws IOException {
        // logical screen size
        writeShort(image.width);
        writeShort(image.height);

        // packed fields see description
        int gctSize = 0;
        int gctEnabled = 0;
        int colorDepth = 0x70; //7 bits
        int colorTableSorted = 0;
        if (image.gct != null) {
            gctEnabled = 0x80;
            gctSize = (31 - Integer.numberOfLeadingZeros(image.gct.table.length)) - 1;
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
    private void writeNetscapeExt() throws IOException {
        out.write(0x21); // extension introducer
        out.write(0xff); // app extension label
        out.write(11); // block size
        writeString("NETSCAPE2.0"); // app id + auth code
        out.write(3); // sub-block size
        out.write(1); // loop sub-block id
        writeShort(image.loopCount); // loop count (extra iterations, 0=repeat forever)
        out.write(0); // block terminator
    }

    /**
     * Writes Image Descriptor
     */
    private void writeImageDesc(Bitmap bitmap) throws IOException {
        out.write(0x2c); // image separator
        writeShort(0); // image position x,y = 0,0
        writeShort(0);
        writeShort(bitmap.getWidth()); // image size
        writeShort(bitmap.getHeight());

        if (bitmap.getColorTable().equals(image.gct)) {
            out.write(0);
        } else {
            out.write(0x80 | bitmap.getColorTable().table.length);
        }
    }

    /**
     * Writes color table
     */
    private void writeColorTable(GifColorTable colorTable) throws IOException {
        byte[] colors = new byte[colorTable.table.length*3];
        for (int i = 0; i < colorTable.table.length; i += 3) {
            colors[i + 0] = (byte)colorTable.table[i].getRed();
            colors[i + 1] = (byte)colorTable.table[i].getGreen();
            colors[i + 2] = (byte)colorTable.table[i].getBlue();
        }

        out.write(colors, 0, colorTable.table.length);
        /** int n = (3 * 256) - colorTable.table.length;
        for (int i = 0; i < n; i++) {
            out.write(0);
         is this nessesary?
        } */
    }

    /**
     * Encodes and writes pixel data
     */
    private void writeBitmap(Bitmap bitmap) throws IOException {
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
                new LZWEncoder(bitmap.getWidth(), bitmap.getHeight(), byteArray, 7);
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
