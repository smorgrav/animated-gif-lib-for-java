package com.madgag.gif.fmsware;

import java.util.ArrayList;
import java.util.List;

/**
 * The GIF image complete with metadata and frames.
 * <p>
 * This should hold enough information to encode the gif image and
 * methods to create or modify gif images.
 *
 * @author smorgrav
 */
class GifImage {

    private static int SAMPLE_INTERVAL = 10;

    final String version;
    final int width;
    final int height;
    final GifColorTable gct;
    private final int backGroundIndex;

    // Mutable data
    int loopCount = 1;
    final List<GifFrame> frames = new ArrayList<>();

    GifImage(String version, int width, int height, GifColorTable globalColorTable, int backGroundIndex) {
        this.version = version;
        this.width = width;
        this.height = height;
        this.gct = globalColorTable;
        this.backGroundIndex = backGroundIndex;
    }

    GifImage(int[] argb, int width, int height, int loopCount, int delay) {
        this.version = "GIF89a";
        this.width = width;
        this.height = height;
        this.gct = null; // Create or leave it byt default?
        this.backGroundIndex = -1;
        //Add first frame
    }

    /**
     * Export a fully composed raster as an argb array with width*height size and layout.

     * Note on the final raster composition:
     * Each frame might have transparent pixel or size that is not covering the entire image.
     * This makes all frames potentially dependent on the all frames before it. To make it
     * easy... just draw all frames in order to make the result.
     *
     * @param frameNo The frame number to extract argb values for (0 indexed)
     */
    int[] getARGBValues(int frameNo) {
        int[] argb = new int[width*height];
        for (int i = 0; i <= frameNo;i++) {
                GifFrame currentFrame = frames.get(i);
                if (i < frameNo) {
                    switch (currentFrame.getDispose()) {
                        case RESTORE_TO_BACKGROUND:
                            currentFrame.getBitmap().renderWithColorTo(argb, width, gct.getColor(backGroundIndex));
                            break;
                        case NON_SPECIFIED:
                        case RESTORE_TO_PREVIOUS:
                            break;
                        case DO_NOT_DISPOSE:
                            currentFrame.getBitmap().renderTo(argb, width);
                            break;
                        default:
                            throw new GifFormatException("Unknown dispose method: " + currentFrame.getDispose());
                    }
                } else {
                    currentFrame.getBitmap().renderTo(argb, width);
                }
        }

        return argb;
    }

    void addFrame(int[] argb, int width, int height) {
        // What about extensions etc? transparency, background color, delay, etc
        GifColorTable colorTable = createColorTable(argb);

        int[] indexedPixels = new int[argb.length];
        // Find neareset pixel
    }

    void addFrame(GifFrame frame) {
        frames.add(frame);
    }

    /**
     * Analyzes pixels and create color map.
     */
    private GifColorTable createColorTable(int[] pixels) {

        byte[] rgbPixels = new byte[pixels.length*3];
        for (int i = 0; i < pixels.length; i++) {
            rgbPixels[i*3 + 0] = (byte) ((pixels[i] >> 16) & 0xff);
            rgbPixels[i*3 + 1] = (byte) ((pixels[i] >> 8) & 0xff);
            rgbPixels[i*3 + 2] = (byte) ((pixels[i] >> 0) & 0xff);
        }

        NeuQuant nq = new NeuQuant(rgbPixels, rgbPixels.length, SAMPLE_INTERVAL);
        byte[] colorTab = nq.process();

        int[] colorTable = new int[colorTab.length/3];
        for (int i = 0; i < pixels.length; i++) {
            colorTable[i] = (byte) ((colorTab[i*3 + 1] << 16) & 0xff);
            rgbPixels[i] = (byte) ((colorTab[i*3] >> 8) & 0xff);
            rgbPixels[i] = (byte) ((colorTab[i*3] >> 0) & 0xff);
        }

        return new GifColorTable(colorTable, false);
    }
}