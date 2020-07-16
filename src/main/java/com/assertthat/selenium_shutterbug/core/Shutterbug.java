/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.web.Browser;
import com.assertthat.selenium_shutterbug.utils.web.ScrollStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

/**
 * Created by Glib_Briia on 26/06/2016.
 */
public class Shutterbug {

    private static final int DEFAULT_SCROLL_TIMEOUT = 100;
    private static Function<WebDriver,?> beforeShootCondition;
    private static int beforeShootTimeout;

    private Shutterbug(){

    }

    /**
     * Make screen shot of the viewport only.
     * To be used when screen shooting the page
     * and don't need to scroll while making screen shots (FF, IE).
     *
     * @param driver WebDriver instance
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver) {
        return shootPage(driver, true);
    }

    /**
     * Make screen shot of the viewport only.
     * To be used when screen shooting the page
     * and don't need to scroll while making screen shots (FF, IE).
     *
     * @param driver              WebDriver instance
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        PageSnapshot pageScreenshot = new PageSnapshot(driver, browser.getDevicePixelRatio());
        pageScreenshot.setImage(browser.takeScreenshot());
        return pageScreenshot;
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver WebDriver instance
     * @param scroll ScrollStrategy How you need to scroll
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll) {
        return shootPage(driver, scroll, DEFAULT_SCROLL_TIMEOUT);
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver               WebDriver instance
     * @param scroll               ScrollStrategy How you need to scroll
     * @param betweenScrollTimeout Timeout to wait after scrolling and before taking screenshot
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, int betweenScrollTimeout) {
        return shootPage(driver, scroll, betweenScrollTimeout, true);
    }

    /**
     * Wait for condition to be true before taking screenshot
     *
     * @param cond                condition
     * @param timeout             timeout wait for condition
     * @return Shutterbug
     */
    public static Shutterbug wait(ExpectedCondition<?> cond, int timeout) {
        beforeShootCondition = cond;
        beforeShootTimeout = timeout;
        return null;
    }

    /**
     * Wait for before taking screenshot
     *
     * @param timeout             timeout wait for condition
     * @return Shutterbug
     */
    public static Shutterbug wait(int timeout) {
        beforeShootTimeout = timeout;
        return null;
    }


    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver              WebDriver instance
     * @param scroll              ScrollStrategy How you need to scroll
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, boolean useDevicePixelRatio) {
        return shootPage(driver, scroll, 0, useDevicePixelRatio);
    }

    /**
     * To be used when screen shooting the page
     * and need to scroll while making screen shots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver               WebDriver instance
     * @param scroll               ScrollStrategy How you need to scroll
     * @param betweenScrollTimeout Timeout to wait between each scrolling operation
     * @param useDevicePixelRatio  whether or not take into account device pixel ratio
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll, int betweenScrollTimeout, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        browser.setBetweenScrollTimeout(betweenScrollTimeout);
        if(beforeShootCondition != null) {
            browser.setBeforeShootTimeout(beforeShootTimeout);
            browser.setBeforeShootCondition(beforeShootCondition);
        }else if(beforeShootTimeout!=0){
            browser.setBeforeShootTimeout(beforeShootTimeout);
        }

        PageSnapshot pageScreenshot = new PageSnapshot(driver, browser.getDevicePixelRatio());
        switch (scroll) {
            case VIEWPORT_ONLY:
                pageScreenshot.setImage(browser.takeScreenshot());
                break;
            case WHOLE_PAGE:
                pageScreenshot.setImage(browser.takeScreenshotEntirePage());
                break;
        }
        return pageScreenshot;
    }

    /**
     * To be used when need to screenshot particular element by vertically centering it in viewport.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElementVerticallyCentered(WebDriver driver, WebElement element) {
        return shootElementVerticallyCentered(driver, element, true);
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element) {
        return shootElement(driver, element, true);
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver              WebDriver instance
     * @param element             WebElement instance to be screen shot
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElement(element);
        elementSnapshot.setImage(browser.takeScreenshot(), browser.getBoundingClientRect(element));
        return elementSnapshot;
    }

    /**
     * To be used when need to screenshot particular element by vertically centering it within viewport.
     *
     * @param driver              WebDriver instance
     * @param element             WebElement instance to be screen shot
     * @param useDevicePixelRatio whether or not take into account device pixel ratio
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElementVerticallyCentered(WebDriver driver, WebElement element, boolean useDevicePixelRatio) {
        Browser browser = new Browser(driver, useDevicePixelRatio);
        ElementSnapshot elementSnapshot = new ElementSnapshot(driver, browser.getDevicePixelRatio());
        browser.scrollToElementVerticalCentered(element);
        elementSnapshot.setImage(browser.takeScreenshot(), browser.getBoundingClientRect(element));
        return elementSnapshot;
    }

    /**
     * a way to assert a full-page screenshot
     *
     * @param   driver                      WebDriver instance
     * @param   expectedImageFolderPath     Optional: String - path to the folder containing the expected image. For example: "expected".
     *                                      If omitted, the "screenshots" folder will be used.
     * @param   expectedImageName           Optional: String - the name of the expected image file, including the .png file extension.
     *                                      If omitted, a file name based on the URL will be used.
     * @param   diffImageFolderPath         Optional: String - file path to the folder for the diff Image.
     *                                      If omitted, the "screenshots" folder will be used.
     * @param   diffImageName           Optional: String - name for the diffImage. The file extension should not be included.
     *                                  If omitted, it will create a file name ending in "-DIFF_IMAGE.png"
     * @param   deviation               Double - threshold or tolerance level considered acceptable before a difference is reported.
     *                                  If omitted, the tolerance level will be 0.0. For example: 0.1
     *
     * @return  Boolean                 Returns true if assertion succeeds, returns false if it fails.
     */
    public static Boolean compareScreenshotFP(WebDriver driver, String expectedImageFolderPath, String expectedImageName, String diffImageFolderPath, String diffImageName, Double deviation) {
        String expectedImagePath = expectedImageFolderPath + File.separator + expectedImageName;
        String diffImagePath = diffImageFolderPath + File.separator + diffImageName;
        BufferedImage expectedImage = null;
        try {
            expectedImage = ImageIO.read(new File(expectedImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Shutterbug.shootPage(driver, ScrollStrategy.WHOLE_PAGE).equalsWithDiff(expectedImage, diffImagePath, deviation);
    }
    public static Boolean compareScreenshotFP(WebDriver driver, String expectedImageFolderPath, String expectedImageName, String diffImageFolderPath, String diffImageName) {
        Double deviation = 0.0;
        return compareScreenshotFP(driver, expectedImageFolderPath, expectedImageName, diffImageFolderPath, diffImageName, deviation);
    }
    public static Boolean compareScreenshotFP(WebDriver driver, String expectedImageFolder, String diffImageFolder, Double deviation) {
        String expectedImageName = convertUrlToFileName(driver.getCurrentUrl());
        String diffImageName = expectedImageName + "-DIFF_IMAGE";
        expectedImageName += ".png";
        return compareScreenshotFP(driver, expectedImageFolder, expectedImageName, diffImageFolder, diffImageName, deviation);
    }
    public static Boolean compareScreenshotFP(WebDriver driver, String expectedImageFolderPath, String diffImageFolderPath) {
        Double deviation = 0.0;
        return compareScreenshotFP(driver, expectedImageFolderPath, diffImageFolderPath, deviation);
    }
    public static Boolean compareScreenshotFP(WebDriver driver, Double deviation) {
        String expectedImageFolderPath = "screenshots";
        String diffImageFolderPath = "screenshots";
        return compareScreenshotFP(driver, expectedImageFolderPath, diffImageFolderPath, deviation);
    }
    public static Boolean compareScreenshotFP(WebDriver driver) {
        Double deviation = 0.0;
        return compareScreenshotFP(driver, deviation);
    }

        /**
         * an alternate method to create a full-page screenshot, designed to be used with compareScreenshotFP
         *
         * @param   driver      WebDriver instance
         * @param   fileName    String, name of image file to be created
         *                      If the fileName is omitted, the default value of the URL will be used (converted to remove forbidden characters)
         * @param   folderPath  String, path to the folder that will contain the screenshots
         *                      If the folderPath is omitted, the default value of "screenshots" will be used.
         */
    public static void screenshotFP(WebDriver driver, String folderPath, String fileName){
        shootPage(driver, ScrollStrategy.WHOLE_PAGE).withName(fileName).save(folderPath);
    }
    public static void screenshotFP(WebDriver driver, String folderPath){
        String url = driver.getCurrentUrl();
        String fileName = convertUrlToFileName(url);
        screenshotFP(driver, folderPath, fileName);
    }
    public static void screenshotFP(WebDriver driver){
        String folderPath = "screenshots";
        screenshotFP(driver, folderPath);
    }

    /**
     * converts a url String to a file name string, removing forbidden characters. For use with compareScreenshotFP
     *
     * @param url   String of the URL
     */
    private static String convertUrlToFileName(String url){
        url = url.replaceFirst("https?://","");
        url = url.replaceAll("[?|*:<>\"/\\\\]","-");
        url = truncate(url,159);//Windows file name limit
        return url;
    }

    private static String truncate(String value, int length) {
        // Ensure String length is longer than requested size.
        if (value.length() > length) {
            return value.substring(0, length);
        } else {
            return value;
        }//resource: https://www.dotnetperls.com/truncate-java
    }

    static void echo(Object object){
        System.out.println("******************start echo*****************");
        System.out.println(object);
        System.out.println("******************end echo*****************");
    }//\\
}
