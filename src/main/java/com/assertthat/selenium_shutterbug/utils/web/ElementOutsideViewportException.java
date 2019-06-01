package com.assertthat.selenium_shutterbug.utils.web;

/**
 * Created by Glib_Briia on 10/07/2016.
 */
public class ElementOutsideViewportException extends RuntimeException  {

    public ElementOutsideViewportException() {
        super();
    }

    public ElementOutsideViewportException(String message) {
        super(message);
    }

    public ElementOutsideViewportException(Throwable cause) {
        super(cause);
    }

    public ElementOutsideViewportException(String message, Throwable cause) {
        super(message, cause);
    }
}
