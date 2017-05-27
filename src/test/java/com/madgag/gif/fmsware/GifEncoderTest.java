package com.madgag.gif.fmsware;

import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.madgag.gif.fmsware.Color.BLUE;
import static com.madgag.gif.fmsware.Color.GREEN;
import static com.madgag.gif.fmsware.Color.RED;
import static org.fest.assertions.api.Assertions.assertThat;

public class GifEncoderTest {

    private ByteArrayOutputStream outputStream;
    private GifEncoder encoder;
    private Bitmap sonic1;
    private Bitmap sonic2;

    @Before
    public void setUp() throws IOException {
        sonic1 = getImage("/sonic1.png");
        sonic2 = getImage("/sonic2.png");

        outputStream = new ByteArrayOutputStream();
        encoder = new GifEncoder();
        encoder.start(outputStream);
    }

    @Test
    public void testBasicOutput() throws Exception {
        encodeSampleSonicFrames();

        assertEncodedImageIsEqualTo("/sonic-normal.gif");
    }

    @Test
    public void testNullBackgroundWorks() throws Exception {
        encoder.setTransparent(null);
        encodeSampleSonicFrames();

        assertEncodedImageIsEqualTo("/sonic-normal.gif");
    }

    @Test
    public void testBackgroundColorWorksOnOversizeImage() throws Exception {
        encoder.setSize(600, 600);
        encoder.setBackground(RED);
        encodeSampleSonicFrames();

        assertEncodedImageIsEqualTo("/sonic-big-and-red.gif");
    }

    @Test
    public void testTransparentColor() throws Exception {
        encoder.setTransparent(BLUE);
        encodeSampleSonicFrames();

        assertEncodedImageIsEqualTo("/sonic-blue-transparent.gif");
    }

    @Test
    public void testBackgroundAndTransparent() throws Exception {
        encoder.setSize(600, 600);
        encoder.setBackground(GREEN);
        encoder.setTransparent(BLUE);
        encodeSampleSonicFrames();

        assertEncodedImageIsEqualTo("/sonic-green-bg-blue-transparent.gif");
    }

    private void encodeSampleSonicFrames() {
        encoder.setRepeat(0);
        encoder.setDelay(400);
        encoder.addFrame(sonic1);
        encoder.addFrame(sonic2);
        encoder.finish();
    }

    private void assertEncodedImageIsEqualTo(String name) throws IOException {
        byte[] expectedBytes = getExpectedBytes(name);
        byte[] actualBytes = outputStream.toByteArray();

        assertThat(actualBytes.length).isEqualTo(expectedBytes.length);
        assertThat(actualBytes).isEqualTo(expectedBytes);
    }

    private byte[] getExpectedBytes(String name) throws IOException {
        File expectedFile = new File(getClass().getResource(name).getFile());
        byte[] expectedBytes = new byte[(int) expectedFile.length()];
        FileInputStream inputStream = new FileInputStream(expectedFile);
        int readBytes = inputStream.read(expectedBytes);

        assertThat(readBytes).isGreaterThan(0);
        assertThat(readBytes).isEqualTo(expectedBytes.length);

        return expectedBytes;
    }

    private Bitmap getImage(String name) throws IOException {
        BufferedImage image =  ImageIO.read(new File(getClass().getResource(name).getFile()));

        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            tmp.getGraphics().drawImage(image, 0, 0, null);
            image = tmp;
        }

        int[] outPixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        return new Bitmap(image.getWidth(), image.getHeight(), 0,0, outPixels);
    }
}
