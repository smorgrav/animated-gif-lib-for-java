package com.madgag.gif.fmsware;

/**
 * Builder class for GifImage.
 *
 * This is a convenience class useful when composing a GIF animation -
 * where each frame typically share the same delay.
 *
 * TODO add support for global color table, userinput, and interlace
 * TODO add support for text extension
 * TODO set transparent color and background - color not index - how to?
 *
 * @author smorgrav
 */
public class Giffer {

    private static String DEFAULT_VERSION = "GIF89a";

    private int width = -1;
    private int height = -1;
    private GifColorTable gct;
    private int backgroundIndex = -1;
    private GifGraphicControlExt currentGCE = GifGraphicControlExt.DEFAULT;
    private GifImage image;
    private int loopCount = 1;
    private boolean interlace = false;

    public static Giffer create() {
        return new Giffer();
    }

    GifImage build() {
        if (image == null) {
            image = new GifImage(DEFAULT_VERSION, width, height, gct, backgroundIndex);
            image.setLoopCount(loopCount);
        }
        return image;
    }

    Giffer addFrame(int argb[], int width, int height) {
        if (this.width == -1) {
            this.width = width;
        }
        if (this.height == -1) {
            this.height = height;
        }
        if (image == null) {
            build();
        }
        image.addFrame(argb, width, height, currentGCE, interlace);
        return this;
    }

    Giffer addFrame(int argb[]) {
        if (image == null) {
            build();
        }
        image.addFrame(argb, width, height, currentGCE, interlace);
        return this;
    }

    Giffer setWidth(int width) {
        if (this.width != -1) {
            throw new RuntimeException("You cannot change width - this needs to be fixed");
        }
        this.width = width;
        return this;
    }

    Giffer setHeight(int height) {
        if (this.height != -1) {
            throw new RuntimeException("You cannot change height - this needs to be fixed");
        }
        this.height = height;
        return this;
    }

    Giffer setBackgroundIndex(int index) {
        this.backgroundIndex = index;
        return this;
    }

    Giffer withInterlace(boolean interlace) {
        this.interlace = interlace;
        return this;
    }

    Giffer withLoopCount(int loopCount) {
        if (image != null) {
            image.setLoopCount(loopCount);
        }
        this.loopCount = loopCount;
        return this;
    }

    Giffer withDelay(int delay) {
        currentGCE.setDelay(delay);
        return this;
    }

    Giffer setUserInput(boolean userInput) {
        currentGCE.setUserInputFlag(userInput);
        return this;
    }

    Giffer setTransparencyIndex(int index) {
        currentGCE.setTransparcyIndex(index);
        currentGCE.setTransparent(index >= 0);
        return this;
    }

    Giffer withDisposeMethod(GifGraphicControlExt.DisposeMethod dispose) {
        currentGCE.setDispose(dispose);
        return this;
    }
}
