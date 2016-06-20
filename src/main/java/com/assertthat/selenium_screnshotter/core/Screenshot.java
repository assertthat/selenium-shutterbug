package com.assertthat.selenium_screnshotter.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.assertthat.selenium_screnshotter.utils.web.*;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.assertthat.selenium_screnshotter.utils.image.ImageProcessor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public abstract class Screenshot<T extends Screenshot<T>> {

    protected BufferedImage image;
    private static final String extension = "PNG";
    private String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"))
            + "." + extension.toLowerCase();
    private Path location = Paths.get("./screenshots/");
    private String title;
    protected WebDriver driver;

    protected abstract T self();

    public T withName(String name) {
        if (name != null) {
            fileName = name + "." + extension.toLowerCase();
        }
        return self();
    }

    public T withTitle(String title) {
        this.title = title;
        return self();
    }

    public T saveTo(Path path) {
        if (Files.exists(path)) {
            this.location = path;
        }
        return self();
    }

    public T saveTo(String path) {
        if (path != null) {
            saveTo(Paths.get(path));
        }
        return self();
    }

    public T monochrome() {
        this.image = ImageProcessor.convertToGrayAndWhite(this.image);
        return self();
    }

    protected void setImage(BufferedImage image) {
        self().image = image;
    }

    protected BufferedImage getImage() {
        return image;
    }


    public void take() {
        File screenshotFile = new File(location.toString(), fileName);
        screenshotFile.mkdirs();
        if (title != null && !title.isEmpty()) {
            image = ImageProcessor.addTitle(image, title, Color.red, new Font("Serif", Font.BOLD, 20));
        }
        try {
            ImageIO.write(image, extension, screenshotFile);
        } catch (IOException e) {
            throw new UnableTakeScreenshotException(e);
        }
    }

    public static PageScreenshot page(WebDriver driver) {
        Browser browser = new Browser(driver);
        PageScreenshot pageScreenshot = new PageScreenshot(driver);
        pageScreenshot.setImage(Screenshotter.takeScreenshot(browser));
        return pageScreenshot;
    }

    public static PageScreenshot page(WebDriver driver, ScrollStrategy scroll) {
        Browser browser = new Browser(driver);
        PageScreenshot pageScreenshot = new PageScreenshot(driver);
        switch (scroll) {
            case HORIZONTALLY:
                pageScreenshot.setImage(Screenshotter.takeScreenshotScrollHorizontally(browser));
            case VERTICALLY:
                pageScreenshot.setImage(Screenshotter.takeScreenshotScrollVertically(browser));
            case BOTH_DIRECTIONS:
                pageScreenshot.setImage(Screenshotter.takeScreenshotEntirePage(browser));
        }
        return pageScreenshot;
    }


    public static ElementScreenshot element(WebDriver driver, WebElement element) {
        Browser browser = new Browser(driver);
        ElementScreenshot elementScreenshot = new ElementScreenshot(driver, element);
        browser.scrollToElement(element);
        elementScreenshot.setImage(Screenshotter.takeScreenshot(browser));
        elementScreenshot.setImage(ImageProcessor.getElement(elementScreenshot.getImage(), browser.getBoundingClientRect(element)));
        return elementScreenshot;
    }

}