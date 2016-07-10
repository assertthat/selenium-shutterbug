/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class ElementSnapshot extends Snapshot {

    ElementSnapshot(WebDriver driver) {
        this.driver = driver;
    }

    @Override
    protected ElementSnapshot self() {
        return this;
    }
}
