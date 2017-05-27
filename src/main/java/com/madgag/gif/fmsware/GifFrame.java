package com.madgag.gif.fmsware;

/**
 * @author smorgrav
 */
class GifFrame {

    private final Bitmap bitmap;
    private final GifColorTable lct;
    private final GifGraphicControlExt gce;
    private final boolean interlace;

    GifFrame(Bitmap bitmap, GifColorTable lct, GifGraphicControlExt gce, boolean interlace) {
        this.bitmap = bitmap;
        this.lct = lct;
        this.gce = gce;
        this.interlace = interlace;
    }

    private Color getBackground() {
        return new Color(lct.table[lct.backgroundIndex]);
    }

    void fillWithBackgroundColor() {
        bitmap.fill(getBackground());
    }

    void draw(Bitmap source) {
        bitmap.draw(source);
    }

    Color getColor(int colorIndex) {
        if(!gce.hasTransparency() || gce.getTransparcyIndex() != colorIndex) {
            return new Color(lct.table[colorIndex]);
        }
        return null;
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
}
