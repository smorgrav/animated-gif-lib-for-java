package com.madgag.gif.fmsware;

import java.util.ArrayList;
import java.util.List;

/**
 * The GIF image complete with metadata and frames.
 * <p>
 * This should hold enough information to encode the gif image and
 * methods to create or modify gif images.
 *
 * TODO create a builder to set different options when creating a GIF
 *
 * @author smorgrav
 */
class GifImage {

    /** Header info */
    private final String version;
    private final int width;
    private final int height;
    private final GifColorTable gct;
    private final int backGroundIndex;

    /** Netscape 2.0 extension */
    private int loopCount = 1;

    /** The frames - images and subimages */
    private final List<GifFrame> frames = new ArrayList<>();

    GifImage(String version, int width, int height, GifColorTable globalColorTable, int backGroundIndex) {
        this.version = version;
        this.width = width;
        this.height = height;
        this.gct = globalColorTable;
        this.backGroundIndex = backGroundIndex;
    }

    GifColorTable getGlobalColorTable() {
        return gct;
    }

    String getVersion() {
        return version;
    }

    List<GifFrame> getFrames() {
        return frames;
    }

    int getLoopCount() {
        return loopCount;
    }

    void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    int getHeight() {
        return height;
    }

    int getWidth() {
        return width;
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

    void addFrame(int[] argb, int width, int height, GifGraphicControlExt gce, boolean interlace) {

        GifColorTable colorTable = frames.size() == 0 ? getGlobalColorTable() : GifColorTable.create(argb);

        int[] indexedPixels = new int[argb.length];
        for (int i = 0; i < argb.length; i++) {
            indexedPixels[i] = colorTable.findClosestIndex(argb[i]);
        }

        GifBitmap bitmap = new GifBitmap(width, height, 0, 0, colorTable, indexedPixels);

        GifFrame frame = new GifFrame(bitmap, gce, interlace);
        frames.add(frame);
    }

    void addFrame(GifFrame frame) {
        frames.add(frame);
    }
}