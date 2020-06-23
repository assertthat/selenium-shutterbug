/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.web.Browser;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.function.Function;

/**
 * Created by Glib_Briia on 26/06/2016.
 */
public class Shutterbug {

    private static final int DEFAULT_SCROLL_TIMEOUT = 100;
    private static Function<WebDriver,?> beforeShootCondition;
    private static int beforeShootTimeout;

    private Shutterbug(){

    }

    /**
     * Make screen shot of the viewport only.
     * To be used when screen shooting the page
     * and don't need to scroll while making screen shots (FF, IE).
     *
     * @param driver WebDriver instance
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver) {
        return shootPage(driver, true);
    }

    /**
     * Make screen shot of the viewport only.
     * To be used when screen shooting the page
     * and don't need to scroll while making screen shots (FF, IE).
     *
     * @param driver              WebDriver instance
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        PageSnapshot pageScreenshot = new PageSnapshot(driver, browser.getDevicePixelRatio());
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
     * @param driver               WebDriver instance
     * @param scroll               ScrollStrategy How you need to scroll
     * @param betweenScrollTimeout Timeout to wait after scrolling and before taking screenshot
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, int betweenScrollTimeout) {
        return shootPage(driver, scroll, betweenScrollTimeout, true);
    }

    /**
     * Wait for condition to be true before taking screenshot
     *
     * @param cond                condition
     * @param timeout             timeout wait for condition
     * @return Shutterbug
     */
    public static Shutterbug wait(ExpectedCondition<?> cond, int timeout) {
        beforeShootCondition = cond;
        beforeShootTimeout = timeout;
        return null;
    }

    /**
     * Wait for before taking screenshot
     *
     * @param timeout             timeout wait for condition
     * @return Shutterbug
     */
    public static Shutterbug wait(int timeout) {
        beforeShootTimeout = timeout;
        return null;
    }


    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver              WebDriver instance
     * @param scroll              ScrollStrategy How you need to scroll
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, boolean useDevicePixelRatio) {
        return shootPage(driver, scroll, 0, useDevicePixelRatio);
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver               WebDriver instance
     * @param scroll               ScrollStrategy How you need to scroll
     * @param betweenScrollTimeout Timeout to wait between each scrolling operation
     * @param useDevicePixelRatio  whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, int betweenScrollTimeout, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        browser.setBetweenScrollTimeout(betweenScrollTimeout);
        if(beforeShootCondition != null) {
            browser.setBeforeShootTimeout(beforeShootTimeout);
            browser.setBeforeShootCondition(beforeShootCondition);
        }else if(beforeShootTimeout!=0){
            browser.setBeforeShootTimeout(beforeShootTimeout);
        }

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
        return shootElementVerticallyCentered(driver, element, true);
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element) {
        return shootElement(driver, element, true);
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver              WebDriver instance
     * @param element             WebElement instance to be screen shot
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElement(element);
        elementSnapshot.setImage(browser.takeScreenshot(), browser.getBoundingClientRect(element));
        return elementSnapshot;
    }

    /**
     * To be used when need to screenshot particular element by vertically centering it within viewport.
     *
     * @param driver              WebDriver instance
     * @param element             WebElement instance to be screen shot
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElementVerticallyCentered(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElementVerticalCentered(element);
        elementSnapshot.setImage(browser.takeScreenshot(), browser.getBoundingClientRect(element));
        return elementSnapshot;
    }
}
