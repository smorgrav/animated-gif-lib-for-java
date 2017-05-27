package com.madgag.gif.fmsware;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class GifDecoderTest {

    @Test
    public void testDecodingGifWithDeferredClearCodesInLZWCompression() throws Exception {
        InputStream is = getClass().getResourceAsStream("/brucelee.gif");
        GifDecoder decoder = new GifDecoder(new BufferedInputStream(is));
        GifImage image = decoder.decode();

        /*BufferedImage image = null;//decoder.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", outputStream);
        byte[] actualBytes = outputStream.toByteArray();
        byte[] expectedBytes = IOUtils.readFully(getClass().getResourceAsStream("/brucelee-frame.gif"), -1, true);
        assertThat(actualBytes).isEqualTo(expectedBytes);*/
    }
}
