/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.web;

import com.assertthat.selenium_shutterbug.utils.file.FileUtil;
import com.github.zafarkhaja.semver.Version;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.TracedCommandExecutor;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.support.ui.FluentWait;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by GlibBriia on 17/06/2016.
 */
public class Browser {

    private static final String RELATIVE_COORDS_JS = "js/relative-element-coords.js";
    private static final String MAX_DOC_WIDTH_JS = "js/max-document-width.js";
    private static final String MAX_DOC_HEIGHT_JS = "js/max-document-height.js";
    private static final String VIEWPORT_HEIGHT_JS = "js/viewport-height.js";
    private static final String VIEWPORT_WIDTH_JS = "js/viewport-width.js";
    private static final String SCROLL_TO_JS = "js/scroll-to.js";
    private static final String SCROLL_BY_JS = "js/scroll-by.js";
    private static final String SCROLL_ELEMENT = "js/scroll-element.js";
    private static final String SCROLL_INTO_VIEW_JS = "js/scroll-element-into-view.js";
    private static final String SCROLL_INTO_VIEW_VERTICAL_CENTERED_JS = "js/scroll-element-into-view-vertical-centered.js";
    private static final String CURRENT_SCROLL_Y_JS = "js/get-current-scrollY.js";
    private static final String CURRENT_SCROLL_X_JS = "js/get-current-scrollX.js";
    private static final String DEVICE_PIXEL_RATIO = "js/get-device-pixel-ratio.js";
    private static final String DOC_SCROLL_BAR_WIDTH = "js/doc-scrollbar-width.js";
    private static final String ELEMENT_SCROLL_BAR_HEIGHT = "js/element" +
            "-scrollbar-height.js";
    private static final String ELEMENT_SCROLL_BAR_WIDTH = "js/element" +
            "-scrollbar" +
            "-width" +
            ".js";
    private static final String ALL_METRICS = "js/all-metrics.js";
    private static final String ELEMENT_CURRENT_SCROLL_X_JS = "js/get-current" +
            "-element-scrollX.js";
    private static final String ELEMENT_CURRENT_SCROLL_Y_JS = "js/get-current" +
            "-element-scrollY.js";

    private WebDriver driver;
    private int docHeight = -1;
    private int docWidth = -1;
    private int viewportWidth = -1;
    private int viewportHeight = -1;
    private int betweenScrollTimeout;
    private Function<WebDriver, ?> beforeShootCondition;
    private int beforeShootTimeout;

    private Double devicePixelRatio = 1.0;

    public Browser(WebDriver driver, boolean useDevicePixelRatio) {
        this.driver = driver;
        if (useDevicePixelRatio) {
            Object devicePixelRatio = executeJsScript(DEVICE_PIXEL_RATIO);
            this.devicePixelRatio = devicePixelRatio instanceof Double ? (Double) devicePixelRatio : (Long) devicePixelRatio * 1.0;
        }
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new UnableTakeSnapshotException(e);
        }
    }

    public Double getDevicePixelRatio() {
        return devicePixelRatio;
    }

    public void wait(Function<WebDriver, ?> condition, int timeout) {
        if (condition != null) {
            new FluentWait<>(driver)
                    .withTimeout(Duration.ofSeconds(timeout))
                    .ignoring(StaleElementReferenceException.class, NoSuchMethodException.class)
                    .until(condition);
        } else if (timeout > 0) {
            wait(timeout);
        }
    }

    public void setBetweenScrollTimeout(int betweenScrollTimeout) {
        this.betweenScrollTimeout = betweenScrollTimeout;
    }

    public void setBeforeShootTimeout(int beforeShootTimeout) {
        this.beforeShootTimeout = beforeShootTimeout;
    }

    public void setBeforeShootCondition(Function<WebDriver, ?> beforeShootCondition) {
        this.beforeShootCondition = beforeShootCondition;
    }

    public BufferedImage takeScreenshot() {
        wait(beforeShootCondition, beforeShootTimeout);
        File srcFile = ((TakesScreenshot) this.getUnderlyingDriver()).getScreenshotAs(OutputType.FILE);
        try {
            return ImageIO.read(srcFile);
        } catch (IOException e) {
            throw new UnableTakeSnapshotException(e);
        } finally {
            // add this to clean up leaving this file in the temporary directory forever...
            if (srcFile.exists()) {
                srcFile.delete();
            }
        }

    }

    /**
     * Using different capture type dependently on driver:
     * for  chrome - chrome command will be used
     * for firefox - geckodriver endpoint will be used if available
     * for others - their default screenshot methods
     *
     * @return BufferedImage resulting image
     */
    public BufferedImage takeFullPageScreenshot() {
        driver = unwrapDriver();
        if (driver instanceof ChromeDriver || driver instanceof EdgeDriver) {
            return takeFullPageScreenshotChromeCommand();
        } else if (driver instanceof FirefoxDriver) {
            return takeFullPageScreenshotGeckoDriver();
        } else if (driver instanceof RemoteWebDriver) {
            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("chrome")
                    || ((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("MicrosoftEdge")
                    || ((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("msedge")) {
                return takeFullPageScreenshotChromeCommand();
            } else if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("firefox")) {
                return takeFullPageScreenshotGeckoDriver();
            }
        }
        return takeFullPageScreenshotScroll(null);
    }

    /**
     * Using different capture type dependently on driver:
     * for  chrome - chrome command will be used
     * for firefox - geckodriver endpoint will be used if available
     * for others - their default screenshot methods
     *
     * @return BufferedImage resulting image
     */
    public BufferedImage takeFullPageElementScreenshot() {
        driver = unwrapDriver();
        if (driver instanceof ChromeDriver || driver instanceof EdgeDriver) {
            return takeFullPageScreenshotChromeCommand();
        } else if (driver instanceof FirefoxDriver) {
            return takeFullPageScreenshotGeckoDriver();
        } else if (driver instanceof RemoteWebDriver) {
            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("chrome") || ((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("MicrosoftEdge")) {
                return takeFullPageScreenshotChromeCommand();
            } else if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("firefox")) {
                return takeFullPageScreenshotGeckoDriver();
            }
        }
        throw new UnsupportedOperationException("Full scrollable element " +
                "screenshot is " +
                "supported in Chrome, Firefox and MicrosoftEdge browsers only" +
                ".");
    }

    private WebDriver unwrapDriver() {
        String[] wrapperClassNames = {"org.openqa.selenium.WrapsDriver", "org.openqa.selenium.internal.WrapsDriver"};
        for (String wrapperClassName : wrapperClassNames) {
            try {
                Class<?> clazz = Class.forName(wrapperClassName);
                if (clazz.isInstance(driver)) {
                    return (WebDriver) clazz.getMethod("getWrappedDriver").invoke(driver);
                }
            } catch (ReflectiveOperationException e) {
                // NOP
            }
        }
        return driver;
    }

    public BufferedImage takeFullPageScreenshotScroll(Coordinates coordinates) {
        final int docWidth = this.getDocWidth();
        final int docHeight = this.getDocHeight();
        BufferedImage combinedImage = new BufferedImage(docWidth, docHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int viewportWidth = this.getViewportWidth();
        int viewportHeight = this.getViewportHeight();
        final int scrollBarMaxWidth = getDocScrollBarWidth();

        if (viewportWidth < docWidth || (viewportHeight < docHeight && viewportWidth - scrollBarMaxWidth < docWidth))
            viewportHeight -= scrollBarMaxWidth;
        if (viewportHeight < docHeight)
            viewportWidth -= scrollBarMaxWidth;

        int horizontalIterations = (int) Math.ceil(((double) docWidth) / viewportWidth);
        int verticalIterations = (int) Math.ceil(((double) docHeight) / viewportHeight);
        wait(beforeShootCondition, beforeShootTimeout);
        outer_loop:
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(0, j * viewportHeight);
            for (int i = 0; i < horizontalIterations; i++) {
                this.scrollTo(i * viewportWidth, viewportHeight * j);
                wait(betweenScrollTimeout);
                BufferedImage image = takeScreenshot();
                if (coordinates != null) {
                    image = image.getSubimage(coordinates.getX(),
                            coordinates.getY(),
                            coordinates.getWidth(), coordinates.getHeight());
                }
                g.drawImage(image, this.getCurrentScrollX(),
                        this.getCurrentScrollY(), null);
                if (docWidth == image.getWidth(null) && docHeight == image.getHeight(null)) {
                    break outer_loop;
                }
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullPageHorizontalScreenshotScroll(Coordinates coordinates) {
        final int docWidth = this.getDocWidth();
        final int docHeight = this.getDocHeight();
        int viewportWidth = this.getViewportWidth();
        int viewportHeight = this.getViewportHeight();
        final int scrollBarMaxWidth = getDocScrollBarWidth();

        if (viewportWidth < docWidth || (viewportHeight < docHeight && viewportWidth - scrollBarMaxWidth < docWidth)) {
            viewportHeight -= scrollBarMaxWidth;
        }
        if (viewportHeight < docHeight) {
            viewportWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(docWidth, viewportHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int horizontalIterations =
                (int) Math.ceil(((double) docWidth) / viewportWidth);
        for (int i = 0; i < horizontalIterations; i++) {
            this.scrollTo(i * viewportWidth, getCurrentScrollY());
            wait(betweenScrollTimeout);
            BufferedImage image = takeScreenshot();
            if (coordinates != null) {
                image = image.getSubimage(coordinates.getX(),
                        coordinates.getY(),
                        coordinates.getWidth(), coordinates.getHeight());
            }
            g.drawImage(image, i * viewportWidth, 0, null);
            if (this.getDocWidth() == image.getWidth(null)) {
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullPageVerticalScreenshotScroll(Coordinates coordinates) {
        final int docWidth = this.getDocWidth();
        final int docHeight = this.getDocHeight();
        int viewportWidth = this.getViewportWidth();
        int viewportHeight = this.getViewportHeight();
        final int scrollBarMaxWidth = getDocScrollBarWidth();

        if (viewportWidth < docWidth || (viewportHeight < docHeight && viewportWidth - scrollBarMaxWidth < docWidth)) {
            viewportHeight -= scrollBarMaxWidth;
        }
        if (viewportHeight < docHeight) {
            viewportWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(viewportWidth, docHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int verticalIterations =
                (int) Math.ceil(((double) docHeight) / viewportHeight);
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(getCurrentScrollX(), j * viewportHeight);
            wait(betweenScrollTimeout);
            BufferedImage image = takeScreenshot();
            if (coordinates != null) {
                image = image.getSubimage(coordinates.getX(),
                        coordinates.getY(),
                        coordinates.getWidth(), coordinates.getHeight());
            }
            g.drawImage(image, 0, j * viewportHeight, null);
            if (this.getDocHeight() == image.getHeight(null)) {
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullElementScreenshotScroll(WebElement element) {
        Coordinates coordinates = getCoordinates(element);
        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);


        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(scrollableWidth,
                scrollableHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        int horizontalIterations =
                (int) Math.ceil((double) scrollableWidth / elementWidth);
        int verticalIterations = (int) Math.ceil((double) scrollableHeight / elementHeight);
        wait(beforeShootCondition, beforeShootTimeout);
        outer_loop:
        for (int j = 0; j < verticalIterations; j++) {

            this.scrollElement(element, 0,
                    j *
                            elementHeight);
            for (int i = 0; i < horizontalIterations; i++) {
                this.scrollElement(element,
                        i *
                                elementWidth, j *
                                elementHeight);
                wait(betweenScrollTimeout);
                BufferedImage image = takeFullPageElementScreenshot();
                image = image.getSubimage(coordinates.getAbsoluteX(),
                        coordinates.getAbsoluteY(),
                        elementWidth,
                        elementHeight);
                g.drawImage(image, this.getElementCurrentScrollX(element),
                        this.getElementCurrentScrollY(element), null);
                if (scrollableWidth == image.getWidth(null) && scrollableHeight == image.getHeight(null)) {
                    break outer_loop;
                }
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullElementScreenshotScroll(By element) {
        Coordinates coordinates = getCoordinates(element);
        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);


        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(scrollableWidth,
                scrollableHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        int horizontalIterations =
                (int) Math.ceil((double) scrollableWidth / elementWidth);
        int verticalIterations = (int) Math.ceil((double) scrollableHeight / elementHeight);
        wait(beforeShootCondition, beforeShootTimeout);
        outer_loop:
        for (int j = 0; j < verticalIterations; j++) {

            this.scrollElement(element, 0,
                    j *
                            elementHeight);
            for (int i = 0; i < horizontalIterations; i++) {
                this.scrollElement(element,
                        i *
                                elementWidth, j *
                                elementHeight);
                wait(betweenScrollTimeout);
                BufferedImage image = takeFullPageElementScreenshot();
                image = image.getSubimage(coordinates.getAbsoluteX(),
                        coordinates.getAbsoluteY(),
                        elementWidth,
                        elementHeight);
                g.drawImage(image, this.getElementCurrentScrollX(element),
                        this.getElementCurrentScrollY(element), null);
                if (scrollableWidth == image.getWidth(null) && scrollableHeight == image.getHeight(null)) {
                    break outer_loop;
                }
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullElementVerticalScreenshotScroll(WebElement element) {
        Coordinates coordinates = getCoordinates(element);

        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);


        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(elementWidth,
                scrollableHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        int verticalIterations =
                (int) Math.ceil(((double) scrollableHeight) / elementHeight);
        BufferedImage image;
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollElement(element, getElementCurrentScrollX(element),
                    j *
                            elementHeight);
            wait(betweenScrollTimeout);
            image = takeFullPageElementScreenshot();
            image = image.getSubimage(coordinates.getAbsoluteX(),
                    coordinates.getAbsoluteY(),
                    elementWidth,
                    elementHeight);

            g.drawImage(image, 0, getElementCurrentScrollY(element),
                    null);
            if (scrollableHeight == image.getHeight(null)) {
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullElementVerticalScreenshotScroll(By element) {
        Coordinates coordinates = getCoordinates(element);

        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);


        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(elementWidth,
                scrollableHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        int verticalIterations =
                (int) Math.ceil(((double) scrollableHeight) / elementHeight);
        BufferedImage image;
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollElement(element, getElementCurrentScrollX(element),
                    j *
                            elementHeight);
            wait(betweenScrollTimeout);
            image = takeFullPageElementScreenshot();
            image = image.getSubimage(coordinates.getAbsoluteX(),
                    coordinates.getAbsoluteY(),
                    elementWidth,
                    elementHeight);

            g.drawImage(image, 0, getElementCurrentScrollY(element),
                    null);
            if (scrollableHeight == image.getHeight(null)) {
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullElementHorizontalScreenshotScroll(WebElement element) {
        Coordinates coordinates = getCoordinates(element);
        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);


        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(scrollableWidth,
                elementHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        int horizontalIterations =
                (int) Math.ceil(((double) scrollableWidth) / elementWidth);
        BufferedImage image;
        for (int j = 0; j < horizontalIterations; j++) {
            this.scrollElement(element,
                    j *
                            elementWidth, getElementCurrentScrollY(element));
            wait(betweenScrollTimeout);
            image = takeFullPageElementScreenshot();
            image = image.getSubimage(coordinates.getAbsoluteX(),
                    coordinates.getAbsoluteY(),
                    elementWidth,
                    elementHeight);

            g.drawImage(image, getElementCurrentScrollX(element), 0,
                    null);
            if (scrollableWidth == image.getWidth(null)) {
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullElementHorizontalScreenshotScroll(By element) {
        Coordinates coordinates = getCoordinates(element);
        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);


        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(scrollableWidth,
                elementHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        int horizontalIterations =
                (int) Math.ceil(((double) scrollableWidth) / elementWidth);
        BufferedImage image;
        for (int j = 0; j < horizontalIterations; j++) {
            this.scrollElement(element,
                    j *
                            elementWidth, getElementCurrentScrollY(element));
            wait(betweenScrollTimeout);
            image = takeFullPageElementScreenshot();
            image = image.getSubimage(coordinates.getAbsoluteX(),
                    coordinates.getAbsoluteY(),
                    elementWidth,
                    elementHeight);

            g.drawImage(image, getElementCurrentScrollX(element), 0,
                    null);
            if (scrollableWidth == image.getWidth(null)) {
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeElementViewportScreenshot(WebElement element) {
        Coordinates coordinates = getCoordinates(element);
        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);

        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(elementWidth,
                elementHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        wait(betweenScrollTimeout);
        BufferedImage image = takeFullPageElementScreenshot();
        image = image.getSubimage(coordinates.getAbsoluteX(),
                coordinates.getAbsoluteY(),
                elementWidth,
                elementHeight);
        g.drawImage(image, 0, 0,
                null);
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeElementViewportScreenshot(By element) {
        Coordinates coordinates = getCoordinates(element);
        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();
        final int scrollBarMaxWidth = getElementScrollBarWidth(element);
        final int scrollBarMaxHeight = getElementScrollBarHeight(element);

        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxHeight < scrollableWidth)) {
            elementHeight -= scrollBarMaxHeight;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(elementWidth,
                elementHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        wait(betweenScrollTimeout);
        BufferedImage image = takeFullPageElementScreenshot();
        image = image.getSubimage(coordinates.getAbsoluteX(),
                coordinates.getAbsoluteY(),
                elementWidth,
                elementHeight);
        g.drawImage(image, 0, 0,
                null);
        g.dispose();
        return combinedImage;
    }


    public BufferedImage takeFrameViewportScreenshot(Coordinates coordinates) {
        final int scrollableHeight = coordinates.getScrollHeight();
        final int scrollableWidth = coordinates.getScrollWidth();
        int elementWidth = coordinates.getWidth();
        int elementHeight = coordinates.getHeight();


        final int scrollBarMaxWidth = getDocScrollBarWidth();

        if (elementWidth < scrollableWidth || (elementHeight < scrollableHeight && elementWidth - scrollBarMaxWidth < scrollableWidth)) {
            elementHeight -= scrollBarMaxWidth;
        }
        if (elementHeight < scrollableHeight) {
            elementWidth -= scrollBarMaxWidth;
        }
        BufferedImage combinedImage = new BufferedImage(elementWidth,
                elementHeight,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();


        BufferedImage image;


        wait(betweenScrollTimeout);
        image = takeFullPageElementScreenshot();
        image = image.getSubimage(coordinates.getAbsoluteX(),
                coordinates.getAbsoluteY(),
                elementWidth,
                elementHeight);

        g.drawImage(image, 0, 0,
                null);


        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeFullPageScreenshotChromeCommand() {
        //should use devicePixelRatio by default as chrome command executor makes screenshot account for that
        Object devicePixelRatio = executeJsScript(DEVICE_PIXEL_RATIO);
        this.devicePixelRatio = devicePixelRatio instanceof Double ? (Double) devicePixelRatio : (Long) devicePixelRatio * 1.0;

        defineCustomCommand("sendCommand", new CommandInfo("/session/:sessionId/chromium/send_command_and_get_result", HttpMethod.POST));

        int verticalIterations = (int) Math.ceil(((double) this.getDocHeight()) / this.getViewportHeight());
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(0, j * this.getViewportHeight());
            wait(betweenScrollTimeout);
        }
        Object metrics = this.evaluate(FileUtil.getJsScript(ALL_METRICS));
        this.sendCommand("Emulation.setDeviceMetricsOverride", metrics);
        wait(beforeShootCondition, beforeShootTimeout);
        Object result = this.sendCommand("Page.captureScreenshot", ImmutableMap.of("format", "png", "fromSurface", true));
        this.sendCommand("Emulation.clearDeviceMetricsOverride", ImmutableMap.of());
        return decodeBase64EncodedPng((String) ((Map<String, ?>) result).get("data"));
    }

    public BufferedImage takeFullPageScreenshotGeckoDriver() {
        // Check geckodriver version (>= 0.24.0 is requried)
        String version = (String) ((RemoteWebDriver) driver).getCapabilities().getCapability("moz:geckodriverVersion");
        if (version == null || Version.valueOf(version).satisfies("<0.24.0")) {
            return takeFullPageScreenshotScroll(null);
        }
        defineCustomCommand("mozFullPageScreenshot", new CommandInfo("/session/:sessionId/moz/screenshot/full", HttpMethod.GET));
        Object result = this.executeCustomCommand("mozFullPageScreenshot");
        String base64EncodedPng;
        if (result instanceof String) {
            base64EncodedPng = (String) result;
        } else if (result instanceof byte[]) {
            base64EncodedPng = new String((byte[]) result);
        } else {
            throw new RuntimeException(String.format("Unexpected result for /moz/screenshot/full command: %s",
                    result == null ? "null" : result.getClass().getName() + "instance"));
        }
        return decodeBase64EncodedPng(base64EncodedPng);
    }

    public WebDriver getUnderlyingDriver() {
        return driver;
    }

    public int getCurrentScrollX() {
        return (int) (Double.parseDouble(executeJsScript(Browser.CURRENT_SCROLL_X_JS).toString()) * devicePixelRatio);
    }

    public int getDocScrollBarWidth() {
        return Math.max((int) (Double.parseDouble(executeJsScript(Browser.DOC_SCROLL_BAR_WIDTH).toString()) * devicePixelRatio), 40);
    }

    public int getElementScrollBarWidth(WebElement element) {
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_SCROLL_BAR_WIDTH, element).toString()) * devicePixelRatio);
    }

    public int getElementScrollBarHeight(WebElement element) {
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_SCROLL_BAR_HEIGHT, element).toString()) * devicePixelRatio);
    }

    public int getElementScrollBarWidth(By by) {
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_SCROLL_BAR_WIDTH, driver.findElement(by)).toString()) * devicePixelRatio);
    }

    public int getElementScrollBarHeight(By by) {
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_SCROLL_BAR_HEIGHT, driver.findElement(by)).toString()) * devicePixelRatio);
    }

    public int getCurrentScrollY() {
        return (int) (Double.parseDouble(executeJsScript(Browser.CURRENT_SCROLL_Y_JS).toString()) * devicePixelRatio);
    }

    public int getElementCurrentScrollX(WebElement element) {
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_CURRENT_SCROLL_X_JS, element).toString()) * devicePixelRatio);
    }

    public int getElementCurrentScrollY(WebElement element) {
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_CURRENT_SCROLL_Y_JS, element).toString()) * devicePixelRatio);
    }

    public int getElementCurrentScrollX(By by){
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_CURRENT_SCROLL_X_JS, driver.findElement(by)).toString()) * devicePixelRatio);
    }

    public int getElementCurrentScrollY(By by) {
        return (int) (Double.parseDouble(executeJsScript(Browser.ELEMENT_CURRENT_SCROLL_Y_JS, driver.findElement(by)).toString()) * devicePixelRatio);
    }

    public int getDocWidth() {
        if (docWidth == -1)
            docWidth =
                    (int) (Double.parseDouble(executeJsScript(MAX_DOC_WIDTH_JS).toString()) * devicePixelRatio);
        return docWidth;
    }

    public int getDocHeight() {
        if (docHeight == -1)
            docHeight = (int) (Double.parseDouble(executeJsScript(MAX_DOC_HEIGHT_JS).toString()) * devicePixelRatio);
        return docHeight;
    }

    public int getViewportWidth() {
        if (viewportWidth == -1)
            viewportWidth = (int) (Double.parseDouble(executeJsScript(VIEWPORT_WIDTH_JS).toString()) * devicePixelRatio);
        return viewportWidth;
    }

    public int getViewportHeight() {
        if (viewportHeight == -1)
            viewportHeight = (int) (Double.parseDouble(executeJsScript(VIEWPORT_HEIGHT_JS).toString()) * devicePixelRatio);
        return viewportHeight;
    }

    public Coordinates getCoordinates(WebElement element) {
        ArrayList<String> list = (ArrayList<String>) executeJsScript(RELATIVE_COORDS_JS, element);
        Point currentLocation = new Point(Integer.parseInt(list.get(0)),
                Integer.parseInt(list.get(1)));
        Dimension size = new Dimension(Integer.parseInt(list.get(2)), Integer.parseInt(list.get(3)));
        Dimension scrollableSize = new Dimension(Integer.parseInt(list.get(4)),
                Integer.parseInt(list.get(5)));
        return new Coordinates(element.getLocation(), currentLocation, size,
                scrollableSize,
                devicePixelRatio);
    }

    public Coordinates getCoordinates(By by) {
       return getCoordinates(driver.findElement(by));
    }

    public void scrollToElement(WebElement element) {
        executeJsScript(SCROLL_INTO_VIEW_JS, element);
    }

    public void scrollToElement(By by) {
        executeJsScript(SCROLL_INTO_VIEW_JS,  driver.findElement(by));
    }

    public void scrollToElementVerticalCentered(WebElement element) {
        executeJsScript(SCROLL_INTO_VIEW_VERTICAL_CENTERED_JS, element);
    }

    public void scrollTo(int x, int y) {
        executeJsScript(SCROLL_TO_JS, x / devicePixelRatio, y / devicePixelRatio);
    }

    public void scrollBy(int x, int y) {
        executeJsScript(SCROLL_BY_JS, x / devicePixelRatio,
                y / devicePixelRatio);
    }

    public void scrollElement(WebElement element, int x, int y) {
        executeJsScript(SCROLL_ELEMENT, element, x / devicePixelRatio,
                y / devicePixelRatio);
    }


    public void scrollElement(By by, int x, int y) {
        executeJsScript(SCROLL_ELEMENT, driver.findElement(by), x / devicePixelRatio,
                y / devicePixelRatio);
    }

    public Object executeJsScript(String filePath, Object... arg) {
        String script = FileUtil.getJsScript(filePath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(script, arg);
    }

    public Object sendCommand(String cmd, Object params) {
        try {
            Method execute = RemoteWebDriver.class.getDeclaredMethod("execute", String.class, Map.class);
            execute.setAccessible(true);
            Response res = (Response) execute.invoke(driver, "sendCommand", ImmutableMap.of("cmd", cmd, "params", params));
            return res.getValue();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object evaluate(String script) {
        Object response = sendCommand("Runtime.evaluate", ImmutableMap.of("returnByValue", true, "expression", script));
        Object result = ((Map<String, ?>) response).get("result");
        return ((Map<String, ?>) result).get("value");
    }

    public Object executeCustomCommand(String commandName) {
        try {
            Method execute = RemoteWebDriver.class.getDeclaredMethod("execute", String.class);
            execute.setAccessible(true);
            Response res = (Response) execute.invoke(this.driver, commandName);
            return res.getValue();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void defineCustomCommand(String name, CommandInfo info) {
        try {
            Method defineCommand = HttpCommandExecutor.class.getDeclaredMethod("defineCommand", String.class, CommandInfo.class);
            defineCommand.setAccessible(true);
            CommandExecutor commandExecutor = ((RemoteWebDriver) this.driver).getCommandExecutor();
            if (commandExecutor instanceof TracedCommandExecutor) {
                Field delegateField = TracedCommandExecutor.class.getDeclaredField("delegate");
                delegateField.setAccessible(true);
                commandExecutor = (CommandExecutor) delegateField.get(commandExecutor);
            }
            defineCommand.invoke(commandExecutor, name, info);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage decodeBase64EncodedPng(String base64EncodedPng) {
        InputStream in = new ByteArrayInputStream(OutputType.BYTES.convertFromBase64Png(base64EncodedPng));
        BufferedImage bImageFromConvert;
        try {
            bImageFromConvert = ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException("Error while converting results from bytes to BufferedImage");
        }
        return bImageFromConvert;
    }
}
