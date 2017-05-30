package com.madgag.gif.fmsware;

/**
 * Not just a bitmap but a bitmap of indexed colors, the color table the indices refers to
 * and the meta data to position the bitmap onto other bitmaps.
 *
 * Also handle transparency color (which means skip pixel with this color set).
 *
 * @author smorgrav
 */
class GifBitmap {

    private final int width;
    private final int height;
    private final int[] colorIndices;
    private final int offsetx;
    private final int offsety;
    private final GifColorTable colorTable;
    private final boolean hasTransparency;
    private final int transparentColorIndex;

    GifBitmap(int width, int height, int offsetx, int offsety, GifColorTable colorTable, int[] colorIndices) {
        this.width = width;
        this.height = height;
        this.colorIndices = colorIndices;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.colorTable = colorTable;
        this.hasTransparency = false;
        this.transparentColorIndex = 0;
    }

    /**
     * Render/populate the colors from this bitmap onto a argb array.
     *
     * The array layout must be equal or larger than this bitmap
     */
    void renderTo(int[] argb, int argbWidth) {
        if (argb.length < width*height) {
            throw new IllegalArgumentException("The target argb array is too small: " + argb.length);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sourceIndex = y*width + x;
                int targetIndex = (offsety+y)*argbWidth + offsetx + x;
                // Only set color on target if the current pixel is not transparent
                if (!hasTransparency || transparentColorIndex != colorIndices[sourceIndex]) {
                    int colorIndex = colorIndices[sourceIndex];
                    int color = colorTable.getColor(colorIndex);
                    argb[targetIndex] = color;
                }
            }
        }
    }

    /**
     * Render/populate the background color from this bitmap onto a argb array.
     */
    void renderWithColorTo(int[] argb, int argbWidth, int color) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int targetIndex = (offsety+y)*argbWidth + offsetx + x;
                argb[targetIndex] = color;
            }
        }
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
    }

    GifColorTable getColorTable() { return colorTable; }

    int[] getColorIndices() {
        return colorIndices;
    }
}
