package com.assertthat.selenium_screenshotter.utils.web;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class Screenshotter {

    public static BufferedImage takeScreenshot(Browser browser) {
        File srcFile = ((TakesScreenshot) browser.getUnderlyingDriver()).getScreenshotAs(OutputType.FILE);
        try {
            return ImageIO.read(srcFile);
        } catch (IOException e) {
            throw new UnableTakeScreenshotException(e);
        }
    }

    public static BufferedImage takeScreenshotEntirePage(Browser browser) {
        BufferedImage combinedImage = new BufferedImage(browser.getDocWidth(), browser.getDocHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int horizontalIterations = (int) Math.ceil(((double) browser.getDocWidth()) / browser.getViewportWidth());
        int verticalIterations = (int) Math.ceil(((double) browser.getDocHeight()) / browser.getViewportHeight());
        for (int j = 0; j < verticalIterations; j++) {
            browser.scrollTo(0, j * browser.getViewportHeight());
            for (int i = 0; i < horizontalIterations; i++) {
                browser.scrollTo(i * browser.getViewportWidth(), browser.getViewportHeight() * j);
                Browser.wait(50);
                g.drawImage(takeScreenshot(browser), browser.getCurrentScrollX(), browser.getCurrentScrollY(), null);
            }
        }
        g.dispose();
        return combinedImage;
    }

    public static BufferedImage takeScreenshotScrollHorizontally(Browser browser) {
        BufferedImage combinedImage = new BufferedImage(browser.getDocWidth(), browser.getViewportHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int horizontalIterations = (int) Math.ceil(((double) browser.getDocWidth()) / browser.getViewportWidth());
        for (int i = 0; i < horizontalIterations; i++) {
            browser.scrollTo(i * browser.getViewportWidth(), 0);
            g.drawImage(takeScreenshot(browser), browser.getCurrentScrollX(), 0, null);
        }
        g.dispose();
        return combinedImage;
    }

    public static BufferedImage takeScreenshotScrollVertically(Browser browser) {
        BufferedImage combinedImage = new BufferedImage(browser.getViewportWidth(), browser.getDocHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();
        int verticalIterations = (int) Math.ceil(((double) browser.getDocHeight()) / browser.getViewportHeight());
        for (int j = 0; j < verticalIterations; j++) {
            browser.scrollTo(0, j * browser.getViewportHeight());
            g.drawImage(takeScreenshot(browser), 0, browser.getCurrentScrollY(), null);

        }
        g.dispose();
        return combinedImage;
    }

}
