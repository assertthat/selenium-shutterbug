/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.web;

import com.assertthat.selenium_shutterbug.utils.file.FileUtil;
import com.github.zafarkhaja.semver.Version;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.Response;
import org.openqa.selenium.remote.http.HttpMethod;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class Browser {

    private static final String RELATIVE_COORDS_JS = "js/relative-element-coords.js";
    private static final String MAX_DOC_WIDTH_JS = "js/max-document-width.js";
    private static final String MAX_DOC_HEIGHT_JS = "js/max-document-height.js";
    private static final String VIEWPORT_HEIGHT_JS = "js/viewport-height.js";
    private static final String VIEWPORT_WIDTH_JS = "js/viewport-width.js";
    private static final String SCROLL_TO_JS = "js/scroll-to.js";
    private static final String SCROLL_INTO_VIEW_JS = "js/scroll-element-into-view.js";
    private static final String SCROLL_INTO_VIEW_VERTICAL_CENTERED_JS = "js/scroll-element-into-view-vertical-centered.js";
    private static final String CURRENT_SCROLL_Y_JS = "js/get-current-scrollY.js";
    private static final String CURRENT_SCROLL_X_JS = "js/get-current-scrollX.js";
    private static final String DEVICE_PIXEL_RATIO = "js/get-device-pixel-ratio.js";
    private static final String ALL_METRICS = "js/all-metrics.js";

    private WebDriver driver;
    private int docHeight = -1;
    private int docWidth = -1;
    private int viewportWidth = -1;
    private int viewportHeight = -1;
    private int scrollTimeout;
    private Double devicePixelRatio = 1.0;

    public Browser(WebDriver driver, boolean useDevicePixelRatio) {
        this.driver = driver;
        if (useDevicePixelRatio) {
            Object devicePixelRatio = executeJsScript(DEVICE_PIXEL_RATIO);
            this.devicePixelRatio = devicePixelRatio instanceof Double ? (Double) devicePixelRatio : (Long) devicePixelRatio * 1.0;
        }
    }

    public Double getDevicePixelRatio() {
        return devicePixelRatio;
    }

    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new UnableTakeSnapshotException(e);
        }
    }

    public void setScrollTimeout(int scrollTimeout) {
        this.scrollTimeout = scrollTimeout;
    }

    public BufferedImage takeScreenshot() {
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
     * Using different screenshot strategy dependently on driver:
     * for  chrome - chrome command will be used
     * for firefox - geckodriver endpoint will be used if available
     * for others - their default screenshot methods
     *
     * @return BufferedImage resulting image
     */
    public BufferedImage takeScreenshotEntirePage() {
        driver = unwrapDriver();

        if (driver instanceof ChromeDriver) {
            return takeScreenshotEntirePageUsingChromeCommand();
        } else if (driver instanceof FirefoxDriver) {
            return takeScreenshotEntirePageUsingGeckoDriver();
        } else if (driver instanceof RemoteWebDriver) {
            if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("chrome")) {
                return takeScreenshotEntirePageUsingChromeCommand();
            } else if (((RemoteWebDriver) driver).getCapabilities().getBrowserName().equals("firefox")) {
                return takeScreenshotEntirePageUsingGeckoDriver();
            }
        }
        return takeScreenshotEntirePageDefault();
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

	public BufferedImage takeScreenshotEntirePageDefault() {
        final int _docWidth = this.getDocWidth();
        final int _docHeight = this.getDocHeight();
        BufferedImage combinedImage = new BufferedImage(_docWidth, _docHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int _viewportWidth = this.getViewportWidth();
        int _viewportHeight = this.getViewportHeight();
        final int scrollBarMaxWidth = 40; // this is probably too high, but better to be safe than sorry

        if (_viewportWidth < _docWidth || (_viewportHeight < _docHeight && _viewportWidth - scrollBarMaxWidth < _docWidth))
            _viewportHeight -= scrollBarMaxWidth; // some space for a scrollbar 
        if (_viewportHeight < _docHeight)
            _viewportWidth -= scrollBarMaxWidth; // some space for a scrollbar

        int horizontalIterations = (int) Math.ceil(((double) _docWidth) / _viewportWidth);
        int verticalIterations = (int) Math.ceil(((double) _docHeight) / _viewportHeight);
        outer_loop:
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(0, j * _viewportHeight);
            for (int i = 0; i < horizontalIterations; i++) {
                this.scrollTo(i * _viewportWidth, _viewportHeight * j);
                wait(scrollTimeout);
                Image image = takeScreenshot();
                g.drawImage(image, this.getCurrentScrollX(), this.getCurrentScrollY(), null);
                if (_docWidth == image.getWidth(null) && _docHeight == image.getHeight(null)) {
                    break outer_loop;
                }
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeScreenshotEntirePageUsingChromeCommand() {
        //should use devicePixelRatio by default as chrome command executor makes screenshot account for that
        Object devicePixelRatio = executeJsScript(DEVICE_PIXEL_RATIO);
        this.devicePixelRatio = devicePixelRatio instanceof Double ? (Double) devicePixelRatio : (Long) devicePixelRatio * 1.0;

        defineCustomCommand("sendCommand", new CommandInfo("/session/:sessionId/chromium/send_command_and_get_result", HttpMethod.POST));

        int verticalIterations = (int) Math.ceil(((double) this.getDocHeight()) / this.getViewportHeight());
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(0, j * this.getViewportHeight());
            wait(scrollTimeout);
        }
        Object metrics = this.evaluate(FileUtil.getJsScript(ALL_METRICS));
        this.sendCommand("Emulation.setDeviceMetricsOverride", metrics);
        Object result = this.sendCommand("Page.captureScreenshot", ImmutableMap.of("format", "png", "fromSurface", true));
        this.sendCommand("Emulation.clearDeviceMetricsOverride", ImmutableMap.of());
        return decodeBase64EncodedPng((String) ((Map<String, ?>) result).get("data"));
    }

    public BufferedImage takeScreenshotEntirePageUsingGeckoDriver() {
        // Check geckodriver version (>= 0.24.0 is requried)
        String version = (String) ((RemoteWebDriver) driver).getCapabilities().getCapability("moz:geckodriverVersion");
        if (version == null || Version.valueOf(version).satisfies("<0.24.0")) {
            return takeScreenshotEntirePageDefault();
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
        return (int) (((Long) executeJsScript(Browser.CURRENT_SCROLL_X_JS)) * devicePixelRatio);
    }

    public int getCurrentScrollY() {
        return (int) (((Long) executeJsScript(Browser.CURRENT_SCROLL_Y_JS)) * devicePixelRatio);
    }

    public int getDocWidth() {
        if (docWidth == -1) docWidth = (int) (((Long) executeJsScript(MAX_DOC_WIDTH_JS)) * devicePixelRatio);
        return docWidth;
    }

    public int getDocHeight() {
        if (docHeight == -1) docHeight = (int) (((Long) executeJsScript(MAX_DOC_HEIGHT_JS)) * devicePixelRatio);
        return docHeight;
    }

    public int getViewportWidth() {
        if (viewportWidth == -1) viewportWidth = (int) (((Long) executeJsScript(VIEWPORT_WIDTH_JS)) * devicePixelRatio);
        return viewportWidth;
    }

    public int getViewportHeight() {
        if (viewportHeight == -1)
            viewportHeight = (int) (((Long) executeJsScript(VIEWPORT_HEIGHT_JS)) * devicePixelRatio);
        return viewportHeight;
    }

    public Coordinates getBoundingClientRect(WebElement element) {
        FileUtil.getJsScript(RELATIVE_COORDS_JS);
        ArrayList<String> list = (ArrayList<String>) executeJsScript(RELATIVE_COORDS_JS, element);
        Point start = new Point(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        Dimension size = new Dimension(Integer.parseInt(list.get(2)), Integer.parseInt(list.get(3)));
        return new Coordinates(start, size, devicePixelRatio);
    }

    public void scrollToElement(WebElement element) {
        executeJsScript(SCROLL_INTO_VIEW_JS, element);
    }

    public void scrollToElementVerticalCentered(WebElement element) {
    	executeJsScript(SCROLL_INTO_VIEW_VERTICAL_CENTERED_JS, element);
    }
    
    public void scrollTo(int x, int y) {
        executeJsScript(SCROLL_TO_JS, x / devicePixelRatio, y / devicePixelRatio);
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
            defineCommand.invoke(((RemoteWebDriver) this.driver).getCommandExecutor(), name, info);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
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
