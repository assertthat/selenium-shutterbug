/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.web.Browser;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 26/06/2016.
 */
public class Shutterbug {

    private static final int DEFAULT_SCROLL_TIMEOUT = 100;

    /**
     * Make screen shot of the viewport only.
     * To be used when screen shooting the page
     * and don't need to scroll while making screen shots (FF, IE).
     *
     * @param driver WebDriver instance
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver) {
        return shootPage(driver,false);
    }

    /**
     * Make screen shot of the viewport only.
     * To be used when screen shooting the page
     * and don't need to scroll while making screen shots (FF, IE).
     *
     * @param driver WebDriver instance
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        PageSnapshot pageScreenshot = new PageSnapshot(driver,browser.getDevicePixelRatio());
        pageScreenshot.setImage(browser.takeScreenshot());
        return pageScreenshot;
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver WebDriver instance
     * @param scroll ScrollStrategy How you need to scroll
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll) {
        return shootPage(driver, scroll, DEFAULT_SCROLL_TIMEOUT);
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver WebDriver instance
     * @param scroll ScrollStrategy How you need to scroll
     * @param  scrollTimeout Timeout to wait after scrolling and before taking screen shot
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, int scrollTimeout) {
        return shootPage(driver,scroll,scrollTimeout,false);
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver WebDriver instance
     * @param scroll ScrollStrategy How you need to scroll
     * @param  useDevicePixelRatio whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, boolean useDevicePixelRatio) {
        return shootPage(driver,scroll,0,useDevicePixelRatio);
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver WebDriver instance
     * @param scroll ScrollStrategy How you need to scroll
     * @param  scrollTimeout Timeout to wait after scrolling and before taking screen shot
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, int scrollTimeout, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        browser.setScrollTimeout(scrollTimeout);

        PageSnapshot pageScreenshot = new PageSnapshot(driver, browser.getDevicePixelRatio());
        switch (scroll) {
            case VIEWPORT_ONLY:
                pageScreenshot.setImage(browser.takeScreenshot());
                break;
            case WHOLE_PAGE:
                pageScreenshot.setImage(browser.takeScreenshotEntirePage());
                break;
        }
        return pageScreenshot;
    }

    /**
     * To be used when need to screenshot particular element by vertically centering it in viewport.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElementVerticallyCentered(WebDriver driver, WebElement element) {
        return shootElementVerticallyCentered(driver,element,false);
    }
    
    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element) {
        return shootElement(driver,element,false);
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screen shot
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElement(element);
        elementSnapshot.setImage(browser.takeScreenshot(),browser.getBoundingClientRect(element));
        return elementSnapshot;
    }
    
    /**
     * To be used when need to screenshot particular element by vertically centering it within viewport.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screen shot
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * 
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElementVerticallyCentered(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElementVerticalCentered(element);
        elementSnapshot.setImage(browser.takeScreenshot(),browser.getBoundingClientRect(element));
        return elementSnapshot;
    }
}
