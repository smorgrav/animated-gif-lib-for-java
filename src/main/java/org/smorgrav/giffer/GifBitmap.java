package org.smorgrav.giffer;

/**
 * Not just a bitmap but a bitmap of indexed colors, the color table that
 * the indices refers to and the meta data to position the bitmap onto other bitmaps.
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

    GifBitmap(int width, int height, int offsetx, int offsety, GifColorTable colorTable, int[] colorIndices) {
        this.width = width;
        this.height = height;
        this.colorIndices = colorIndices;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.colorTable = colorTable;
    }

    /**
     * Render/populate the colors from this bitmap onto a argb int array.
     * <p>
     * The array layout must be equal or larger than this bitmap
     */
    void renderTo(int[] targetARGBArray, int targetWidth, GifGraphicControlExt ext) {
        if (targetARGBArray.length < width * height) {
            throw new IllegalArgumentException("The target argb array is too small: " + targetARGBArray.length);
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int sourceIndex = y * width + x;
                int targetIndex = (offsety + y) * targetWidth + offsetx + x;
                // Only set color on target if the current pixel is not transparent
                if (!ext.hasTransparency() || ext.getTransparcyIndex() != colorIndices[sourceIndex]) {
                    int colorIndex = colorIndices[sourceIndex];
                    int color = colorTable.getColor(colorIndex);
                    targetARGBArray[targetIndex] = color;
                } else {
                    int color = colorTable.getColor(ext.getTransparcyIndex());
                }
            }
        }
    }

    /**
     * Render/populate the area represented by this bitmap with a color
     * (usually the background color) onto a argb int array.
     */
    void renderWithColorTo(int[] argb, int argbWidth, int color) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int targetIndex = (offsety + y) * argbWidth + offsetx + x;
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

    GifColorTable getColorTable() {
        return colorTable;
    }

    int[] getColorIndices() {
        return colorIndices;
    }
}
