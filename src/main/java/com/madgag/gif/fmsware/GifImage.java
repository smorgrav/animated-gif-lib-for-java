package com.madgag.gif.fmsware;

import java.util.ArrayList;
import java.util.List;

/**
 * The GIF image complete with metadata and frames.
 * <p>
 * This should hold enough information to encode a valid gif image.
 *
 * @author smorgrav
 */
class GifImage {
    final String version;
    final int width;
    final int height;
    final GifColorTable gct;

    // Mutable data
    int loopCount = 1;
    final List<GifFrame> frames = new ArrayList<>();

    GifImage(String version, int width, int height, GifColorTable globalColorTable) {
        this.version = version;
        this.width = width;
        this.height = height;
        this.gct = globalColorTable;
    }
}