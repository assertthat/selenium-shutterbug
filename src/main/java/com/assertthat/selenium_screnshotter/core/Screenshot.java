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

    public static WebPageScreenshot page(WebDriver driver) {
        Browser browser = new Browser(driver);
        WebPageScreenshot webPageScreenshot = new WebPageScreenshot(driver);
        webPageScreenshot.setImage(Screenshotter.takeScreenshot(browser));
        return webPageScreenshot;
    }

    public static WebPageScreenshot page(WebDriver driver, ScrollStrategy scroll) {
        Browser browser = new Browser(driver);
        WebPageScreenshot webPageScreenshot = new WebPageScreenshot(driver);
        switch (scroll) {
            case HORIZONTALLY:
                webPageScreenshot.setImage(Screenshotter.takeScreenshotScrollHorizontally(browser));
            case VERTICALLY:
                webPageScreenshot.setImage(Screenshotter.takeScreenshotScrollVertically(browser));
            case BOTH_DIRECTIONS:
                webPageScreenshot.setImage(Screenshotter.takeScreenshotEntirePage(browser));
        }
        return webPageScreenshot;
    }


    public static WebElementScreenshot element(WebDriver driver, WebElement element) {
        Browser browser = new Browser(driver);
        WebElementScreenshot el = new WebElementScreenshot(driver, element);
        browser.scrollToElement(element);
        el.setImage(Screenshotter.takeScreenshot(browser));
        el.setImage(ImageProcessor.getElement(el.getImage(), browser.getBoundingClientRect(element)));
        return el;
    }

}