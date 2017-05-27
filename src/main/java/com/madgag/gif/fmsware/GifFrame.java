package com.madgag.gif.fmsware;

/**
 * @author smorgrav
 */
class GifFrame {

    private final Bitmap bitmap;
    private final GifGraphicControlExt gce;
    private final boolean interlace;

    GifFrame(Bitmap bitmap, GifGraphicControlExt gce, boolean interlace) {
        this.bitmap = bitmap;
        this.gce = gce;
        this.interlace = interlace;
    }

    void draw(Bitmap source) {
        bitmap.draw(source);
    }

    boolean isTransparent(int colorIndex) {
        return (gce.hasTransparency() && gce.getTransparcyIndex() == colorIndex);
    }

    boolean isInterlaced() {
        return interlace;
    }

    GifGraphicControlExt.DisposeMethod getDisposeMethod() {
        return gce.getDispose();
    }

    Bitmap getBitmap()  {
        return bitmap;
    }

    GifGraphicControlExt getGraphicControlExt() {
        return gce;
    }
}
