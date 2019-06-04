/*
 * Copyright 2016 Glib Briia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.assertthat.selenium_shutterbug.utils.image;

import com.assertthat.selenium_shutterbug.utils.web.Coordinates;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Glib_Briia, Inha Briia on 26/06/2016.
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

    @Test
    public void testImagesAreEquals() throws IOException {
        BufferedImage image1 = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("clearImage.png"));
        BufferedImage image2 = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("clearImage.png"));
        assertTrue("Images are not equal",ImageProcessor.imagesAreEquals(image1, image2, 0.0));
    }

    @Test
    public void testBlur() throws IOException {
        BufferedImage clearImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("clearImage.png"));
        BufferedImage blurredExpectedImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("blurredImage.png"));
        BufferedImage blurredActualImage = ImageProcessor.blur(clearImage);
        assertTrue("Images are not equal after blur",ImageProcessor.imagesAreEquals(blurredActualImage, blurredExpectedImage, 0.0));
    }

    @Test
    public void testImagesAreEqualsWithDeviation() throws IOException {
        double deviation = 0.2;
        BufferedImage image1 = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("clearImage.png"));
        BufferedImage image2 = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("clearImageDeviation.png"));
        assertTrue("Images are not equal with deviation: " + deviation,ImageProcessor.imagesAreEquals(image1, image2, deviation));
    }

    @Test
    public void testHighlight() throws IOException {
        Point point = new Point(9,33);
        Dimension size = new Dimension(141,17);
        Coordinates coords = new Coordinates(point, size, 1D);
        BufferedImage clearImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("clearImage.png"));
        BufferedImage highlightedExpectedImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("highlightedImage.png"));
        BufferedImage highlightedActualImage = ImageProcessor.highlight(clearImage, coords, Color.red, 3);
        assertTrue("Images are not equal after highlighting",ImageProcessor.imagesAreEquals(highlightedExpectedImage, highlightedActualImage, 0.0));
    }

    @Ignore
    @Test
    public void testAddText() throws IOException {
        BufferedImage clearImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("clearImage.png"));
        BufferedImage addedTextExpectedImage = ImageIO.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("addedTextImage.png"));
        BufferedImage addedTextActualImage = ImageProcessor.addText(clearImage, 60, 70, "ABC", Color.red, new Font("SansSerif", Font.BOLD, 20));
        assertTrue("Images are not equal after adding text",ImageProcessor.imagesAreEquals(addedTextExpectedImage, addedTextActualImage, 0.0));
    }


}
