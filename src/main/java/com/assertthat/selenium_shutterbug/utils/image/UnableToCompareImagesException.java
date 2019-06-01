/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.image;

/**
 * Created by Glib_Briia on 25/06/2016.
 */
public class UnableToCompareImagesException extends RuntimeException {

    public UnableToCompareImagesException() {
        super();
    }

    public UnableToCompareImagesException(String message) {
        super(message);
    }

    public UnableToCompareImagesException(Throwable cause) {
        super(cause);
    }

    public UnableToCompareImagesException(String message, Throwable cause) {
        super(message, cause);
    }
}
