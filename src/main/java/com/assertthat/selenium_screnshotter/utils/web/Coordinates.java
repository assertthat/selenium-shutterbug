package com.assertthat.selenium_screnshotter.utils.web;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class Coordinates {

    private int width;
    private int height;
    private int x;
    private int y;

    public Coordinates(WebElement element) {
        Point point = element.getLocation();
        Dimension size = element.getSize();
        this.width = size.getWidth();
        this.height = size.getHeight();
        this.x = point.getX();
        this.y = point.getY();
    }

    public Coordinates(Point point, Dimension size) {
        this.width = size.getWidth();
        this.height = size.getHeight();
        this.x = point.getX();
        this.y = point.getY();
    }


    public int getWidth() {
        return width;
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
