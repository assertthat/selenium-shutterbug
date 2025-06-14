/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.web.Browser;
import com.assertthat.selenium_shutterbug.utils.web.Coordinates;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.function.Function;

/**
 * Created by Glib_Briia on 26/06/2016.
 */
public class Shutterbug {

    private static final int DEFAULT_SCROLL_TIMEOUT = 100;
    private static Function<WebDriver, ?> beforeShootCondition;
    private static int beforeShootTimeout;

    private Shutterbug() {

    }

    static {
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Make screenshot of the viewport only.
     * To be used when screenshotting the page
     * and don't need to scroll while making screenshots.
     *
     * @param driver WebDriver instance
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver) {
        return shootPage(driver, true);
    }

    /**
     * Make screenshot of the viewport only.
     * To be used when screenshotting the page
     * and don't need to scroll while making screenshots.
     *
     * @param driver              WebDriver instance
     * @param useDevicePixelRatio whether to account for device pixel
     *                            ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        PageSnapshot pageScreenshot = new PageSnapshot(driver, browser.getDevicePixelRatio());
        pageScreenshot.setImage(browser.takeScreenshot());
        return pageScreenshot;
    }

    /**
     * To be used when screenshotting the page
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver  WebDriver instance
     * @param capture Capture type
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, Capture capture) {
        return shootPage(driver, capture, DEFAULT_SCROLL_TIMEOUT);
    }

    /**
     * To be used when screenshotting the page
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver               WebDriver instance
     * @param capture              Capture type
     * @param betweenScrollTimeout Timeout to wait after scrolling and before taking screenshot
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, Capture capture, int betweenScrollTimeout) {
        return shootPage(driver, capture, betweenScrollTimeout, true);
    }

    /**
     * Wait for condition to be true before taking screenshot
     *
     * @param cond    condition
     * @param timeout timeout wait for condition
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
     * @param timeout timeout wait for condition
     * @return Shutterbug
     */
    public static Shutterbug wait(int timeout) {
        beforeShootTimeout = timeout;
        return null;
    }


    /**
     * To be used when screenshotting the page
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver              WebDriver instance
     * @param capture             Capture type
     * @param useDevicePixelRatio whether to account for device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, Capture capture,
                                         boolean useDevicePixelRatio) {
        return shootPage(driver, capture, 0, useDevicePixelRatio);
    }

    /**
     * To be used when screenshotting the page
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver               WebDriver instance
     * @param capture              Capture type
     * @param betweenScrollTimeout Timeout to wait between each scrolling operation
     * @param useDevicePixelRatio  whether to account for device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, Capture capture,
                                         int betweenScrollTimeout, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        browser.setBetweenScrollTimeout(betweenScrollTimeout);
        if (beforeShootCondition != null) {
            browser.setBeforeShootTimeout(beforeShootTimeout);
            browser.setBeforeShootCondition(beforeShootCondition);
        } else if (beforeShootTimeout != 0) {
            browser.setBeforeShootTimeout(beforeShootTimeout);
        }

        PageSnapshot pageScreenshot = new PageSnapshot(driver, browser.getDevicePixelRatio());
        switch (capture) {
            case VIEWPORT:
                pageScreenshot.setImage(browser.takeScreenshot());
                break;
            case FULL:
                pageScreenshot.setImage(browser.takeFullPageScreenshot());
                break;
            case VERTICAL_SCROLL:
                pageScreenshot.setImage(browser.takeFullPageVerticalScreenshotScroll(null));
                break;
            case HORIZONTAL_SCROLL:
                pageScreenshot.setImage(browser.takeFullPageHorizontalScreenshotScroll(null));
                break;
            case FULL_SCROLL:
                pageScreenshot.setImage(browser.takeFullPageScreenshotScroll(null));
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
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver,
                                               WebElement element,
                                               CaptureElement capture) {
        return shootElement(driver, element, capture, true);
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver  WebDriver instance
     * @param element By element locator
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver,
                                               By element,
                                               CaptureElement capture) {
        return shootElement(driver, element, capture, true);
    }

    /**
     * To be used when need to screenshot particular element.
     * Doesn't account for scrollable elements.
     *
     * @param driver              WebDriver instance
     * @param element             WebElement instance to be screenshot
     * @param useDevicePixelRatio whether to account for device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElement(element);
        elementSnapshot.setImage(browser.takeScreenshot(), browser.getCoordinates(element));
        return elementSnapshot;
    }

    /**
     * To be used when need to screenshot particular element.
     * Can take  screenshots of scrollable elements if Capture type is supplied.
     *
     * @param driver              WebDriver instance
     * @param element             WebElement instance to be screenshot
     * @param capture             Capture type
     * @param useDevicePixelRatio whether to account for device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver,
                                               WebElement element,
                                               CaptureElement capture,
                                               boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElement(element);
        switch (capture) {
            case VERTICAL_SCROLL:
                elementSnapshot.setImage(browser.takeFullElementVerticalScreenshotScroll(element));
                break;
            case HORIZONTAL_SCROLL:
                elementSnapshot.setImage(browser.takeFullElementHorizontalScreenshotScroll(element));
                break;
            case FULL_SCROLL:
                elementSnapshot.setImage(browser.takeFullElementScreenshotScroll(element));
                break;
            default:
                elementSnapshot.setImage(browser.takeElementViewportScreenshot(element));
        }
        return elementSnapshot;
    }

    /**
     * To be used when need to screenshot particular element.
     * Can take  screenshots of scrollable elements if Capture type is supplied.
     *
     * @param driver              WebDriver instance
     * @param by                  By element locator
     * @param capture             Capture type
     * @param useDevicePixelRatio whether to account for device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver,
                                               By by,
                                               CaptureElement capture,
                                               boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElement(by);
        switch (capture) {
            case VERTICAL_SCROLL:
                elementSnapshot.setImage(browser.takeFullElementVerticalScreenshotScroll(by));
                break;
            case HORIZONTAL_SCROLL:
                elementSnapshot.setImage(browser.takeFullElementHorizontalScreenshotScroll(by));
                break;
            case FULL_SCROLL:
                elementSnapshot.setImage(browser.takeFullElementScreenshotScroll(by));
                break;
            default:
                elementSnapshot.setImage(browser.takeElementViewportScreenshot(by));
        }
        return elementSnapshot;
    }

    /**
     * To be used when need to screenshot particular element by vertically centering it within viewport.
     *
     * @param driver              WebDriver instance
     * @param element             WebElement instance to be screenshot
     * @param useDevicePixelRatio whether to account for device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElementVerticallyCentered(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElementVerticalCentered(element);
        elementSnapshot.setImage(browser.takeScreenshot(), browser.getCoordinates(element));
        return elementSnapshot;
    }

    /**
     * To be used when screenshotting the frame
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver              WebDriver instance
     * @param capture             Capture type
     * @param useDevicePixelRatio whether to account for device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootFrame(WebDriver driver, String frameId,
                                          CaptureElement capture,
                                          boolean useDevicePixelRatio) {
        WebElement frame = driver.findElement(By.id(frameId));
        return shootFrame(driver, frame, capture, 0,
                useDevicePixelRatio);
    }

    /**
     * To be used when screenshotting the frame
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver              WebDriver instance
     * @param capture             Capture type
     * @param useDevicePixelRatio whether to account for device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootFrame(WebDriver driver, WebElement frame,
                                          CaptureElement capture,
                                          boolean useDevicePixelRatio) {
        return shootFrame(driver, frame, capture, 0,
                useDevicePixelRatio);
    }

    /**
     * To be used when screenshotting the frame
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver  WebDriver instance
     * @param capture Capture type
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootFrame(WebDriver driver, WebElement frame,
                                          CaptureElement capture) {
        return shootFrame(driver, frame, capture, 0,
                true);
    }

    /**
     * To be used when screenshotting the frame
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver               WebDriver instance
     * @param frame                Frame WebElement
     * @param capture              Capture type
     * @param betweenScrollTimeout Timeout to wait between each scrolling operation
     * @param useDevicePixelRatio  whether to account for device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootFrame(WebDriver driver, WebElement frame,
                                          CaptureElement capture,
                                          int betweenScrollTimeout, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        browser.setBetweenScrollTimeout(betweenScrollTimeout);
        browser.scrollToElement(frame);
        Coordinates coordinates = browser.getCoordinates(frame);

        Browser browserParent = new Browser(driver, useDevicePixelRatio);
        if (capture != CaptureElement.VIEWPORT &&
                (coordinates.getWidth() > browserParent.getViewportWidth() || coordinates.getHeight() > browserParent.getViewportHeight())) {
            throw new UnsupportedOperationException("Full frame screenshot is" +
                    " " +
                    "only available if WHOLE frame is fully visible in the " +
                    "viewport. Use CaptureElement.VIEWPORT in case frame is " +
                    "outside of visible viewport.");
        }
        driver.switchTo().frame(frame);

        if (beforeShootCondition != null) {
            browser.setBeforeShootTimeout(beforeShootTimeout);
            browser.setBeforeShootCondition(beforeShootCondition);
        } else if (beforeShootTimeout != 0) {
            browser.setBeforeShootTimeout(beforeShootTimeout);
        }

        PageSnapshot pageScreenshot = new PageSnapshot(driver, browser.getDevicePixelRatio());
        switch (capture) {
            case VERTICAL_SCROLL:
                pageScreenshot.setImage(browser
                        .takeFullPageVerticalScreenshotScroll(coordinates));
                break;
            case HORIZONTAL_SCROLL:
                pageScreenshot.setImage(browser
                        .takeFullPageHorizontalScreenshotScroll(coordinates));
                break;
            case FULL_SCROLL:
                pageScreenshot.setImage(browser
                        .takeFullPageScreenshotScroll(coordinates));
                break;
            default:
                pageScreenshot.setImage(browser
                        .takeFrameViewportScreenshot(coordinates));
                break;

        }
        return pageScreenshot;
    }

    /**
     * To be used when screenshotting the frame
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions.
     *
     * @param driver  WebDriver instance
     * @param frameId Id of the frame element
     * @param capture Capture type
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootFrame(WebDriver driver, String frameId,
                                          CaptureElement capture) {
        WebElement frame = driver.findElement(By.id(frameId));
        return shootFrame(driver, frame, capture, true);
    }

    /**
     * To be used when screenshotting the frame
     * Takes viewport of the frame screenshot be default
     *
     * @param driver  WebDriver instance
     * @param frameId Id of the frame element
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootFrame(WebDriver driver, String frameId) {
        WebElement frame = driver.findElement(By.id(frameId));
        return shootFrame(driver, frame, CaptureElement.VIEWPORT, true);
    }

    public static Configurations configure() {
        return new Configurations();
    }
}
