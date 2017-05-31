package com.madgag.gif.fmsware;

import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
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

    @Test
    public void create_single_gif_image_from_singlecolor_argb_values() {

        // Create
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
    public void encode_decode_roundtrip_brucelee() throws IOException {
        byte[] originalfile = getByteArray(getClass().getResourceAsStream("/sonic-blue-transparent.gif"));
        GifImage image = GifDecoder.decode(new ByteArrayInputStream(originalfile));
        ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);

        GifEncoder.encode(image, baos);
        byte[] firstTripDround = baos.toByteArray();

        for (int i = 0; i < originalfile.length; i++) {
            if (originalfile[i] != firstTripDround[i]) {
                System.out.println("Difference at index: " + i + " expected " + originalfile[i] + " but got " + firstTripDround[i]);
                break;
            }
        }

        Assert.assertArrayEquals(originalfile, firstTripDround);
    }

/*    @Test
    public void testBasicOutput() throws Exception {
        buildStandardOptions(Giffer.create());
        assertEncodedImageIsEqualTo("/sonic-normal.gif");
    }

    @Test
    public void testNullBackgroundWorks() throws Exception {
        encoder.setTransparent(null);
        buildStandardOptions();

        assertEncodedImageIsEqualTo("/sonic-normal.gif");
    }

    @Test
    public void testBackgroundColorWorksOnOversizeImage() throws Exception {
        encoder.setSize(600, 600);
        encoder.setBackground(RED);
        buildStandardOptions();

        assertEncodedImageIsEqualTo("/sonic-big-and-red.gif");
    }

    @Test
    public void testTransparentColor() throws Exception {
        encoder.setTransparent(BLUE);
        buildStandardOptions();

        assertEncodedImageIsEqualTo("/sonic-blue-transparent.gif");
    }

    @Test
    public void testBackgroundAndTransparent() throws Exception {
        Giffer.create()
                .setHeight(600)
                .setWidth(600)
                .setBackground(GREEN);
        encoder.setTransparent(BLUE);
        buildStandardOptions();

        assertEncodedImageIsEqualTo("/sonic-green-bg-blue-transparent.gif");
    }

    private GifImage buildStandardOptions(Giffer giffer) {
        giffer.withLoopCount(0);
        giffer.withDelay(40);
        giffer.addFrame(sonic1);
        giffer.addFrame(sonic2);
        giffer.build();
    }*/

    private GifBitmap getImage(String name) throws IOException {
        BufferedImage image =  ImageIO.read(new File(getClass().getResource(name).getFile()));

        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage tmp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            tmp.getGraphics().drawImage(image, 0, 0, null);
            image = tmp;
        }

        int[] outPixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        return new GifBitmap(image.getWidth(), image.getHeight(), 0,0, null,  outPixels);
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
}
