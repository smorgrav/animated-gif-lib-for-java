package com.madgag.gif.fmsware;

/**
 * Plain bitmap representation
 *
 * @author smorgrav
 */
public class Bitmap {

    final int width;
    final int height;
    final Color[] pixels; //Content is still mutable

    Bitmap(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[width * height];
    }

    Bitmap(int width, int height, int[] argb) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[width*height];
        for (int i = 0; i < width*height; i++) {
            pixels[i] = new Color(argb[i]);
        }
    }

    void fill(Color c) {
        for (int i = 0; i < width*height; i++) {
            pixels[i] = c;
        }
    }

    /**
     * Draw the entire source bitmap to this bitmap.
     * TODO do Color.blend
     *
     * @param source The bitmap to draw on this bitmap.
     * @param offsetx The x position to start drawing on this bitmap
     * @param offsety The y position to start drawing on this bitmap
     */
    void draw(Bitmap source, int offsetx, int offsety) {
        if ((source.width + offsetx) > width || (source.height + offsety) > height) {
            throw new IllegalArgumentException("Cannot draw an image lager than the target!");
        }

        for (int x = 0; x < source.width; x++) {
            for (int y = 0; y < source.height; y++) {
                int sourceIndex = y*source.width + x;
                int targetIndex = (offsety+y)*width + offsetx+x;
                pixels[targetIndex] = source.pixels[sourceIndex];
                // TODO better with blending Color.blend(pixels[targetIndex], source.pixels[sourceIndex]);
            }
        }
    }

    int getWidth() {
        return width;
    }

    int getHeight() {
        return height;
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
