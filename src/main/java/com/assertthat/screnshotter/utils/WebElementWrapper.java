package com.assertthat.screnshotter.utils;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;

/**
 * Created by Glib_Briia on 18/06/2016.
 */
public class WebElementWrapper {
    private int width;
    private int height;
    private int x;
    private int y;

    public WebElementWrapper(WebElement element){
        Point point = element.getLocation();
        Dimension size = element.getSize();
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
