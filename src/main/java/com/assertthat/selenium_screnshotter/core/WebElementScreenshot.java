package com.assertthat.selenium_screnshotter.core;

import org.openqa.selenium.*;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class WebElementScreenshot extends Screenshot<WebElementScreenshot> {

    WebElementScreenshot(WebDriver driver, WebElement element) {
        this.driver = driver;
    }

    @Override
    protected WebElementScreenshot self() {
        return this;
    }
}