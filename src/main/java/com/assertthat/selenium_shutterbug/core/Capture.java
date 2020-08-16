/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public enum Capture {
    VIEWPORT, //capture visible part of the viewport only
    FULL, // full page screenshot using devtools
    FULL_SCROLL, // full page screenshot using scroll & stitch method
    VERTICAL_SCROLL, //vertical scroll page screenshot using scroll & stitch method
    HORIZONTAL_SCROLL // horizontal scroll page screenshot using scroll & stitch method
}
