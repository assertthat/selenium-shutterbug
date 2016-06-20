package com.assertthat.selenium_screnshotter.utils.web;

import org.openqa.selenium.WebDriverException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class UnableTakeScreenshotException extends WebDriverException {

    public UnableTakeScreenshotException() {
        super();
    }

    public UnableTakeScreenshotException(String message) {
        super(message);
    }

    public UnableTakeScreenshotException(Throwable cause) {
        super(cause);
    }

    public UnableTakeScreenshotException(String message, Throwable cause) {
        super(message, cause);
    }
}
