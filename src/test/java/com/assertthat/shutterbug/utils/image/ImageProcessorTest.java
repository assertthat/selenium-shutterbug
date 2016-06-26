package com.assertthat.shutterbug.utils.image;

import org.junit.Test;

import java.awt.image.BufferedImage;

import static org.junit.Assert.*;

/**
 * Created by Glib_Briia on 26/06/2016.
 */
public class ImageProcessorTest {

    @Test(expected=UnableToCompareImagesException.class)
    public void testThrowExceptionIfSizeNotMatch() {
            int width = 300;
            int height = 300;
            ImageProcessor.imagesAreEquals(new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
                        , new BufferedImage(width,height+1,BufferedImage.TYPE_INT_ARGB),0.0);
    }

    @Test
    public void testImagesShouldBeEqualWhenSizeMatch(){
        int width = 300;
        int height = 300;
        boolean imagesAreEqual = ImageProcessor.imagesAreEquals(new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
                , new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB),0.0);
        assertTrue("Images are not equal when size match", imagesAreEqual);
    }
}
