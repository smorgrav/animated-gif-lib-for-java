package com.madgag.gif.fmsware;

/**
 * @author smorgrav
 */
public class Giffer {

  /*  private int width; // image size
    private int height;
    private Color transparent = null; // transparent color if given
    private Color background = null;  // background color if given
    private int transIndex; // transparent index in color table
    private int repeat = -1; // no repeat
    private int delay = 0; // frame delay (hundredths)
    private boolean started = false; // ready to output frames
    private OutputStream out;
    private Bitmap image; // current frame
    private byte[] pixels; // BGR byte array from frame
    private byte[] indexedPixels; // converted frame indexed to palette
    private int colorDepth; // number of bit planes
    private byte[] colorTab; // RGB palette
    private final boolean[] usedEntry = new boolean[256]; // active palette entries
    private int palSize = 7; // color table size (bits-1)
    private int dispose = -1; // disposal code (-1 = use default)
    private boolean closeStream = false; // close stream when finished
    private boolean firstFrame = true;
    private boolean sizeSet = false; // if false, get size from first frame
    private int sample = 10; // default sample interval for quantizer


    *//**
     * Analyzes image colors and creates color map.
     *//*
    private static void analyzePixels() {
        int len = pixels.length;
        int nPix = len / 3;
        indexedPixels = new byte[nPix];
        NeuQuant nq = new NeuQuant(pixels, len, sample);
        // initialize quantizer
        colorTab = nq.process(); // create reduced palette
        // convert map from BGR to RGB
        for (int i = 0; i < colorTab.length; i += 3) {
            byte temp = colorTab[i];
            colorTab[i] = colorTab[i + 2];
            colorTab[i + 2] = temp;
            usedEntry[i / 3] = false;
        }
        // map image pixels to new palette
        int k = 0;
        for (int i = 0; i < nPix; i++) {
            int index =
                    nq.map(pixels[k++] & 0xff,
                            pixels[k++] & 0xff,
                            pixels[k++] & 0xff);
            usedEntry[index] = true;
            indexedPixels[i] = (byte) index;
        }
        pixels = null;
        colorDepth = 8;
        palSize = 7;
        // get closest match to transparent color if specified
        if (transparent != null) {
            transIndex = findClosest(transparent);

        }
    }

    *//**
     * Returns index of palette color closest to c
     *//*
    private int findClosest(Color c) {
        if (colorTab == null) return -1;
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        int minpos = 0;
        int dmin = 256 * 256 * 256;
        int len = colorTab.length;
        for (int i = 0; i < len; ) {
            int dr = r - (colorTab[i++] & 0xff);
            int dg = g - (colorTab[i++] & 0xff);
            int db = b - (colorTab[i] & 0xff);
            int d = dr * dr + dg * dg + db * db;
            int index = i / 3;
            if (usedEntry[index] && (d < dmin)) {
                dmin = d;
                minpos = index;
            }
            i++;
        }
        return minpos;
    }

    *//**
     * Sets frame rate in frames per second.  Equivalent to
     * <code>setDelay(1000/fps)</code>.
     *
     * @param fps float frame rate (frames per second)
     *//*
    public void setFrameRate(float fps) {
        if (fps != 0f) {
            delay = Math.round(100f / fps);
        }
    }

    *//**
     * Sets quality of color quantization (conversion of images
     * to the maximum 256 colors allowed by the GIF specification).
     * Lower values (minimum = 1) produce better colors, but slow
     * processing significantly.  10 is the default, and produces
     * good color mapping at reasonable speeds.  Values greater
     * than 20 do not yield significant improvements in speed.
     *
     * @param quality int greater than 0.
     * @return
     *//*
    public void setQuality(int quality) {
        if (quality < 1) quality = 1;
        sample = quality;
    }


    *//**
     * Sets the GIF frame size.  The default size is the
     * size of the first frame added if this method is
     * not invoked.
     *
     * @param w int frame width.
     * @param h int frame width.
     *//*
    public void setSize(int w, int h) {
        if (started && !firstFrame) return;
        width = w;
        height = h;
        if (width < 1) width = 320;
        if (height < 1) height = 240;
        sizeSet = true;
    }

    GifFrame newFrame(GifGraphicControlExt gce, GifColorTable lct, boolean interlace) {
        GifFrame currentFrame = frames.isEmpty() ? null : frames.get(frames.size() - 1);
        GifFrame newFrame = new GifFrame(new Bitmap(width, height), lct == null ? gct : lct, gce, interlace);

        *//**
         * The can be dependent of the previous frame - given by the dispose code
         *//*
        if (currentFrame != null) {
            switch (currentFrame.getDisposeMethod()) {
                case RESTORE_TO_BACKGROUND:
                    newFrame.fillWithBackgroundColor();
                    break;
                case NON_SPECIFIED:
                case RESTORE_TO_PREVIOUS: //TODO this is perhaps someting different
                case DO_NOT_DISPOSE:
                    newFrame.draw(currentFrame.getBitmap());
                    break;
                default:
                    throw new GifFormatException("Unknown dispose method: " + gce.getDispose());
            }
        }

        frames.add(newFrame);
        return newFrame;
    }*/
}
