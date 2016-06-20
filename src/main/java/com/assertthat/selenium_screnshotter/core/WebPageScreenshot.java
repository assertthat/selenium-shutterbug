package com.assertthat.selenium_screnshotter.core;

import java.awt.Color;
import java.awt.Font;

import com.assertthat.selenium_screnshotter.utils.image.ImageProcessor;
import com.assertthat.selenium_screnshotter.utils.web.Coordinates;
import org.openqa.selenium.*;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class WebPageScreenshot extends Screenshot<WebPageScreenshot> {

    WebPageScreenshot(WebDriver driver) {
        this.driver = driver;
    }

    public WebPageScreenshot highlight(WebElement element) {
        highlight(element, Color.red, 3);
        return this;
    }

    public WebPageScreenshot highlight(WebElement element, Color color, int lineWidth) {
        image = ImageProcessor.highlight(image, new Coordinates(element), color, lineWidth);
        return this;
    }

    public WebPageScreenshot highlightWithText(WebElement element, String text) {
        highlightWithText(element, Color.red, 3, text, Color.red, new Font("Serif", Font.BOLD, 20));
        return this;
    }

    public WebPageScreenshot highlightWithText(WebElement element, Color elementColor, int lineWidth, String text, Color textColor, Font textFont) {
        highlight(element, elementColor, 0);
        Coordinates coords = new Coordinates(element);
        image = ImageProcessor.addText(image, coords.getX(), coords.getY() - textFont.getSize() / 2, text, textColor, textFont);
        return this;
    }

    public WebPageScreenshot blur() {
        image = ImageProcessor.blur(image);
        return this;
    }

    public WebPageScreenshot blur(WebElement element) {
        image = ImageProcessor.blurArea(image, new Coordinates(element));
        return this;
    }

    public WebPageScreenshot blurExcept(WebElement element) {
        image = ImageProcessor.blurExceptArea(image, new Coordinates(element));
        return this;
    }

    @Override
    protected WebPageScreenshot self() {
        return this;
    }

}