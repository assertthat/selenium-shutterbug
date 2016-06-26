package com.assertthat.shutterbug.core;

import com.assertthat.shutterbug.utils.image.ImageProcessor;
import com.assertthat.shutterbug.utils.web.Coordinates;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class PageSnapshot extends Snapshot {

    PageSnapshot(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Highlights WebElement within the page with Color.red
     * and line width 3.
     *
     * @param element WebElement to be highlighted
     * @return instance of type PageSnapshot
     */
    public PageSnapshot highlight(WebElement element) {
        highlight(element, Color.red, 3);
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
        image = ImageProcessor.highlight(image, new Coordinates(element), color, lineWidth);
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
        highlightWithText(element, Color.red, 3, text, Color.red, new Font("Serif", Font.BOLD, 20));
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
        highlight(element, elementColor, 0);
        Coordinates coords = new Coordinates(element);
        image = ImageProcessor.addText(image, coords.getX(), coords.getY() - textFont.getSize() / 2, text, textColor, textFont);
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
        image = ImageProcessor.blurArea(image, new Coordinates(element));
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
        image = ImageProcessor.monochromeArea(image, new Coordinates(element));
        return this;
    }

    /**
     * Blurs all the page except the element provided.
     *
     * @param element WebElement to stay not blurred
     * @return instance of type PageSnapshot
     */
    public PageSnapshot blurExcept(WebElement element) {
        image = ImageProcessor.blurExceptArea(image, new Coordinates(element));
        return this;
    }

    @Override
    protected PageSnapshot self() {
        return this;
    }

}
