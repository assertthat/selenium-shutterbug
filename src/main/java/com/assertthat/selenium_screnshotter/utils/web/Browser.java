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
        return ((Long) executeJsScript(Browser.CURRENT_SCROLL_X_JS)).intValue();
    }

    public int getCurrentScrollY() {
        return ((Long) executeJsScript(Browser.CURRENT_SCROLL_Y_JS)).intValue();
    }

    public int getDocWidth() {
        return docWidth != -1 ? docWidth : ((Long) executeJsScript(MAX_DOC_WIDTH_JS)).intValue();
    }

    public int getDocHeight() {
        return docHeight != -1 ? docHeight : ((Long) executeJsScript(MAX_DOC_HEIGHT_JS)).intValue();
    }

    public int getViewportWidth() {
        return viewportWidth != -1 ? viewportWidth : ((Long) executeJsScript(VIEWPORT_WIDTH_JS)).intValue();
    }

    public int getViewportHeight() {
        return viewportHeight != -1 ? viewportHeight : ((Long) executeJsScript(VIEWPORT_HEIGHT_JS)).intValue();
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
        executeJsScript(SCROLL_TO_JS, x, y);
    }

    public Object executeJsScript(String filePath, Object... arg) {
        String script = FileUtil.getJsScript(filePath);
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return js.executeScript(script, arg);
    }

    public static void wait(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new UnableTakeScreenshotException(e);
        }
    }
}
