package com.assertthat.selenium_screenshotter.core;

import com.assertthat.selenium_screenshotter.utils.image.ImageProcessor;
import com.assertthat.selenium_screenshotter.utils.web.Browser;
import com.assertthat.selenium_screenshotter.utils.web.ScrollStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 26/06/2016.
 */
public class ScreenshotFactory {

    /**
     * Make screenshot of the viewport only.
     * To be used when screenshotting the page
     * and don't need to scroll while making screenshots (FF, IE).
     *
     * @param driver WebDriver instance
     * @return PageScreenshot instance
     */
    public static PageScreenshot page(WebDriver driver) {
        Browser browser = new Browser(driver);
        PageScreenshot pageScreenshot = new PageScreenshot(driver);
        pageScreenshot.setImage(browser.takeScreenshot());
        return pageScreenshot;
    }

    /**
     * To be used when screenshotting the page
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver WebDriver instance
     * @param scroll ScrollStrategy How you need to scroll
     * @return PageScreenshot instance
     */
    public static PageScreenshot page(WebDriver driver, ScrollStrategy scroll) {
        Browser browser = new Browser(driver);
        PageScreenshot pageScreenshot = new PageScreenshot(driver);
        switch (scroll) {
            case HORIZONTALLY:
                pageScreenshot.setImage(browser.takeScreenshotScrollHorizontally());
                break;
            case VERTICALLY:
                pageScreenshot.setImage(browser.takeScreenshotScrollVertically());
                break;
            case BOTH_DIRECTIONS:
                pageScreenshot.setImage(browser.takeScreenshotEntirePage());
        }
        return pageScreenshot;
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementScreenshot instance
     */
    public static ElementScreenshot element(WebDriver driver, WebElement element) {
        Browser browser = new Browser(driver);
        ElementScreenshot elementScreenshot = new ElementScreenshot(driver, element);
        browser.scrollToElement(element);
        elementScreenshot.setImage(browser.takeScreenshot());
        elementScreenshot.setImage(ImageProcessor.getElement(elementScreenshot.getImage(), browser.getBoundingClientRect(element)));
        return elementScreenshot;
    }
}
