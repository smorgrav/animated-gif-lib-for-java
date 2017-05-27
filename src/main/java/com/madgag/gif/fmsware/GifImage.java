package com.madgag.gif.fmsware;

import java.util.ArrayList;
import java.util.List;

/**
 * The GIF image complete with metadata and frames.
 * <p>
 * This should hold enough information to encode a valid gif image.
 *
 * @author smorgrav
 */
class GifImage {
    final String version;
    final int width;
    final int height;
    final GifColorTable gct;

    // Mutable data
    int loopCount = 1;
    final List<GifFrame> frames = new ArrayList<>();

    GifImage(String version, int width, int height, GifColorTable globalColorTable) {
        this.version = version;
        this.width = width;
        this.height = height;
        this.gct = globalColorTable;
    }

    GifFrame newFrame(GifGraphicControlExt gce, GifColorTable lct, boolean interlace) {
        GifFrame currentFrame = frames.isEmpty() ? null : frames.get(frames.size() - 1);
        GifFrame newFrame = new GifFrame(new Bitmap(width, height), lct == null ? gct : lct, gce, interlace);

        /**
         * The can be dependent of the previous frame - given by the dispose code
         */
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
    }
}