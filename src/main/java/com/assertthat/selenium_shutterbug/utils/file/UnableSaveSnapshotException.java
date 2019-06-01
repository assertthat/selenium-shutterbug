/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.file;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class UnableSaveSnapshotException extends RuntimeException {

    public UnableSaveSnapshotException() {
        super();
    }

    public UnableSaveSnapshotException(String message) {
        super(message);
    }

    public UnableSaveSnapshotException(Throwable cause) {
        super(cause);
    }

    public UnableSaveSnapshotException(String message, Throwable cause) {
        super(message, cause);
    }
}
