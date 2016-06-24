package com.assertthat.selenium_screenshotter.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
