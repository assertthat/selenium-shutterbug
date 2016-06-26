package com.assertthat.selenium_screenshotter.utils.image

import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel

/**
 * Created by Glib_Briia on 24/06/2016.
 */
class ImageProcessorTest extends GroovyTestCase {

    void setUp() {

    }

    void tearDown() {

    }

    void testThrowExceptionIfSizeNotMatch() {
        def width = 300
        def height = 300
        def msg = shouldFail(UnableToCompareImagesException) {
            ImageProcessor.imagesAreEquals(new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
                    , new BufferedImage(width,height+1,BufferedImage.TYPE_INT_ARGB),0.0)
        }
        assert 'Images dimensions mismatch: image1 - '+width+'x'+height+'; image2 - '+width+'x'+(height+1) == msg

    }

    void testImagesShouldBeEqualWhenSizeMatch() {
        def width = 300
        def height = 300
        assert ImageProcessor.imagesAreEquals(new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB)
                , new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB),0.0)

    }
}
