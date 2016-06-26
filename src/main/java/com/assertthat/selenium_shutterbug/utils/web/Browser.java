/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.assertthat.selenium_shutterbug.utils.web;

import com.assertthat.selenium_shutterbug.utils.file.FileUtil;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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

    public static void wait(int milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new UnableTakeSnapshotException(e);
        }
    }

    public BufferedImage takeScreenshot() {
        File srcFile = ((TakesScreenshot) this.getUnderlyingDriver()).getScreenshotAs(OutputType.FILE);
        try {
            return ImageIO.read(srcFile);
        } catch (IOException e) {
            throw new UnableTakeSnapshotException(e);
        }
    }

    public BufferedImage takeScreenshotEntirePage() {
        BufferedImage combinedImage = new BufferedImage(this.getDocWidth(), this.getDocHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int horizontalIterations = (int) Math.ceil(((double) this.getDocWidth()) / this.getViewportWidth());
        int verticalIterations = (int) Math.ceil(((double) this.getDocHeight()) / this.getViewportHeight());
        for (int j = 0; j < verticalIterations; j++) {
            this.scrollTo(0, j * this.getViewportHeight());
            for (int i = 0; i < horizontalIterations; i++) {
                this.scrollTo(i * this.getViewportWidth(), this.getViewportHeight() * j);
                wait(50);
                g.drawImage(takeScreenshot(), this.getCurrentScrollX(), this.getCurrentScrollY(), null);
            }
        }
        g.dispose();
        return combinedImage;
    }

    public BufferedImage takeScreenshotScrollHorizontally() {
        BufferedImage combinedImage = new BufferedImage(this.getDocWidth(), this.getViewportHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int horizontalIterations = (int) Math.ceil(((double) this.getDocWidth()) / this.getViewportWidth());
        for (int i = 0; i < horizontalIterations; i++) {
            this.scrollTo(i * this.getViewportWidth(), 0);
            g.drawImage(takeScreenshot(), this.getCurrentScrollX(), 0, null);
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
            g.drawImage(takeScreenshot(), 0, this.getCurrentScrollY(), null);

        }
        g.dispose();
        return combinedImage;
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
}
