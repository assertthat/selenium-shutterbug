/*
 * Distributed under the terms of the MIT License.
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.image.ImageProcessor;
import com.assertthat.selenium_shutterbug.utils.web.Coordinates;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertTrue;

public class PageSnapshotTest {

    @Test
    public void highlightWithTextUsesProvidedLineWidth() {
        WebElement element = syntheticElement();
        BufferedImage actualImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        PageSnapshot snapshot = new PageSnapshot(null, 1D);
        snapshot.image = actualImage;

        snapshot.highlightWithText(element, Color.BLUE, 9, "", Color.BLUE,
                new Font("SansSerif", Font.PLAIN, 1));

        BufferedImage expectedImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
        ImageProcessor.highlight(expectedImage, new Coordinates(element, 1D), Color.BLUE, 9);
        assertTrue("highlightWithText should honor the requested line width",
                ImageProcessor.imagesAreEquals(expectedImage, snapshot.getImage(), 0D));
    }

    private WebElement syntheticElement() {
        return (WebElement) Proxy.newProxyInstance(
                WebElement.class.getClassLoader(),
                new Class<?>[]{WebElement.class},
                (proxy, method, args) -> {
                    if ("getLocation".equals(method.getName())) {
                        return new Point(10, 10);
                    }
                    if ("getSize".equals(method.getName())) {
                        return new Dimension(20, 20);
                    }
                    if ("toString".equals(method.getName())) {
                        return "synthetic element";
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
    }
}
