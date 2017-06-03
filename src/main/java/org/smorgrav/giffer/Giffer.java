package org.smorgrav.giffer;

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
 * TODO make methods indicate if for image or for frame, next or previous - maybe make separate Frame builder?
 * TODO add generic extension list instead of netscape ext as inlined
 * TODO use optionals when supporting java8
 * TODO background - gce or part of image?
 *
 * @author smorgrav
 */
public class Giffer {

    private static String DEFAULT_VERSION = "GIF89a";

    private int width = -1;
    private int height = -1;
    private GifColorTable gct;
    private int backgroundIndex = 0;
    private int backgroundColor = 0;
    private int transparencyColor = 0;
    private boolean enableBackground = false;
    private boolean enableTransparency = false;
    private int aspectRatio = 0;
    private GifGraphicControlExt currentGCE = new GifGraphicControlExt();
    private GifImage image;
    private int loopCount = 1;
    private boolean interlace = false;

    public static Giffer create() {
        return new Giffer();
    }

    protected GifImage build() {
        return image;
    }

    public Giffer addFrame(int argb[], int subWidth, int subHeight, int offsetx, int offsety) {
        if (this.width == -1 && offsetx == 0 && this.height == -1 && offsety == 0) {
            this.width = subWidth;
            this.height = subHeight;
        } else if (this.width == -1 || this.height == -1){
            throw new GifferException("You must set the image width and height before adding sub images");
        }

        // Convert from argb values to indexed color raster
        GifColorTable colorTable = GifColorTable.create(argb, false);
        int[] colorIndices = colorTable.indexColors(argb);

        if (image == null) {
            //
            // Decide on global colortable, abd background index on first frame
            //
            if (enableBackground) {
                GifBitmap backgroundMap = new GifBitmap(this.width, this.height, 0, 0, colorTable, colorIndices);
                int[] finalFirst = new int[this.width * this.height];
                backgroundMap.renderWithColorTo(finalFirst,width,backgroundColor);
                GifBitmap firstFrameMap = new GifBitmap(subWidth, subHeight, offsetx, offsety, colorTable, colorIndices);
                firstFrameMap.renderTo(finalFirst, width, currentGCE);
                gct = GifColorTable.create(finalFirst, true);
                backgroundIndex = gct.findClosestIndex(backgroundColor);
            } else {
                gct = GifColorTable.create(argb, true);
            }

            image = new GifImage(DEFAULT_VERSION, width, height, colorTable, backgroundIndex, aspectRatio);
            image.setLoopCount(loopCount);
        }

        if (enableTransparency) {
            currentGCE.setTransparcyIndex(colorTable.findClosestIndex(transparencyColor));
        }

        image.addFrame(argb, subWidth, subHeight, offsetx, offsety, colorTable, currentGCE, interlace);
        return this;
    }

    public Giffer addFrame(int argb[]) {
        addFrame(argb, width, height, 0, 0);
        return this;
    }

    public Giffer withWidth(int width) {
        if (this.width != -1) {
            throw new GifferException("You cannot change width - this needs to be fixed");
        }
        this.width = width;
        return this;
    }

    public Giffer withHeight(int height) {
        if (this.height != -1) {
            throw new GifferException("You cannot change height - this needs to be fixed");
        }
        this.height = height;
        return this;
    }

    public Giffer withBackground(int argb) {
        this.backgroundColor = argb;
        this.enableBackground = true;
        return this;
    }

    public Giffer withFrameInterlace(boolean interlace) {
        this.interlace = interlace;
        return this;
    }

    public Giffer withPixelAspectRation(int ratio) {
        this.aspectRatio = ratio;

        return this;
    }

    public Giffer withLoopCount(int loopCount) {
        if (image != null) {
            image.setLoopCount(loopCount);
        }
        this.loopCount = loopCount;
        return this;
    }

    public Giffer withFrameDelay(int delay) {
        currentGCE.setDelay(delay);
        return this;
    }

    public Giffer withFrameUserInput(boolean userInput) {
        currentGCE.setUserInputFlag(userInput);
        return this;
    }

    //TODO reset frame stuff... create builder?
    public Giffer withFrameTransparency(int argb) {
        transparencyColor = argb;
        this.enableTransparency = true;
        return this;
    }

    public Giffer withFrameDispose(GifGraphicControlExt.DisposeMethod dispose) {
        currentGCE.setDispose(dispose);
        return this;
    }

    public Giffer encode(OutputStream stream, boolean isComplete) {
        if (image == null) throw new GifferException("No image decoded or created to encode");
        try {
            GifEncoder.encode(image, stream, isComplete);
        } catch (IOException e) {
            throw new GifferException("Encoding issue: " + e);
        }
        return this;
    }

    public Giffer encodeFrame(OutputStream outputStream) {
        try {
            GifEncoder.encode(image.getFrames().get(image.getFrames().size() - 1), outputStream);
        } catch (IOException ioe) {
            throw new GifferException("Something with that outputstream: " + ioe);
        }
        return this;
    }

    public Giffer decode(InputStream stream) {
        image = GifDecoder.decode(stream);
        return this;
    }

    public int[] getARGBInts() {
        return image.getARGBValues(image.getFrames().size() - 1);
    }
}
