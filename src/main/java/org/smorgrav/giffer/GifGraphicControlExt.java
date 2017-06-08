package org.smorgrav.giffer;

/**
 * @author smorgrav
 */
class GifGraphicControlExt {

    static GifGraphicControlExt DEFAULT = new GifGraphicControlExt();

    private GifDispose dispose = GifDispose.NON_SPECIFIED;
    private boolean userInputFlag = false;
    private boolean isTransparent = false;
    private int transparcyIndex = 0;
    private int delay = 0;

    GifDispose getDispose() {
        return dispose;
    }

    void setDispose(GifDispose dispose) {
        this.dispose = dispose;
    }

    boolean hasTransparency() {
        return isTransparent;
    }

    void setTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    void setUserInputFlag(boolean userInputFlag) {
        this.userInputFlag = userInputFlag;
    }

    boolean getUserInputFlag() {
        return this.userInputFlag;
    }

    int getTransparcyIndex() {
        return transparcyIndex;
    }

    void setTransparcyIndex(int transparcyIndex) {
        this.transparcyIndex = transparcyIndex;
    }

    int getDelay() {
        return delay;
    }

    void setDelay(int delay_tenth_ms) {
        this.delay = delay_tenth_ms;
    }
}
