/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.image.ImageProcessor;
import com.assertthat.selenium_shutterbug.utils.web.Coordinates;
import com.assertthat.selenium_shutterbug.utils.web.ElementOutsideViewportException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.awt.image.RasterFormatException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class PageSnapshot extends Snapshot {

    PageSnapshot(WebDriver driver, Double devicePixelRatio) {
        this.driver = driver;
        this.devicePixelRatio = devicePixelRatio;
    }

    /**
     * Highlights WebElement within the page with Color.red
     * and line width 3.
     *
     * @param element WebElement to be highlighted
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlight(WebElement element) {
        try {
            highlight(element, Color.red, 3);
        } catch (RasterFormatException rfe) {
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Highlights WebElement within the page with provided color
     * and line width.
     *
     * @param element WebElement to be highlighted
     * @param color color of the line
     * @param lineWidth width of the line
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlight(WebElement element, Color color, int lineWidth) {
        try {
            image = ImageProcessor.highlight(image, new Coordinates(element, devicePixelRatio), color, lineWidth);
        } catch (RasterFormatException rfe) {
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Highlight WebElement within the page (same as in {@link #highlight(WebElement)}}
     * and adding provided text above highlighted element.
     *
     * @param element WebElement to be highlighted with Color.red
     *                and line width 3
     * @param text test to be places above highlighted element with
     *             Color.red, font "Serif", BOLD, size 20
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlightWithText(WebElement element, String text) {
        try {
            highlightWithText(element, Color.red, 3, text, Color.red, new Font("Serif", Font.BOLD, 20));
        } catch (RasterFormatException rfe) {
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Highlight WebElement within the page, same as in {@link #highlight(WebElement)}
     * but providing ability to override default color, font values.
     *
     * @param element WebElement to be highlighted
     * @param elementColor element highlight color
     * @param lineWidth line width around the element
     * @param text text to be placed above the highlighted element
     * @param textColor color of the text
     * @param textFont text font
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlightWithText(WebElement element, Color elementColor, int lineWidth, String text, Color textColor, Font textFont) {
       try {
           highlight(element, elementColor, 0);
           Coordinates coords = new Coordinates(element, devicePixelRatio);
           image = ImageProcessor.addText(image, coords.getX(), coords.getY() - textFont.getSize() / 2, text, textColor, textFont);
       } catch (RasterFormatException rfe) {
           throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
       }
        return this;
    }

    /**
     * Blur the entire page.
     *
     * @return instance of type PageSnapshot
     */
    public PageSnapshot blur() {
        image = ImageProcessor.blur(image);
        return this;
    }

    /**
     * Blur provided element within the page only.
     *
     * @param element WebElement to be blurred
     * @return instance of type PageSnapshot
     */
    public PageSnapshot blur(WebElement element) {
        try {
            image = ImageProcessor.blurArea(image, new Coordinates(element, devicePixelRatio));
        }catch(RasterFormatException rfe) {
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Makes an element withing a page 'monochrome' - applies gray-and-white filter.
     * Original colors remain on the rest of the page.
     *
     * @param element WebElement within the page to be made 'monochrome'
     * @return instance of type PageSnapshot
     */
    public PageSnapshot monochrome(WebElement element) {
        try {
            image = ImageProcessor.monochromeArea(image, new Coordinates(element,devicePixelRatio));
        } catch (RasterFormatException rfe) {
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE, rfe);
        }
        return this;
    }

    /**
     * Blurs all the page except the element provided.
     *
     * @param element WebElement to stay not blurred
     * @return instance of type PageSnapshot
     */
    public PageSnapshot blurExcept(WebElement element) {
        try{
            image = ImageProcessor.blurExceptArea(image, new Coordinates(element,devicePixelRatio));
        }catch(RasterFormatException rfe){
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE,rfe);
        }
        return this;
    }

    /**
     * Crop the image around specified element with offset.
     *
     * @param element WebElement to crop around
     * @param offsetX  offsetX around element in px
     * @param offsetY offsetY around element in px
     * @return instance of type PageSnapshot
     */
    public PageSnapshot cropAround(WebElement element, int offsetX, int offsetY) {
        try{
            image = ImageProcessor.cropAround(image, new Coordinates(element,devicePixelRatio), offsetX, offsetY);
        }catch(RasterFormatException rfe){
            throw new ElementOutsideViewportException(ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE,rfe);
        }
        return this;
    }

    @Override
    protected PageSnapshot self() {
        return this;
    }

}
