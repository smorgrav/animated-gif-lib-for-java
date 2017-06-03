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

    GifGraphicControlExt.DisposeMethod getDispose() {
        return gce.getDispose();
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
