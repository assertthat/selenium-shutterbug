package com.assertthat.selenium_screnshotter.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class WebDriverHelper {

    public static BufferedImage takeScreenshot(WebDriver driver) throws IOException {
        File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        return ImageIO.read(srcFile);
    }

}