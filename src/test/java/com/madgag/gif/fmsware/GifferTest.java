package com.madgag.gif.fmsware;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

/**
 * Test the creation and composition of GifImages with various options.
 *
 * Encoding and decoding is tested implicitly.
 */
public class GifferTest {

    static int RED = 0xffff0000;
    static int GREEN = 0xff00ff00;
    static int BLUE = 0xff0000ff;

    ByteArrayOutputStream baos;
    int[] sonic1, sonic2;

    @Before
    public void setup() throws IOException {
        baos = new ByteArrayOutputStream(10000);
        sonic1 = getImage("/sonic1.png");
        sonic2 = getImage("/sonic2.png");
    }

    @Test
    public void encode_decode_roundtrips() throws IOException {
        roundtrip("/sonic-blue-transparent.gif");
        roundtrip("/sonic-big-and-red.gif");
        roundtrip("/sonic-green-bg-blue-transparent.gif");
        roundtrip("/sonic-normal.gif");
        // TODO check if writing this out roundtrip("/brucelee.gif");
        // TODO check if writing this out roundtrip("/brucelee-frame.gif");
    }

    @Test
    public void create_single_gif_image_from_singlecolor_argb_values() {
        int red = 0xffff0000;
        int[] argb = new int[] { red, red, red, red, red, red, red, red, red};
        GifImage image = Giffer.create().addFrame(argb, 3, 3).build();

        // Fetch rendered raster
        int[] rendered = image.getARGBValues(0);

        // Test raster and colortable
        assertEquals(1, image.getFrames().size());
        assertEquals(image.getFrames().get(0).getBitmap().getColorTable().getColor(0), red);
        assertEquals(argb.length, rendered.length);
        for (int i = 0; i < argb.length; i++) {
            assertEquals("Diff in pos: " + i, argb[i], rendered[i]);
        }
    }

    @Test
    public void testBasicOutput() throws Exception {
        Giffer.create()
                .withLoopCount(0)
                .withDelay(40)
                .addFrame(sonic1, 290, 360)
                .addFrame(sonic2)
                .encodeTo(baos);

        compare("/sonic-normal.gif", baos.toByteArray());
    }

    @Test
    public void testBackgroundColorWorksOnOversizeImage() throws Exception {
        Giffer.create()
                .withLoopCount(0)
                .withDelay(40)
                .withWidth(600)
                .withHeight(600)
                .withBackground(RED)
                .addFrame(sonic1, 290, 360)
                .addFrame(sonic2, 290, 360)
                .encodeTo(baos);

        compare("/sonic-big-and-red.gif", baos.toByteArray());
    }

    @Test
    public void testTransparentColor() throws Exception {
        Giffer.create()
                .withLoopCount(0)
                .withDelay(40)
                .addFrame(sonic1, 290, 360)
                .withTransparency(BLUE)
                .addFrame(sonic2, 290, 360)
                .encodeTo(baos);

        compare("/sonic-blue-transparent.gif", baos.toByteArray());
    }

    @Test
    public void testBackgroundAndTransparent() throws Exception {
        Giffer.create()
                .withLoopCount(0)
                .withDelay(40)
                .withHeight(600)
                .withWidth(600)
                .withBackground(GREEN)
                .withTransparency(BLUE)
                .addFrame(sonic1, 290, 360)
                .addFrame(sonic2, 290, 360)
                .encodeTo(baos);

        compare("/sonic-green-bg-blue-transparent.gif", baos.toByteArray());
    }

    private int[] getImage(String name) throws IOException {
        BufferedImage image =  ImageIO.read(new File(getClass().getResource(name).getFile()));

        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            tmp.getGraphics().drawImage(image, 0, 0, null);
            image = tmp;
        }

        return ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
    }

    private byte[] getByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        is.close();
        buffer.flush();
        return buffer.toByteArray();
    }

    private void roundtrip(String filename) throws IOException {
        Giffer.create()
                .decodeFrom(getClass().getResourceAsStream(filename))
                .encodeTo(baos);
        compare(filename, baos.toByteArray());
    }

    private void compare(String expectedFile, byte[] actualArray) throws IOException {
        byte[] expectedArray = getByteArray(getClass().getResourceAsStream(expectedFile));

        for (int i = 0; i < actualArray.length; i++) {
            if (expectedArray[i] != actualArray[i]) {
                System.out.println("Difference at index: " + i + " expected " + expectedArray[i] + " but got " + actualArray[i]);
                break;
            }
        }

        Assert.assertArrayEquals(expectedArray, actualArray);
    }
}
