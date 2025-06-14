/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.web.Browser;
import com.assertthat.selenium_shutterbug.utils.web.Coordinates;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.function.Function;

public class Configurations {
    /**
     * by default Shutterbug sets java.awt.headless=true
     * to avoid exception "Could not initialize class sun.awt.X11GraphicsEnvironment"
     * Method is to delete already set property
     */
    public Configurations headless(boolean isHeadless) {
        if (!isHeadless) {
            System.clearProperty("java.awt.headless");
        }
        return this;
    }
}
