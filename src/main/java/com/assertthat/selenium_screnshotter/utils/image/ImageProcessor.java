package com.assertthat.selenium_screnshotter.utils.image;

import com.assertthat.selenium_screnshotter.utils.web.Coordinates;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class ImageProcessor {

    private static final int ARCH_SIZE = 10;
    private static float[] matrix = new float[49];

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
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, x, y);
        g.dispose();
        return sourceImage;
    }

    public static BufferedImage getElement(BufferedImage sourceImage, Coordinates coords) {
        return sourceImage.getSubimage(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight());
    }

    public static BufferedImage blurArea(BufferedImage sourceImage, Coordinates coords) {
        BufferedImage blurredImage = blur(sourceImage.getSubimage(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight()));
        BufferedImage combined = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(sourceImage, 0, 0, null);
        g.drawImage(blurredImage, coords.getX(), coords.getY(), null);
        g.dispose();
        return combined;
    }

    public static BufferedImage blurExceptArea(BufferedImage sourceImage, Coordinates coords) {
        BufferedImage subImage = sourceImage.getSubimage(coords.getX(), coords.getY(), coords.getWidth(), coords.getHeight());
        BufferedImage blurredImage = blur(sourceImage);
        BufferedImage combined = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(blurredImage, 0, 0, null);
        g.drawImage(subImage, coords.getX(), coords.getY(), null);
        g.dispose();
        return combined;
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

}