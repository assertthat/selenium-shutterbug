/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.assertthat.selenium_shutterbug.utils.web;

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
