/*
 * Copyright 2016 Glib Briia
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

package com.assertthat.shutterbug.core;

import com.assertthat.shutterbug.utils.image.ImageProcessor;
import com.assertthat.shutterbug.utils.web.Browser;
import com.assertthat.shutterbug.utils.web.ScrollStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 26/06/2016.
 */
public class Shutterbug {

    /**
     * Make screenshot of the viewport only.
     * To be used when screenshotting the page
     * and don't need to scroll while making screenshots (FF, IE).
     *
     * @param driver WebDriver instance
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver) {
        Browser browser = new Browser(driver);
        PageSnapshot pageScreenshot = new PageSnapshot(driver);
        pageScreenshot.setImage(browser.takeScreenshot());
        return pageScreenshot;
    }

    /**
     * To be used when screenshotting the page
     * and need to scroll while making screenshots, either vertically or
     * horizontally or both directions (Chrome).
     *
     * @param driver WebDriver instance
     * @param scroll ScrollStrategy How you need to scroll
     * @return PageSnapshot instance
     */
    public static PageSnapshot shootPage(WebDriver driver, ScrollStrategy scroll) {
        Browser browser = new Browser(driver);
        PageSnapshot pageScreenshot = new PageSnapshot(driver);
        switch (scroll) {
            case HORIZONTALLY:
                pageScreenshot.setImage(browser.takeScreenshotScrollHorizontally());
                break;
            case VERTICALLY:
                pageScreenshot.setImage(browser.takeScreenshotScrollVertically());
                break;
            case BOTH_DIRECTIONS:
                pageScreenshot.setImage(browser.takeScreenshotEntirePage());
        }
        return pageScreenshot;
    }

    /**
     * To be used when need to screenshot particular element.
     *
     * @param driver  WebDriver instance
     * @param element WebElement instance to be screenshotted
     * @return ElementSnapshot instance
     */
    public static ElementSnapshot shootElement(WebDriver driver, WebElement element) {
        Browser browser = new Browser(driver);
        ElementSnapshot elementScreenshot = new ElementSnapshot(driver, element);
        browser.scrollToElement(element);
        elementScreenshot.setImage(browser.takeScreenshot());
        elementScreenshot.setImage(ImageProcessor.getElement(elementScreenshot.getImage(), browser.getBoundingClientRect(element)));
        return elementScreenshot;
    }
}
