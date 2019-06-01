package com.assertthat.selenium_shutterbug.utils.image.model;

import com.assertthat.selenium_shutterbug.utils.file.FileUtil;
import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

@Getter
public class ImageData {
    private final int RED_RGB = new Color(255, 0, 0).getRGB();
    private final BufferedImage image;
    private final int width;
    private final int height;

    public ImageData(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    public boolean notEqualsDimensions(ImageData imageData) {
        return !equalsDimensions(imageData);
    }

    private boolean equalsDimensions(ImageData imageData) {
        return this.width == imageData.width && this.height == imageData.height;
    }

    public boolean equalsEachPixelsWithCreateDifferencesImage(ImageData imageData, double deviation, String pathDifferenceImageFileName) {
        return equalsEachPixelsWithCreateDifferencesImage(imageData.getImage(), deviation, pathDifferenceImageFileName);
    }

    private boolean equalsEachPixelsWithCreateDifferencesImage(BufferedImage image, double deviation, String pathDifferenceImageFileName) {
        createDifferencesImage(image, pathDifferenceImageFileName);
        return equalsEachPixels(image, deviation);
    }

    private void createDifferencesImage(BufferedImage image, String pathDifferenceImageFileName) {
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = this.getImage().getRGB(x, y);
                int rgb2 = image.getRGB(x, y);

                // If difference > recorded difference, change pixel to red. If zero, set to image 1's original pixel
                if (rgb1 != rgb2)
                    output.setRGB(x, y, RED_RGB & rgb1); // Dark red = original position, Light red is moved to
                else
                    output.setRGB(x, y, rgb1);
            }
        }

        FileUtil.writeImage(output, "png", new File(pathDifferenceImageFileName + ".png"));
    }

    public boolean equalsEachPixels(ImageData imageData, double deviation) {
        return equalsEachPixels(imageData.getImage(), deviation);
    }

    private boolean equalsEachPixels(BufferedImage image, double deviation) {
        double p = calculatePixelsDifference(image);

        return p == 0 || p <= deviation;
    }

    private double calculatePixelsDifference(BufferedImage image) {
        long diff = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = this.getImage().getRGB(x, y);
                int rgb2 = image.getRGB(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = (rgb1) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = (rgb2) & 0xff;
                diff += Math.abs(r1 - r2);
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
            }
        }
        double n = width * height * 3;

        return diff / n / 255.0;
    }
}
