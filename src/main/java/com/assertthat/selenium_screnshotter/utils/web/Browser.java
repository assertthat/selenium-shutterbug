package com.assertthat.selenium_screnshotter.utils.web;

import com.assertthat.selenium_screnshotter.utils.file.FileUtil;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.*;

import java.io.IOException;
import java.util.ArrayList;

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

    private WebDriver driver;
    private int docHeight = -1;
    private int docWidth = -1;
    private int viewportWidth = -1;
    private int viewportHeight = -1;
    private int currentScrollX;
    private int currentScrollY;

    public Browser(WebDriver driver) {
        this.driver = driver;
    }

    public WebDriver getUnderlyingDriver() {
        return driver;
    }

    public int getCurrentScrollX() {
        return executeJsScript(Browser.CURRENT_SCROLL_X_JS);
    }

    public int getCurrentScrollY() {
        return executeJsScript(Browser.CURRENT_SCROLL_Y_JS);
    }

    public int getDocWidth() {
        return docWidth != -1 ? docWidth : executeJsScript(MAX_DOC_WIDTH_JS);
    }

    public int getDocHeight() {
        return docHeight != -1 ? docHeight : executeJsScript(MAX_DOC_HEIGHT_JS);
    }

    public int getViewportWidth() {
        return viewportWidth != -1 ? viewportWidth : executeJsScript(VIEWPORT_WIDTH_JS);
    }

    public int getViewportHeight() {
        return viewportHeight != -1 ? viewportHeight : executeJsScript(VIEWPORT_HEIGHT_JS);
    }

    public Coordinates getBoundingClientRect(WebElement element) {
        String script = null;
        try {
            script = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(RELATIVE_COORDS_JS));
        } catch (IOException e) {
            e.printStackTrace();
        }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        ArrayList<String> list = (ArrayList<String>) js.executeScript(script, element);
        Point start = new Point(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        Dimension size = new Dimension(Integer.parseInt(list.get(2)), Integer.parseInt(list.get(3)));
        return new Coordinates(start, size);
    }

    public void scrollToElement(WebElement element) {
        executeJsScript(SCROLL_INTO_VIEW_JS, element);
    }

    public void scrollTo(int x, int y) {
        executeJsScript(SCROLL_TO_JS, x, y);
    }

    public int executeJsScript(String filePath) {
        String script = FileUtil.getJsScript(filePath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return ((Long) js.executeScript(script)).intValue();
    }

    public void executeJsScript(String filePath, Object... args) {
        String script = FileUtil.getJsScript(filePath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(script, args);
    }

    public static void wait(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new UnableTakeScreenshotException(e);
        }
    }
}
