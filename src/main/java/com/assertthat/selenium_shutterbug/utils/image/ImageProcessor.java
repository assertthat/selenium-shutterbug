/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.image;

import com.assertthat.selenium_shutterbug.utils.image.model.ImageData;
import com.assertthat.selenium_shutterbug.utils.web.Coordinates;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class ImageProcessor {

    private static final int ARCH_SIZE = 10;
    private static final float[] matrix = new float[49];
    private static double pixelError = Double.MAX_VALUE;

    static {
        for (int i = 0; i < 49; i++)
            matrix[i] = 1.0f / 49.0f;
    }

    public static BufferedImage blur(BufferedImage sourceImage) {
        BufferedImageOp options = new ConvolveOp(new Kernel(7, 7, matrix), ConvolveOp.EDGE_NO_OP, null);
        return options.filter(sourceImage, null);
    }

    public static BufferedImage highlight(BufferedImage sourceImage, Coordinates coords, Color color, int lineWidth) {
        byte defaultLineWidth = 3;
        Graphics2D g = sourceImage.createGraphics();
        g.setPaint(color);
        g.setStroke(new BasicStroke(lineWidth == 0 ? defaultLineWidth : lineWidth));
        g.drawRoundRect(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight(), ARCH_SIZE, ARCH_SIZE);
        g.dispose();
        return sourceImage;
    }

    public static BufferedImage addText(BufferedImage sourceImage, int x, int y, String text, Color color, Font font) {
        Graphics2D g = sourceImage.createGraphics();
        g.setPaint(color);
        g.setFont(font);
        g.drawString(text, x, y);
        g.dispose();
        return sourceImage;
    }

    public static BufferedImage getElement(BufferedImage sourceImage, Coordinates coords) {
        return sourceImage.getSubimage(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight());
    }

    public static BufferedImage blurArea(BufferedImage sourceImage, Coordinates coords) {
        BufferedImage blurredImage = blur(sourceImage.getSubimage(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight()));
        return getBufferedImage(sourceImage, coords, blurredImage, sourceImage);
    }

    public static BufferedImage monochromeArea(BufferedImage sourceImage, Coordinates coords) {
        BufferedImage monochromedImage = convertToGrayAndWhite(sourceImage.getSubimage(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight()));
        return getBufferedImage(sourceImage, coords, monochromedImage, sourceImage);
    }

    public static BufferedImage blurExceptArea(BufferedImage sourceImage, Coordinates coords) {
        BufferedImage subImage = sourceImage.getSubimage(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight());
        BufferedImage blurredImage = blur(sourceImage);
        return getBufferedImage(sourceImage, coords, subImage, blurredImage);
    }

    private static BufferedImage getBufferedImage(BufferedImage sourceImage, Coordinates coords, BufferedImage subImage, BufferedImage blurredImage) {
        BufferedImage combined = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(blurredImage, 0, 0, null);
        g.drawImage(subImage, coords.getX(), coords.getY(), null);
        g.dispose();
        return combined;
    }

    public static BufferedImage cropAround(BufferedImage sourceImage, Coordinates coords, int offsetX, int offsetY) {
        return sourceImage.getSubimage(coords.getX() - offsetX, coords.getY() - offsetY, coords.getWidth() + offsetX * 2, coords.getHeight() + offsetY * 2);
    }

    public static BufferedImage addTitle(BufferedImage sourceImage, String title, Color color, Font textFont) {
        int textOffset = 5;
        BufferedImage combined = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight() + textFont.getSize(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(sourceImage, 0, textFont.getSize() + textOffset, null);
        addText(combined, 0, textFont.getSize(), title, color, textFont);
        g.dispose();
        return combined;
    }

    public static BufferedImage convertToGrayAndWhite(BufferedImage sourceImage) {
        ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        op.filter(sourceImage, sourceImage);
        return sourceImage;
    }

    public static boolean imagesAreEquals(BufferedImage image1, BufferedImage image2, double deviation) {
        ImageData image1Data = new ImageData(image1);
        ImageData image2Data = new ImageData(image2);
        if (image1Data.notEqualsDimensions(image2Data)) {
            throw new UnableToCompareImagesException("Images dimensions mismatch: image1 - " + image1Data.getWidth() + "x" + image1Data.getHeight() + "; image2 - " + image2Data.getWidth() + "x" + image2Data.getHeight());
        }
        return image1Data.equalsEachPixels(image2Data, deviation);
    }

    /**
     * Extends the functionality of imagesAreEqualsWithDiff, but creates a third BufferedImage and applies pixel manipulation to it.
     *
     * @param image1       The first image to compare
     * @param image2       The second image to compare
     * @param pathFileName The output path filename for the third image, if null then is ignored
     * @param deviation    The upper limit of the pixel deviation for the test
     * @return If the test passes
     */
    public static boolean imagesAreEqualsWithDiff(BufferedImage image1, BufferedImage image2, String pathFileName, double deviation) {
        ImageData image1Data = new ImageData(image1);
        ImageData image2Data = new ImageData(image2);
        if (image1Data.notEqualsDimensions(image2Data)) {
            throw new UnableToCompareImagesException("Images dimensions mismatch: image1 - " + image1Data.getWidth() + "x" + image1Data.getHeight() + "; image2 - " + image2Data.getWidth() + "x" + image2Data.getHeight());
        }
        return image1Data.equalsEachPixelsWithCreateDifferencesImage(image2Data, deviation, pathFileName);
    }

    public static BufferedImage scale(BufferedImage source, double ratio) {
        return cropAndScale(source, ratio, 1.0, 1.0);
    }

    public static BufferedImage cropAndScale(BufferedImage source, double ratio, double cropWidth, double cropHeight) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        BufferedImage scaledImage = createAndDrawImage(source, w, h);
        return scaledImage.getSubimage(0, 0, (int) (w * cropWidth), (int) (h * cropHeight));
    }

    public static BufferedImage cropAndScale(BufferedImage source, double ratio, int maxWidth, int maxHeight) {
        int w = (int) (source.getWidth() * ratio);
        int h = (int) (source.getHeight() * ratio);
        BufferedImage scaledImage = createAndDrawImage(source, w, h);
        if (maxWidth != -1 && w > maxWidth) {
            w = maxWidth;
        }
        if (maxHeight != -1 && h > maxHeight) {
            h = maxHeight;
        }
        return scaledImage.getSubimage(0, 0, w, h);
    }

    private static BufferedImage createAndDrawImage(BufferedImage source, int w, int h) {
        BufferedImage scaledImage = getCompatibleImage(w, h, source);
        Graphics2D resultGraphics = scaledImage.createGraphics();
        resultGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        resultGraphics.drawImage(source, 0, 0, w, h, null);
        resultGraphics.dispose();
        return scaledImage;
    }

    private static BufferedImage getCompatibleImage(int w, int h, BufferedImage source) {
        BufferedImage bImage = null;
        try {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            bImage = gc.createCompatibleImage(w, h);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bImage == null) {
            boolean hasAlpha = hasAlpha(source);
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bImage = new BufferedImage(w, h, type);
        }
        return bImage;
    }

    public static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bImage = (BufferedImage) image;
            return bImage.getColorModel().hasAlpha();
        }
        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException ignored) {
        }
        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
}
