package com.assertthat.selenium_screnshotter.core;

import org.openqa.selenium.*;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class ElementScreenshot extends Screenshot<ElementScreenshot> {

    ElementScreenshot(WebDriver driver, WebElement element) {
        this.driver = driver;
    }

    @Override
    protected ElementScreenshot self() {
        return this;
    }
}