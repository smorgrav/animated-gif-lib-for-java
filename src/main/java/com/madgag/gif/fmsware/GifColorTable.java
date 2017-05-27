package com.madgag.gif.fmsware;

/**
 * @author smorgrav
 */
public class GifColorTable {
    final Color[] table;
    final int backgroundIndex;

    GifColorTable(Color[] table, int backgroundIndex) {
        this.backgroundIndex = backgroundIndex;
        this.table = table;
    }

    GifColorTable(int[] table, int backgroundIndex) {
        this.backgroundIndex = backgroundIndex;
        this.table = new Color[table.length];
        for (int i = 0; i < this.table.length; i++) {
            this.table[i] = new Color(table[i]);
        }
    }
}
