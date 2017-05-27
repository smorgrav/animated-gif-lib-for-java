package com.madgag.gif.fmsware;

/**
 * @author smorgrav
 */
class GifGraphicControlExt {

    static GifGraphicControlExt DEFAULT = new GifGraphicControlExt();

    enum DisposeMethod {
        NON_SPECIFIED,
        DO_NOT_DISPOSE,
        RESTORE_TO_BACKGROUND,
        RESTORE_TO_PREVIOUS,
    }

    private DisposeMethod dispose = DisposeMethod.DO_NOT_DISPOSE;
    private boolean userInputFlag = false;
    private boolean isTransparent = false;
    private int transparcyIndex = -1;
    private int delay = 0;

    void setDisposeFromValue(int value) {
        switch(value) {
            case 0:
                dispose = DisposeMethod.NON_SPECIFIED;
                break;
            case 1:
                dispose = DisposeMethod.DO_NOT_DISPOSE;
                break;
            case 2:
                dispose = DisposeMethod.RESTORE_TO_BACKGROUND;
                break;
            case 3:
                dispose = DisposeMethod.RESTORE_TO_PREVIOUS;
                break;
            default:
                throw new GifFormatException("Unknown dispose method specified: " + value);
        }
    }

    public boolean hasTransparency() {
        return isTransparent;
    }

    public void setTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    public DisposeMethod getDispose() {
        return dispose;
    }

    public void setDispose(DisposeMethod dispose) {
        this.dispose = dispose;
    }

    public boolean isUserInputFlag() {
        return userInputFlag;
    }

    public void setUserInputFlag(boolean userInputFlag) {
        this.userInputFlag = userInputFlag;
    }

    public int getTransparcyIndex() {
        return transparcyIndex;
    }

    public void setTransparcyIndex(int transparcyIndex) {
        this.transparcyIndex = transparcyIndex;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
