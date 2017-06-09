package org.smorgrav.giffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

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
 * TODO support short colortables
 *
 * @author smorgrav
 */
public class Giffer {

    private static String DEFAULT_VERSION = "GIF89a";

    private GifImage image;
    private List<GifFrame> frames = new ArrayList<>();
    private FrameBuilder defaultFrame = new FrameBuilder();
    private int width = -1;
    private int height = -1;
    private boolean enableBackground = false;
    private int backgroundIndex = 0;
    private int backgroundColor = 0;
    private int loopCount = 1;
    private boolean interlace = false;
    private int aspectRatio = 0;
    private GifColorTable globalColorTable;
    private boolean useGlobalColorTable = false;

    public class FrameBuilder {
        private int width = -1;
        private int height = -1;
        private int offsetx, offsety;
        private int[] argb;
        private GifColorTable localColorTable;
        private int transparencyColor = 0;
        private boolean userInput = false;
        private boolean enableTransparency = false;
        private boolean useGlobalColorTable = false;
        private int delay = 0;
        private GifDispose dispose = GifDispose.NON_SPECIFIED;

        public FrameBuilder() {}

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

        public FrameBuilder withGlobalColorTable(boolean useGlobalNoLocal) {
            this.useGlobalColorTable = useGlobalNoLocal;
            return this;
        }

        public Giffer build() {
            if (this.width == -1 || this.height == -1){
                throw new GifferException("You must set the width and height before you can build a frame");
            }

            // Decide color table
            if (localColorTable == null) {
                if (useGlobalColorTable) {
                    localColorTable = getGlobalColorTable();
                } else {
                    localColorTable = GifColorTable.create(argb, false);
                }
            }

            // Index raster
            int[] colorIndices = localColorTable.indexColors(argb);
            GifBitmap raster = new GifBitmap(width, height, offsetx, offsety, localColorTable, colorIndices);

            // Create control extension
            GifGraphicControlExt gce = new GifGraphicControlExt();
            gce.setDelay(delay);
            gce.setDispose(dispose);
            gce.setUserInputFlag(userInput);
            if (enableTransparency) {
                gce.setTransparcyIndex(localColorTable.findClosestIndex(transparencyColor));
                gce.setTransparent(enableTransparency);
            }

            // Add frame
            frames.add(new GifFrame(raster, gce, interlace));
            return Giffer.this;
        }

        private GifColorTable getGlobalColorTable() {
            if (globalColorTable != null) return globalColorTable;
            if (enableBackground) {
                if (argb == null) {
                    globalColorTable = new GifColorTable(new int[256], true);
                } else {
                    globalColorTable = GifColorTable.create(argb, false);
                }
                globalColorTable.setBackground(backgroundColor);
            }
            return globalColorTable;
        }
    }

    public static Giffer create() {
        return new Giffer();
    }

    protected GifImage build() {
        if (image == null) {
                if (globalColorTable == null) {
                    globalColorTable = new GifColorTable(new int[256], true);
                    if (enableBackground) {
                        backgroundIndex = globalColorTable.setBackground(backgroundColor);
                    }
                }
                image = new GifImage(DEFAULT_VERSION, width, height, globalColorTable, backgroundIndex, aspectRatio);
                image.setLoopCount(loopCount);
                for (GifFrame frame : frames) {
                    image.addFrame(frame);
                }
            }
        return image;
    }

    public FrameBuilder addFrame(int argb[], int frameWidth, int frameHeight, int offsetx, int offsety) {
        FrameBuilder frame = new FrameBuilder(defaultFrame);
        frame.width = frameWidth;
        frame.height = frameHeight;
        frame.offsetx = offsetx;
        frame.offsety = offsety;
        frame.argb = argb;

        if (width == -1 || height == -1) {
            width = frameWidth;
            height = frameHeight;
        }
        return frame;
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

    public Giffer withGlobalColorTable(boolean useGlobalNoLocal) {
        this.useGlobalColorTable = useGlobalNoLocal;
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
        if (image == null) {
            build();
        }
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
        if (image == null) {
            build();
        }
        try {
            GifEncoder.encode(image.getFrames().get(image.getFrames().size() - 1), outputStream);
        } catch (IOException ioe) {
            throw new GifferException("Something with that outputstream: " + ioe);
        }
        return this;
    }

    public int[] getARGBInts() {
        if (image == null) {
            build();
        }
        return image.getARGBValues(image.getFrames().size() - 1);
    }
}
