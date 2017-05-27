package com.madgag.gif.fmsware;

/**
 * Plain bitmap representation
 *
 * Optimized for comprehension and not speed or memory :)
 *
 * @author smorgrav
 */
class Bitmap {

    private final int width;
    private final int height;
    private final int[] colorIndices; /** The pixels as indices into the colortable. -1 means unset */
    private final int offsetx; /**  offsetx/column/x position on the larger image */
    private final int offsety; /**  offsety/row/y position on the larger image */
    private final GifColorTable colorTable;
    private final boolean hasTransparency;
    private final int transparentColorIndex;

    Bitmap(int width, int height, int offsetx, int offsety, GifColorTable colorTable, int[] indexedPixels) {
        this.width = width;
        this.height = height;
        this.colorIndices = indexedPixels;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.colorTable = colorTable;
        this.hasTransparency = false;
        this.transparentColorIndex = 0;
    }

    /**
     * Draw the entire source bitmap to this bitmap.
     *
     * The source bitmap must be equal or smaller thant this bitmap - using source.offsetx, source.offsety to
     * position the source onto this.
     *
     * TODO allow to draw a bigger image too?
     *
     * @param source The bitmap to draw on this bitmap.
     */
    void draw(Bitmap source) {
        if ((source.width + source.offsetx) > width || (source.height + source.offsety) > height) {
            throw new IllegalArgumentException("Cannot draw an image lager than the target!");
        }

        for (int x = 0; x < source.width; x++) {
            for (int y = 0; y < source.height; y++) {
                int sourceIndex = y*source.width + x;
                int targetIndex = (source.offsety+y)*width + source.offsetx +x;
                if (!source.hasTransparency || source.colorIndices[sourceIndex] != source.transparentColorIndex) {
                    colorIndices[targetIndex] = source.colorIndices[sourceIndex];
                }
            }
        }
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    int getOffsetX() {
        return offsetx;
    }

    int getOffsetY() {
        return offsetx;
    }

    void setPixel(int x, int y, int colorIndex) {
        int pos = y*width+x;
        colorIndices[pos] = colorIndex;
    }

    GifColorTable getColorTable() { return colorTable; }

    int[] getColorIndices() {
        return colorIndices;
    }
}
