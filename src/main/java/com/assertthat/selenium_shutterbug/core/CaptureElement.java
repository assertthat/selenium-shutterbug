package com.assertthat.selenium_shutterbug.core;

public enum CaptureElement {
    FULL_SCROLL, //full element/frame screenshot using scroll & stitch method
    VIEWPORT, //capture visible part of the viewport only
    VERTICAL_SCROLL, //vertical scroll element/frame screenshot using scroll & stitch method
    HORIZONTAL_SCROLL //horizontal scroll element/frame screenshot using scroll & stitch method
}