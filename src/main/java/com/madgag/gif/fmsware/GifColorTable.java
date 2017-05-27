package com.madgag.gif.fmsware;

/**
 * @author smorgrav
 */
public class GifColorTable {
    final int[] table;
    final int backgroundIndex;

    GifColorTable(int[] table, int backgroundIndex) {
        this.backgroundIndex = backgroundIndex;
        this.table = table;
    }
}
