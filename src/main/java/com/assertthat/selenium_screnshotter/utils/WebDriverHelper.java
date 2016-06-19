package com.assertthat.selenium_screnshotter.utils;

import org.apache.commons.io.IOUtils;
import org.jboss.netty.handler.codec.http.websocketx.WebSocket13FrameDecoder;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class WebDriverHelper {
    public static final String RELATIVE_COORDS_JS = "js/relative-element-coords.js";
    public static final String MAX_DOC_HEIGHT_JS = "js/max-document-height.js";
    public static final String MAX_DOC_WIDTH_JS = "js/max-document-width.js";
    public static final String VIEWPORT_HEIGHT_JS = "js/viewport-height.js";
    public static final String VIEWPORT_WIDTH_JS = "js/viewport-width.js";
    public static final String SCROLL_TO_JS = "js/scroll-to.js";
    public static final String SCROLL_INTO_VIEW_JS = "js/scroll-element-into-view.js";
    public static final String CURRENT_SCROLL_Y_JS = "js/get-current-scrollY.js";
    public static final String CURRENT_SCROLL_X_JS = "js/get-current-scrollX.js";

    public static BufferedImage takeScreenshot(WebDriver driver) throws IOException {
        File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        return ImageIO.read(srcFile);
    }

    public static void scrollToElement(WebDriver driver, WebElement element){
        ((JavascriptExecutor) driver).executeScript(SCROLL_INTO_VIEW_JS, element);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //TODO
        }
    }

    public static Object[] getBoundingClientRect(WebElement element, WebDriver driver) throws IOException {
        String script = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(RELATIVE_COORDS_JS));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        ArrayList<String> list = (ArrayList<String>) js.executeScript(script,element);
        Point start = new Point(Integer.parseInt(list.get(0)), Integer.parseInt(list.get(1)));
        Dimension size = new Dimension(Integer.parseInt(list.get(2)), Integer.parseInt(list.get(3)));
        return new Object[]{start, size};
    }

    public static BufferedImage takeScreenshotEntirePage(WebDriver driver) throws IOException {
        int docHeight = executeJsScriptReturn(driver, MAX_DOC_HEIGHT_JS);
        System.out.println(docHeight);
        int docWidth = executeJsScriptReturn(driver, MAX_DOC_WIDTH_JS);
        System.out.println(docWidth);
        int viewportWidth = executeJsScriptReturn(driver, VIEWPORT_WIDTH_JS);
        System.out.println(viewportWidth);
        int viewportHeight = executeJsScriptReturn(driver, VIEWPORT_HEIGHT_JS);
        System.out.println(viewportHeight);
        BufferedImage combinedImage = new BufferedImage(docWidth, docHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D  g = combinedImage.createGraphics();
        int horizontalIterations =(int) Math.ceil(((double)docWidth)/viewportWidth);
        int verticalIterations =(int) Math.ceil(((double)docHeight)/viewportHeight);
        for(int j=0; j<verticalIterations; j++) {
            executeJsScript(driver, SCROLL_TO_JS, "0" , String.valueOf(j*viewportHeight));
            for (int i = 0; i < horizontalIterations; i++) {
                executeJsScript(driver, SCROLL_TO_JS, String.valueOf(i * viewportWidth), String.valueOf(viewportHeight * j));
                int scrollX = executeJsScriptReturn(driver, CURRENT_SCROLL_X_JS);
                int scrollY = executeJsScriptReturn(driver, CURRENT_SCROLL_Y_JS);
                g.drawImage(takeScreenshot(driver), scrollX, scrollY, null);

            }
        }
        g.dispose();
        return combinedImage;
    }

    public static BufferedImage takeScreenshotScrollHorizontally(WebDriver driver) throws IOException {
        int docWidth = executeJsScriptReturn(driver, MAX_DOC_WIDTH_JS);
        int viewportWidth = executeJsScriptReturn(driver, VIEWPORT_WIDTH_JS);
        int viewportHeight = executeJsScriptReturn(driver, VIEWPORT_HEIGHT_JS);
        BufferedImage combinedImage = new BufferedImage(docWidth, viewportHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D  g = combinedImage.createGraphics();
        int horizontalIterations =(int) Math.ceil(((double)docWidth)/viewportWidth);
            for (int i = 0; i < horizontalIterations; i++) {
                executeJsScript(driver, SCROLL_TO_JS, String.valueOf(i * viewportWidth), "0");
                int scrollX = executeJsScriptReturn(driver, CURRENT_SCROLL_X_JS);
                g.drawImage(takeScreenshot(driver), scrollX, 0, null);

            }
        g.dispose();
        return combinedImage;
    }

    public static BufferedImage takeScreenshotScrollVertically(WebDriver driver) throws IOException {
        int docHeight = executeJsScriptReturn(driver, MAX_DOC_HEIGHT_JS);
        int viewportWidth = executeJsScriptReturn(driver, VIEWPORT_WIDTH_JS);
        int viewportHeight = executeJsScriptReturn(driver, VIEWPORT_HEIGHT_JS);
        BufferedImage combinedImage = new BufferedImage(viewportWidth, docHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D  g = combinedImage.createGraphics();
        int verticalIterations =(int) Math.ceil(((double)docHeight)/viewportHeight);
        for(int j=0; j<verticalIterations; j++) {
            executeJsScript(driver, SCROLL_TO_JS, "0" , String.valueOf(j*viewportHeight));
                int scrollY = executeJsScriptReturn(driver, CURRENT_SCROLL_Y_JS);
                g.drawImage(takeScreenshot(driver), 0, scrollY, null);

            }
        g.dispose();
        return combinedImage;
    }



    public static int executeJsScriptReturn(WebDriver driver, String filePath) throws IOException {
       String script = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
       JavascriptExecutor js = (JavascriptExecutor) driver;
       return ((Long) js.executeScript(script)).intValue();
   }

    public static void executeJsScript(WebDriver driver, String filePath, String ...args) throws IOException {
        String script = IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(script, args);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}