package org.smorgrav.giffer;

import static org.smorgrav.giffer.GifGraphicControlExt.DisposeMethod.RESTORE_TO_PREVIOUS;

/**
 * @author smorgrav
 */
class GifGraphicControlExt {

    static GifGraphicControlExt DEFAULT = new GifGraphicControlExt();

    private DisposeMethod dispose = DisposeMethod.NON_SPECIFIED;
    private boolean userInputFlag = false;
    private boolean isTransparent = false;
    private int transparcyIndex = 0;
    private int delay = 0;

    enum DisposeMethod {
        NON_SPECIFIED,
        DO_NOT_DISPOSE,
        RESTORE_TO_BACKGROUND,
        RESTORE_TO_PREVIOUS,
    }

    int getDisposeValue() {
        switch (dispose) {
            case NON_SPECIFIED:
                return 0;
            case DO_NOT_DISPOSE:
                return 1;
            case RESTORE_TO_BACKGROUND:
                return 2;
            case RESTORE_TO_PREVIOUS:
                return 3;
            default:
                throw new GifFormatException("Unknown dispose method specified: " + dispose);
        }
    }

    void setDispose(DisposeMethod dispose) {
        this.dispose = dispose;
    }

    void setDisposeFromValue(int value) {
        switch (value) {
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
                dispose = RESTORE_TO_PREVIOUS;
                break;
            default:
                throw new GifFormatException("Unknown dispose method specified: " + value);
        }
    }

    boolean hasTransparency() {
        return isTransparent;
    }

    void setTransparent(boolean isTransparent) {
        this.isTransparent = isTransparent;
    }

    DisposeMethod getDispose() {
        return dispose;
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
