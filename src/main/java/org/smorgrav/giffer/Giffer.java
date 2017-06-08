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
 *
 * @author smorgrav
 */
public class Giffer {

    private static String DEFAULT_VERSION = "GIF89a";

    private GifImage image;
    private FrameBuilder defaultFrame = new FrameBuilder();
    private int width = -1;
    private int height = -1;
    private boolean enableBackground = false;
    private int backgroundIndex = 0;
    private int backgroundColor = 0;
    private int loopCount = 1;
    private boolean interlace = false;
    private int aspectRatio = 0;

    public class FrameBuilder {
        private int width = -1;
        private int height = -1;
        private int offsetx, offsety;
        private int[] argb;
        private int[] colorTable;
        private int transparencyColor = 0;
        private boolean userInput = false;
        private boolean enableTransparency = false;
        private boolean useGlobalColorTable = false;
        private int delay = 0;
        private GifDispose dispose = GifDispose.NON_SPECIFIED;

        public FrameBuilder() {}

        public FrameBuilder(int width, int height, int offsetx, int offsety) {
            this.width = width;
            this.height = height;
            this.offsetx = offsetx;
            this.offsety = offsety;
        }

        public FrameBuilder(FrameBuilder source) {
            this.width = source.width;
            this.height = source.height;
            this.offsetx = source.offsetx;
            this.offsety = source.offsety;
            this.argb = source.argb;
            this.transparencyColor = source.transparencyColor;
            this.userInput = source.userInput;
            this.enableTransparency = source.enableTransparency;
            this.delay = source.delay;
            this.dispose = source.dispose;
            this.useGlobalColorTable = source.useGlobalColorTable;
        }

        public FrameBuilder withDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public FrameBuilder withUserInput(boolean userInput) {
            this.userInput = userInput;
            return this;
        }

        public FrameBuilder withTransparency(int argb) {
            transparencyColor = argb;
            this.enableTransparency = true;
            return this;
        }

        public FrameBuilder withoutTransparency() {
            this.enableTransparency = false;
            return this;
        }

        public FrameBuilder withDispose(GifDispose dispose) {
            this.dispose = dispose;
            return this;
        }

        public FrameBuilder withGlobalColorTable() {
            this.useGlobalColorTable = true;
            return this;
        }


        public Giffer build() {
            if (this.width == -1 || this.height == -1){
                throw new GifferException("You must set the image width and height before adding sub images");
            }

            if (colorTable != null) {

            } else {

            }
            GifColorTable colorTable = GifColorTable.create(argb, false);
            int[] colorIndices = colorTable.indexColors(argb);

            GifGraphicControlExt currentGCE = new GifGraphicControlExt();
            currentGCE.setDelay(delay);
            currentGCE.setDispose(dispose);
            currentGCE.setUserInputFlag(userInput);
            if (enableTransparency) {
                currentGCE.setTransparcyIndex(colorTable.findClosestIndex(transparencyColor));
                currentGCE.setTransparent(enableTransparency);
            }

            if (image == null) {
                GifColorTable gct;
                //
                // Decide on global colortable, abd background index on first frame
                //
                if (enableBackground) {
                    GifBitmap backgroundMap = new GifBitmap(this.width, this.height, 0, 0, colorTable, colorIndices);
                    int[] finalFirst = new int[this.width * this.height];
                    backgroundMap.renderWithColorTo(finalFirst,width,backgroundColor);
                    GifBitmap firstFrameMap = new GifBitmap(subWidth, subHeight, offsetx, offsety, colorTable, colorIndices);

                    firstFrameMap.renderTo(finalFirst, this.width, currentGCE);
                    gct = GifColorTable.create(finalFirst, true);
                    backgroundIndex = gct.findClosestIndex(backgroundColor);
                } else {
                    gct = GifColorTable.create(argb, true);
                }

                image = new GifImage(DEFAULT_VERSION, width, height, gct, backgroundIndex, aspectRatio);
                image.setLoopCount(loopCount);
            }

            image.addFrame(argb, subWidth, subHeight, offsetx, offsety, colorTable, currentGCE, interlace);
            return Giffer.this;
        }
    }

    public static Giffer create() {
        return new Giffer();
    }

    protected GifImage build() {
        return image;
    }

    public FrameBuilder addFrame(int argb[], int subWidth, int subHeight, int offsetx, int offsety) {
        if (this.width == -1 && offsetx == 0 && this.height == -1 && offsety == 0) {
            this.width = subWidth;
            this.height = subHeight;
        } else if (this.width == -1 || this.height == -1){
            throw new GifferException("You must set the image width and height before adding sub images");
        }

        FrameBuilder frame = new FrameBuilder(defaultFrame);
        frame.width = subWidth;
        frame.height = subHeight;
        frame.offsetx = offsetx;
        frame.offsety = offsety;
        return frame;

        // Convert from argb values to indexed color raster
     }

    public FrameBuilder addFrame(int argb[]) {
        return addFrame(argb, width, height, 0, 0);
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

    public Giffer withInterlace(boolean interlace) {
        this.interlace = interlace;
        return this;
    }

    public Giffer withPixelAspectRatio(int ratio) {
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

    public Giffer withDelay(int delay) {
        this.defaultFrame.withDelay(delay);
        return this;
    }

    public Giffer withUserInput(boolean userInput) {
        this.defaultFrame.withUserInput(userInput);
        return this;
    }

    public Giffer withTransparency(int argb) {
        this.defaultFrame.withTransparency(argb);
        return this;
    }

    public Giffer withDispose(GifDispose dispose) {
        this.defaultFrame.withDispose(dispose);
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


    public Giffer decode(InputStream stream) {
        image = GifDecoder.decode(stream);
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

    public int[] getARGBInts() {
        return image.getARGBValues(image.getFrames().size() - 1);
    }
}
