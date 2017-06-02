package com.madgag.gif.fmsware;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Gif workflow builder
 *
 * This is the public interface to the library. Should be
 * simple and hassle free to use.
 *
 * TODO add support for user input, and interlace
 * TODO add support for text extension
 * TODO make methods indicate if for image or for frame, next or previous
 *
 * @author smorgrav
 */
public class Giffer {

    private static String DEFAULT_VERSION = "GIF89a";

    private int width = -1;
    private int height = -1;
    private GifColorTable gct;
    private int backgroundIndex = -1;
    private int backgroundColor = -1;
    private int transparencyColor = -1;
    private GifGraphicControlExt currentGCE = new GifGraphicControlExt();
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
            // Create global color table (for first image and text etc)
            gct = GifColorTable.create(argb);
            GifBitmap tempMap = new GifBitmap(this.width, this.height, 0, 0, gct, argb);
            int[] finalFirst = new int[this.width* this.height];
            tempMap.renderWithColorTo(finalFirst,width,backgroundColor);
            gct = GifColorTable.create(finalFirst);

            // Find background index
            if (backgroundColor > -1) {
                backgroundIndex = gct.findClosestIndex(backgroundColor);
            }

            // TODO gct can only be made after adding first frame ... reflect that for GifImage
            // TODO also add extensions

            build();
        }

        GifColorTable colorTable = GifColorTable.create(argb);
        if (transparencyColor > -1) {
            currentGCE.setTransparcyIndex(colorTable.findClosestIndex(transparencyColor));
        }

        image.addFrame(argb, width, height, currentGCE, interlace);
        return this;
    }

    Giffer addFrame(int argb[]) {
        addFrame(argb, width, height);
        return this;
    }

    Giffer withWidth(int width) {
        if (this.width != -1) {
            throw new RuntimeException("You cannot change width - this needs to be fixed");
        }
        this.width = width;
        return this;
    }

    Giffer withHeight(int height) {
        if (this.height != -1) {
            throw new RuntimeException("You cannot change height - this needs to be fixed");
        }
        this.height = height;
        return this;
    }

    Giffer withBackground(int argb) {
        this.backgroundColor = argb;
        return this;
    }

    Giffer withFrameInterlace(boolean interlace) {
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

    Giffer withFrameDelay(int delay) {
        currentGCE.setDelay(delay);
        return this;
    }

    Giffer withFrameUserInput(boolean userInput) {
        currentGCE.setUserInputFlag(userInput);
        return this;
    }

    Giffer withFrameTransparency(int argb) {
        transparencyColor = argb;
        return this;
    }

    Giffer withFrameDispose(GifGraphicControlExt.DisposeMethod dispose) {
        currentGCE.setDispose(dispose);
        return this;
    }

    Giffer encodeTo(OutputStream stream) {
        if (image == null) {
            build();
        }
        try {
            GifEncoder.encode(image, stream);
        } catch (IOException e) {
            throw new GifferException();
        }
        return this;
    }

    Giffer decodeFrom(InputStream stream) {
        image = GifDecoder.decode(stream);
        return this;
    }

    int[] getARGBInts() {
        return image.getARGBValues(image.getFrames().size() - 1);
    }
}
