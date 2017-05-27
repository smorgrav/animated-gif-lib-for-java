package com.madgag.gif.fmsware;

import org.junit.Test;

public class GifDecoderTest {

    @Test
    public void testDecodingGifWithDeferredClearCodesInLZWCompression() throws Exception {
        GifImage image = GifDecoder.decode(getClass().getResourceAsStream("/brucelee.gif"));

        /*BufferedImage image = null;//decoder.getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "gif", outputStream);
        byte[] actualBytes = outputStream.toByteArray();
        byte[] expectedBytes = IOUtils.readFully(getClass().getResourceAsStream("/brucelee-frame.gif"), -1, true);
        assertThat(actualBytes).isEqualTo(expectedBytes);*/
    }
}
