/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.web;

import org.openqa.selenium.WebDriverException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class UnableTakeSnapshotException extends WebDriverException {

    public UnableTakeSnapshotException() {
        super();
    }

    public UnableTakeSnapshotException(String message) {
        super(message);
    }

    public UnableTakeSnapshotException(Throwable cause) {
        super(cause);
    }

    public UnableTakeSnapshotException(String message, Throwable cause) {
        super(message, cause);
    }
}
