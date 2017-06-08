package org.smorgrav.giffer;

/**
 * @author smorgrav
 */
enum GifDispose {

        NON_SPECIFIED(0),
        DO_NOT_DISPOSE(1),
        RESTORE_TO_BACKGROUND(2),
        RESTORE_TO_PREVIOUS(3);

    private int value;

    GifDispose(int value) {
        this.value = value;
    }

    int getValue() {
        return value;
    }

    static GifDispose fromValue(int value) {
        switch (value) {
            case 0:
                return GifDispose.NON_SPECIFIED;
            case 1:
                return GifDispose.DO_NOT_DISPOSE;
            case 2:
                return GifDispose.RESTORE_TO_BACKGROUND;
            case 3:
                return GifDispose.RESTORE_TO_PREVIOUS;
            default:
                throw new GifFormatException("Unknown dispose method specified: " + value);
        }
    }
}
