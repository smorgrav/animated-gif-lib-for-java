package com.madgag.gif.fmsware;

/**
 * A color table of integers composed of a,r,g,b components
 * + each of 8 bits. Methods to get the components and to
 * find colors etc.
 *
 * @author smorgrav
 */
public class GifColorTable {
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
    private int findClosest(int color) {
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
}
