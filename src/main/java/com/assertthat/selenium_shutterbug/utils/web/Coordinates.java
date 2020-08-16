/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.web;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class Coordinates {

    private final int width;
    private final int scrollWidth;
    private final int height;
    private final int scrollHeight;
    private final int x;
    private final int y;
    private final int absoluteX;
    private final int absoluteY;

    public Coordinates(WebElement element,
                       Double devicePixelRatio) {
        Point point = element.getLocation();
        Dimension size = element.getSize();
        this.width = (int) (size.getWidth() * devicePixelRatio);
        this.height = (int) (size.getHeight() * devicePixelRatio);
        this.x = (int) (point.getX() * devicePixelRatio);
        this.y = (int) (point.getY() * devicePixelRatio);
        this.scrollWidth = (int) (size.getWidth() * devicePixelRatio);
        this.scrollHeight = (int) (size.getHeight() * devicePixelRatio);
        this.absoluteX = (int) (point.getX() * devicePixelRatio);
        this.absoluteY = (int) (point.getY() * devicePixelRatio);
    }

    public Coordinates(Point absoluteLocation,Point currentLocation,
                       Dimension size,
                       Dimension scrollableSize,
                       Double devicePixelRatio) {
        this.width = (int) (size.getWidth() * devicePixelRatio);
        this.height = (int) (size.getHeight() * devicePixelRatio);
        this.absoluteX = (int) (absoluteLocation.getX() * devicePixelRatio);
        this.absoluteY = (int) (absoluteLocation.getY() * devicePixelRatio);
        this.x = (int) (currentLocation.getX() * devicePixelRatio);
        this.y = (int) (currentLocation.getY() * devicePixelRatio);
        this.scrollWidth = (int) (scrollableSize.getWidth() * devicePixelRatio);
        this.scrollHeight = (int) (scrollableSize.getHeight() * devicePixelRatio);
    }

    public int getAbsoluteX() {
        return absoluteX;
    }

    public int getAbsoluteY() {
        return absoluteY;
    }

    public int getWidth() {
        return width;
    }

    public int getScrollHeight() {
        return scrollHeight;
    }


    public int getScrollWidth() {
        return scrollWidth;
    }

    public int getHeight() {
        return height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
