package com.assertthat.shutterbug.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class ElementSnapshot extends Snapshot {

    ElementSnapshot(WebDriver driver, WebElement element) {
        this.driver = driver;
    }

    @Override
    protected ElementSnapshot self() {
        return this;
    }
}
