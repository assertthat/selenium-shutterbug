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

    public Coordinates(WebElement element,
                       Double devicePixelRatio) {
        Point point = element.getLocation();
        Dimension size = element.getSize();
        this.width = (int) (size.getWidth() * devicePixelRatio);
        this.height = (int) (size.getHeight() * devicePixelRatio);
        this.x = (int) (point.getX() * devicePixelRatio);
        this.y = (int) (point.getY() * devicePixelRatio);
        this.scrollWidth =  (int) (size.getWidth() * devicePixelRatio);
        this.scrollHeight = (int) (size.getHeight() * devicePixelRatio);
    }

    public Coordinates(Point point, Dimension size, int scrollWidth,
                       int scrollHeight, Double devicePixelRatio) {
        this.width = (int) (size.getWidth() * devicePixelRatio);
        this.height = (int) (size.getHeight() * devicePixelRatio);
        this.x = (int) (point.getX() * devicePixelRatio);
        this.y = (int) (point.getY() * devicePixelRatio);
        this.scrollWidth = (int) (scrollWidth * devicePixelRatio);
        this.scrollHeight = (int) (scrollHeight * devicePixelRatio);
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
