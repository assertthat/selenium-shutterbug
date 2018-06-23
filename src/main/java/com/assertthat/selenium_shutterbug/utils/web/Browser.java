/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.web;

import com.assertthat.selenium_shutterbug.utils.file.FileUtil;
import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class Browser {

    public static final String RELATIVE_COORDS_JS = "js/relative-element-coords.js";
    public static final String MAX_DOC_WIDTH_JS = "js/max-document-width.js";
    public static final String MAX_DOC_HEIGHT_JS = "js/max-document-height.js";
    public static final String VIEWPORT_HEIGHT_JS = "js/viewport-height.js";
    public static final String VIEWPORT_WIDTH_JS = "js/viewport-width.js";
    public static final String SCROLL_TO_JS = "js/scroll-to.js";
    public static final String SCROLL_INTO_VIEW_JS = "js/scroll-element-into-view.js";
    public static final String CURRENT_SCROLL_Y_JS = "js/get-current-scrollY.js";
    public static final String CURRENT_SCROLL_X_JS = "js/get-current-scrollX.js";
    public static final String DEVICE_PIXEL_RATIO = "js/get-device-pixel-ratio.js";
    public static final String ALL_METRICS = "js/all-metrics.js";

    private WebDriver driver;
    private int docHeight = -1;
    private int docWidth = -1;
    private int viewportWidth = -1;
    private int viewportHeight = -1;
    private int scrollTimeout;
    private Double devicePixelRatio = 1.0;

    public Browser(WebDriver driver, boolean useDevicePixelRatio) {
        this.driver = driver;
        if(useDevicePixelRatio) {
            Object devicePixelRatio = executeJsScript(DEVICE_PIXEL_RATIO);
            this.devicePixelRatio = devicePixelRatio instanceof Double? (Double)devicePixelRatio: (Long)devicePixelRatio*1.0;
        }
    }

    public static void wait(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new UnableTakeSnapshotException(e);
        }
    }

    public void setScrollTimeout(int scrollTimeout){
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

    public BufferedImage takeScreenshotEntirePage() {
        final int _docWidth = this.getDocWidth();
		final int _docHeight = this.getDocHeight();
		BufferedImage combinedImage = new BufferedImage(_docWidth, _docHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int _viewportWidth = this.getViewportWidth();
        int _viewportHeight = this.getViewportHeight();
        final int scrollBarMaxWidth = 40; // this is probably too high, but better to be safe than sorry

		if (_viewportWidth < _docWidth || (_viewportHeight < _docHeight && _viewportWidth - scrollBarMaxWidth < _docWidth))
        	_viewportHeight-=scrollBarMaxWidth; // some space for a scrollbar
        if (_viewportHeight < _docHeight)
        	_viewportWidth-=scrollBarMaxWidth; // some space for a scrollbar

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
                if(_docWidth == image.getWidth(null) && _docHeight == image.getHeight(null)){
                    break outer_loop;
                }
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeScreenshotEntirePageUsingChromeCommand() {
        DriverCommandExecutor driver = null;
        try {
            driver = new DriverCommandExecutor((ChromeDriver) this.driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int verticalIterations = (int) Math.ceil(((double) this.getDocHeight()) / this.getViewportHeight());
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(0, j * this.getViewportHeight());
            wait(scrollTimeout);
        }
        Object metrics = driver.evaluate(FileUtil.getJsScript(ALL_METRICS));
        driver.sendCommand("Emulation.setDeviceMetricsOverride", metrics);
        Object result = driver.sendCommand("Page.captureScreenshot", ImmutableMap.of("format", "png", "fromSurface", true));
        driver.sendCommand("Emulation.clearDeviceMetricsOverride", ImmutableMap.of());
        String base64EncodedPng = (String) ((Map<String, ?>) result).get("data");
        InputStream in = new ByteArrayInputStream(OutputType.BYTES.convertFromBase64Png(base64EncodedPng));
        BufferedImage bImageFromConvert;
        try {
            bImageFromConvert = ImageIO.read(in);
        } catch (IOException e) {
            throw new RuntimeException("Error while converting results from bytes to BufferedImage");
        }
        return bImageFromConvert;
    }

    public BufferedImage takeScreenshotScrollHorizontally() {
        BufferedImage combinedImage = new BufferedImage(this.getDocWidth(), this.getViewportHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int horizontalIterations = (int) Math.ceil(((double) this.getDocWidth()) / this.getViewportWidth());
        for (int i = 0; i < horizontalIterations; i++) {
            this.scrollTo(i * this.getViewportWidth(), 0);
            wait(scrollTimeout);
            Image image = takeScreenshot();
            g.drawImage(image, this.getCurrentScrollX(), 0, null);
            if(this.getDocWidth() == image.getWidth(null)){
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeScreenshotScrollVertically() {
        BufferedImage combinedImage = new BufferedImage(this.getViewportWidth(), this.getDocHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int verticalIterations = (int) Math.ceil(((double) this.getDocHeight()) / this.getViewportHeight());
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(0, j * this.getViewportHeight());
            wait(scrollTimeout);
            Image image = takeScreenshot();
            g.drawImage(image, 0, this.getCurrentScrollY(), null);
            if(this.getDocHeight() == image.getHeight(null)){
                break;
            }
        }
        g.dispose();
        return combinedImage;
    }

    public WebDriver getUnderlyingDriver() {
        return driver;
    }

    public int getCurrentScrollX() {
        return (int)(((Long) executeJsScript(Browser.CURRENT_SCROLL_X_JS))*devicePixelRatio);
    }

    public int getCurrentScrollY() {
        return (int)(((Long) executeJsScript(Browser.CURRENT_SCROLL_Y_JS))*devicePixelRatio);
    }

    public int getDocWidth() {
        return docWidth != -1 ? docWidth : (int)(((Long) executeJsScript(MAX_DOC_WIDTH_JS))*devicePixelRatio);
    }

    public int getDocHeight() {
        return docHeight != -1 ? docHeight : (int)(((Long) executeJsScript(MAX_DOC_HEIGHT_JS))*devicePixelRatio);
    }

    public int getViewportWidth() {
        return viewportWidth != -1 ? viewportWidth : (int)(((Long) executeJsScript(VIEWPORT_WIDTH_JS))*devicePixelRatio);
    }

    public int getViewportHeight() {
        return viewportHeight != -1 ? viewportHeight : (int)(((Long) executeJsScript(VIEWPORT_HEIGHT_JS)).intValue()*devicePixelRatio);
    }

    public Coordinates getBoundingClientRect(WebElement element) {
        String script = FileUtil.getJsScript(RELATIVE_COORDS_JS);
        ArrayList<String> list = (ArrayList<String>) executeJsScript(RELATIVE_COORDS_JS, element);
        Point start = new Point(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        Dimension size = new Dimension(Integer.parseInt(list.get(2)), Integer.parseInt(list.get(3)));
        return new Coordinates(start, size);
    }

    public void scrollToElement(WebElement element) {
        executeJsScript(SCROLL_INTO_VIEW_JS, element);
    }

    public void scrollTo(int x, int y) {
        executeJsScript(SCROLL_TO_JS, x/devicePixelRatio, y/devicePixelRatio);
    }

    public Object executeJsScript(String filePath, Object... arg) {
        String script = FileUtil.getJsScript(filePath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(script, arg);
    }
}
