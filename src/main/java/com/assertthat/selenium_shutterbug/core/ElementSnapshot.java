/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.image.ImageProcessor;
import com.assertthat.selenium_shutterbug.utils.web.Coordinates;
import com.assertthat.selenium_shutterbug.utils.web.ElementOutsideViewportException;
import org.openqa.selenium.WebDriver;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class ElementSnapshot extends Snapshot {

    ElementSnapshot(WebDriver driver, Double devicePixelRatio) {
        this.driver = driver;
        this.devicePixelRatio = devicePixelRatio;
    }

    protected void setImage(BufferedImage image, Coordinates coords) {
        try {
            self().image = ImageProcessor.getElement(image, coords);
        } catch (RasterFormatException rfe) {
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
    }

    @Override
    protected ElementSnapshot self() {
        return this;
    }
}
