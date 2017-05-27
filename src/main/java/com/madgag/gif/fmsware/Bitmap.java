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
    private final Color[] pixels; /** Mutable set of colors pr pixel in column/row aka offsetx/y order */
    private final int offsetx; /** if this is a subimage - the offsetx/column position on the larger image */
    private final int offsety; /** if this is a subimage - the y/row position on the larger image */

    Bitmap(int width, int height) {
        this(width, height, 0,0, new int[width*height]);
    }

    Bitmap(int width, int height, int offsetx, int offsety) {
        this(width, height, offsetx, offsety, new int[width*height]);
    }

    Bitmap(int width, int height, int offsetx, int offsety, int[] argb) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[width*height];
        for (int i = 0; i < width*height; i++) {
            pixels[i] = new Color(argb[i]);
        }
        this.offsetx = offsetx;
        this.offsety = offsety;
    }

    /** Fill the entire bitmap with the specified color */
    void fill(Color c) {
        for (int i = 0; i < width*height; i++) {
            pixels[i] = c;
        }
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
                if (source.pixels[sourceIndex] != null) { // if not transparent
                    pixels[targetIndex] = source.pixels[sourceIndex];
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

    void setPixel(int x, int y, Color color) {
        int pos = y*width+x;
        pixels[pos] = color;
    }

    byte[] getBGRPixels() {
        byte[] bgr = new byte[width*height*3];
        for (int i = 0; i < width*height; i++) {
            bgr[i*3 + 0] = (byte)pixels[i].getBlue();
            bgr[i*3 + 1] = (byte)pixels[i].getGreen();
            bgr[i*3 + 2] = (byte)pixels[i].getRed();
        }
        return bgr;
    }
}
