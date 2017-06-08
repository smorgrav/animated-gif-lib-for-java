package org.smorgrav.giffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private final GifColorTable gct; /** Might be null */
    private final int backGroundIndex; /** Only means something with gct is not null */
    private final int aspectRatio;

    /** Netscape 2.0 extension */
    private int loopCount = 1;

    /** The frames - images and subimages */
    private final List<GifFrame> frames = new ArrayList<>();


    GifImage(String version, int width, int height, GifColorTable globalColorTable, int backGroundIndex, int aspectRatio) {
        this.version = version;
        this.width = width;
        this.height = height;
        this.gct = globalColorTable;
        this.backGroundIndex = backGroundIndex;
        this.aspectRatio = aspectRatio;
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

    int getBackGroundIndex() {
        return backGroundIndex;
    }

    int getAspectRatio() {
        return aspectRatio;
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
                    switch (currentFrame.getGraphicControlExt().getDispose()) {
                        case RESTORE_TO_BACKGROUND:
                            if (gct == null) {
                                throw new GifferException("Cannot restore to background without a global color table");
                            }
                            currentFrame.getBitmap().renderWithColorTo(argb, width, gct.getColor(backGroundIndex));
                            break;
                        case NON_SPECIFIED:
                            break;
                        case RESTORE_TO_PREVIOUS:
                            break; //TODO implement this
                        case DO_NOT_DISPOSE:
                            currentFrame.getBitmap().renderTo(argb, width, currentFrame.getGraphicControlExt());
                            break;
                        default:
                            throw new GifFormatException("Unknown dispose method: " + currentFrame.getGraphicControlExt().getDispose());
                    }
                } else {
                    currentFrame.getBitmap().renderTo(argb, width, currentFrame.getGraphicControlExt());
                }
        }

        return argb;
    }

    void addFrame(int[] argb, int subWidth, int subHeigth, int offsetx, int offsety, GifColorTable colorTable, GifGraphicControlExt gce, boolean interlace) {

        int[] indexedPixels = new int[argb.length];
        for (int i = 0; i < argb.length; i++) {
            indexedPixels[i] = colorTable.findClosestIndex(argb[i]);
        }

        GifBitmap bitmap = new GifBitmap(subWidth, subHeigth, offsetx, offsety, colorTable, indexedPixels);

        GifFrame frame = new GifFrame(bitmap, gce, interlace);
        frames.add(frame);
    }

    void addFrame(GifFrame frame) {
        frames.add(frame);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GifImage gifImage = (GifImage) o;
        return getWidth() == gifImage.getWidth() &&
                getHeight() == gifImage.getHeight() &&
                getBackGroundIndex() == gifImage.getBackGroundIndex() &&
                getAspectRatio() == gifImage.getAspectRatio() &&
                getLoopCount() == gifImage.getLoopCount() &&
                Objects.equals(getVersion(), gifImage.getVersion());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVersion(), getWidth(), getHeight(), getBackGroundIndex(), getAspectRatio(), getLoopCount());
    }
}