package com.madgag.gif.fmsware;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GifferTest {

    @Test
    public void encode_decode_roundtrips() throws IOException{
        byte[] originalfile = getByteArray(getClass().getResourceAsStream("/brucelee.gif"));
        GifImage image = GifDecoder.decode(getClass().getResourceAsStream("/brucelee.gif"));
        //FileOutputStream baos = new FileOutputStream("/Users/smorgrav/dev/privat/test.gif");

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
