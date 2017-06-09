package org.smorgrav.giffer;

/**
 * @author smorgrav
 */
class GifFrame {

    private final GifBitmap bitmap;
    private final GifGraphicControlExt gce;
    private final boolean interlace;

    GifFrame(GifBitmap raster, GifGraphicControlExt gce, boolean interlace) {
        this.bitmap = raster;
        this.gce = gce;
        this.interlace = interlace;
    }

    static GifFrame createFrame(int[] argb, int subWidth, int subHeigth, int offsetx, int offsety, GifColorTable colorTable, GifGraphicControlExt gce, boolean interlace) {

        int[] indexedPixels = new int[argb.length];
        for (int i = 0; i < argb.length; i++) {
            indexedPixels[i] = colorTable.findClosestIndex(argb[i]);
        }

        GifBitmap bitmap = new GifBitmap(subWidth, subHeigth, offsetx, offsety, colorTable, indexedPixels);

        return new GifFrame(bitmap, gce, interlace);
    }

    GifBitmap getBitmap()  {
        return bitmap;
    }

    GifGraphicControlExt getGraphicControlExt() {
        return gce;
    }

    boolean hasGraphicControlExt() {
        return !gce.equals(GifGraphicControlExt.DEFAULT);
    }
}
