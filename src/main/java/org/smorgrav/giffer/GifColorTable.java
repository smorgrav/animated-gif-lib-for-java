package org.smorgrav.giffer;

/**
 * A color table of integers composed of a,r,g,b components each of 8 bits.
 *
 * Methods to operate on the color table.
 *
 * @author smorgrav
 */
public class GifColorTable {

    private final static int SAMPLE_INTERVAL = 10;

    private final int[] argbTable;
    private final boolean isGlobal;

    GifColorTable(int[] argbTable, boolean isGlobal) {
        this.argbTable = argbTable;
        this.isGlobal = isGlobal;
    }

    boolean isGlobal() {
        return isGlobal;
    }

    int getSize() {
        return argbTable.length;
    }

    int getRed(int index) {
        return (argbTable[index] >> 16) & 0xff;
    }

    int getGreen(int index) {
        return (argbTable[index] >> 8) & 0xff;
    }

    int getBlue(int index) {
        return (argbTable[index] >> 0) & 0xff;
    }

    int getColor(int index) { return argbTable[index]; }

    /**
     * Returns index of color closest to color
     *
     * Used to make indexed bitmaps of argb bitmaps (where we need to map
     * colors into a finite color table.
     */
    int findClosestIndex(int color) {
        int r = (color  >> 16) & 0xff;
        int g = (color  >> 8) & 0xff;
        int b = (color  >> 0) & 0xff;
        int minIndex = 0;
        int minDiff = 256 * 256 * 256;
        int len = argbTable.length;
        for (int i = 0; i < len; ) {
            int dr = r - getRed(i);
            int dg = g - getGreen(i);
            int db = b - getBlue(i);
            int d = dr * dr + dg * dg + db * db;
            if (d < minDiff) {
                minDiff = d;
                minIndex = i;
            }
            i++;
        }
        return minIndex;
    }

    /**
     * Analyzes pixels and create color map.
     */
    static GifColorTable create(int[] pixels, boolean asGlobal) {

        byte[] rgbPixels = new byte[pixels.length*3];
        for (int i = 0; i < pixels.length; i++) {
            rgbPixels[i*3 + 0] = (byte) ((pixels[i] >> 16) & 0xff);
            rgbPixels[i*3 + 1] = (byte) ((pixels[i] >> 8) & 0xff);
            rgbPixels[i*3 + 2] = (byte) ((pixels[i] >> 0) & 0xff);
        }

        NeuQuant nq = new NeuQuant(rgbPixels, rgbPixels.length, SAMPLE_INTERVAL);
        byte[] colorTab = nq.process(); //This is bgr values?

        int[] result = new int[colorTab.length/3];
        for (int i = 0; i < result.length; i++) {
            int a = 0xff000000;
            int r = colorTab[i*3 + 2] << 16 & 0xff0000;
            int g = colorTab[i*3 + 1] << 8 & 0xff00;
            int b = colorTab[i*3 + 0] & 0xff;
            result[i] = a | r | g | b;
        }

        return new GifColorTable(result, asGlobal);
    }

    int[] indexColors(int[] argb) {
        int[] result = new int[argb.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = findClosestIndex(argb[i]);
        }
        return result;
    }
}
