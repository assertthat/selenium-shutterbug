/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.core;

import com.assertthat.selenium_shutterbug.utils.file.FileUtil;
import com.assertthat.selenium_shutterbug.utils.image.ImageProcessor;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public abstract class Snapshot<T extends Snapshot> {

    private static final String EXTENSION = "PNG";
    protected static final String ELEMENT_OUT_OF_VIEWPORT_EX_MESSAGE = "Requested element is outside the viewport";
    protected BufferedImage image;
    protected BufferedImage thumbnailImage;
    protected WebDriver driver;
    protected Double devicePixelRatio = 1D;
    private String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(new Date())
            + "." + EXTENSION.toLowerCase();
    private Path location = Paths.get("./screenshots/");
    private String title;

    protected abstract T self();

    /**
     * @param name file name of the resulted image
     *             by default will be timestamp in format: 'yyyy_MM_dd_HH_mm_ss_SSS'.
     * @return instance of type Snapshot
     */
    public T withName(String name) {
        if (name != null) {
            fileName = name + "." + EXTENSION.toLowerCase();
        }
        return self();
    }

    /**
     * @param title title of the resulted image.
     *              Won't be assigned by default.
     * @return instance of type Snapshot
     */
    public T withTitle(String title) {
        this.title = title;
        return self();
    }


    /**
     * Generate a thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param path to save thumbnail image to
     * @param name of the resulting image
     * @param scale to apply
     * @return instance of type Snapshot
     */
    public T withThumbnail(String path, String name, double scale) {
        File thumbnailFile = new File(path, name);
        if(!Files.exists(Paths.get(path))) {
            thumbnailFile.mkdirs();
        }
        thumbnailImage=ImageProcessor.scale(image,scale);
        FileUtil.writeImage(thumbnailImage, EXTENSION, thumbnailFile);
        return self();
    }
    
    /**
     * Generate cropped thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param path to save thumbnail image to
     * @param name of the resulting image
     * @param scale to apply
     * @param cropWidth e.g. 0.2 will leave 20% of the initial width
     * @param cropHeight e.g. 0.1 will leave 10% of the initial width
     * @return instance of type Snapshot
     */
    public T withCroppedThumbnail(String path, String name, double scale,  double cropWidth, double cropHeight) {
        File thumbnailFile = getFile(path, name);
        thumbnailImage=ImageProcessor.cropAndScale(image,scale, cropWidth, cropHeight);
        FileUtil.writeImage(thumbnailImage, EXTENSION, thumbnailFile);
        return self();
    }

    /**
     * Generate cropped thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param path to save thumbnail image to
     * @param name of the resulting image
     * @param scale to apply
     * @param maxWidth max width in pixels. If set to -1 the actual image width is used
     * @param maxHeight max height in pixels. If set to -1 the actual image height is used
     * @return instance of type Snapshot
     */
    public T withCroppedThumbnail(String path, String name, double scale,  int maxWidth, int maxHeight) {
        File thumbnailFile = getFile(path, name);
        thumbnailImage=ImageProcessor.cropAndScale(image,scale, maxWidth, maxHeight);
        FileUtil.writeImage(thumbnailImage, EXTENSION, thumbnailFile);
        return self();
    }

    /**
     * Generate file for cropped thumbnail of the original screenshot.
     *
     * @param path to save thumbnail image to
     * @param name of the resulting image
     * @return instance of type File
     */
    private File getFile(String path, String name) {
        File thumbnailFile = new File(path, name);
        if (!Files.exists(Paths.get(path))) {
            thumbnailFile.mkdirs();
        }
        return thumbnailFile;
    }

    /**
     * Generate cropped thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param scale to apply
     * @param cropWidth e.g. 0.2 will leave 20% of the initial width
     * @param cropHeight e.g. 0.1 will leave 10% of the initial width
     * @return instance of type Snapshot
     */
    public T withCroppedThumbnail(double scale, double cropWidth, double cropHeight) {
        return withCroppedThumbnail(Paths.get(location.toString(), "./thumbnails").toString(), "thumb_" + fileName, scale,cropWidth,cropHeight);
    }
    
    /**
     * Generate cropped thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param scale to apply
     * @param maxWidth max width in pixels. If set to -1 the actual image width is used
     * @param maxHeight max height in pixels. If set to -1 the actual image height is used
     * @return instance of type Snapshot
     */
    public T withCroppedThumbnail(double scale, int maxWidth, int maxHeight) {
        return withCroppedThumbnail(Paths.get(location.toString(), "./thumbnails").toString(), "thumb_" + fileName, scale, maxWidth,maxHeight);
    }
    
    /**
     * Generate a thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param path to save thumbnail image to
     * @param name of the resulting image
     * @param scale to apply
     * @return instance of type Snapshot
     */
    public T withThumbnail(Path path, String name, double scale) {
       return withThumbnail(path.toString(),name,scale);
    }

    /**
     * Generate a thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param scale to apply
     * @return instance of type Snapshot
     */
    public T withThumbnail(double scale) {
        return withThumbnail(Paths.get(location.toString(), "./thumbnails").toString(), "thumb_" + fileName, scale);
    }

    /**
     * Apply gray-and-white filter to the image.
     *
     * @return instance of type Snapshot
     */
    public T monochrome() {
        this.image = ImageProcessor.convertToGrayAndWhite(this.image);
        return self();
    }

    /**
     * @return BufferedImage - current image being processed.
     */
    public BufferedImage getImage() {
        return image;
    }

    protected void setImage(BufferedImage image) {
        self().image = image;
    }

    /**
     * Final method to be called in the chain.
     * Actually saves processed image to the default location: ./screenshots
     */
    public void save() {
        File screenshotFile = new File(location.toString(), fileName);
        if(!Files.exists(location)) {
            screenshotFile.mkdirs();
        }
        if (title != null && !title.isEmpty()) {
            image = ImageProcessor.addTitle(image, title, Color.red, new Font("Serif", Font.BOLD, 20));
        }
        FileUtil.writeImage(image, EXTENSION, screenshotFile);
    }

    /**
     * Final method to be called in the chain.
     * Actually saves processed image to the specified path.
	 * @param path to save image to 
     */
    public void save(String path) {
        this.location = Paths.get(path);
        save();
    }

    /**
     * @param other Snapshot to compare with
     * @param deviation allowed deviation while comparing.
     * @return true if the the percentage of differences
     * between current image and provided one is less than or equal to <b>deviation</b>
     */
    public boolean equals(Snapshot other, double deviation) {
        if (this == other) return true;
        return getImage() != null ? ImageProcessor.imagesAreEquals(getImage(), other.getImage(), deviation) : other.getImage() == null;
    }

    /**
     * @param o Object to compare with
     * @return true if the the provided object is of type Snapshot
     * and images are strictly equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Snapshot)) return false;

        Snapshot that = (Snapshot) o;

        return getImage() != null ? ImageProcessor.imagesAreEquals(getImage(), that.getImage(), 0) : that.getImage() == null;
    }

    /**
     * @param image BufferedImage to compare with.
     * @return true if the the provided image and current image are strictly equal.
     */
    public boolean equals(BufferedImage image) {
        if (this.getImage() == image) return true;
        return getImage() != null ? ImageProcessor.imagesAreEquals(getImage(), image, 0) : image == null;
    }

    /**
     * @param image BufferedImage to compare with.
     * @param deviation allowed deviation while comparing.
     * @return true if the the percentage of differences
     * between current image and provided one is less than or equal to <b>deviation</b>
     */
    public boolean equals(BufferedImage image, double deviation) {
        if (this.getImage() == image) return true;
        return getImage() != null ? ImageProcessor.imagesAreEquals(getImage(), image, deviation) : image == null;
    }

    /**
     * @param image BufferedImage to compare with.
     * @param resultingImagePath path with name to save to resulting images with diff
     * @return true if the the provided image and current image are strictly equal.
     */
    public boolean equalsWithDiff(BufferedImage image, String resultingImagePath) {
        if (this.getImage() == image) return true;
        return getImage() != null ? ImageProcessor.imagesAreEqualsWithDiff(getImage(), image, resultingImagePath, 0) : image == null;
    }

    /**
     * @param image BufferedImage to compare with.
     * @param resultingImagePath path with name to save to resulting images with diff
     * @param deviation allowed deviation while comparing
     * @return true if the the provided image and current image are strictly equal.
     */
    public boolean equalsWithDiff(BufferedImage image, String resultingImagePath, double deviation) {
        if (this.getImage() == image) return true;
        return getImage() != null ? ImageProcessor.imagesAreEqualsWithDiff(getImage(), image, resultingImagePath, deviation) : image == null;
    }

    /**
     * @param image Snapshot to compare with.
     * @param resultingImagePath path with name to save to resulting images with diff
     * @return true if the the provided image and current image are strictly equal.
     */
    public boolean equalsWithDiff(Snapshot image, String resultingImagePath) {
        if (this == image) return true;
        return getImage() != null ? ImageProcessor.imagesAreEqualsWithDiff(getImage(), image.getImage(),resultingImagePath, 0) : image == null;
    }

    /**
     * @param image Snapshot to compare with.
     * @param resultingImagePath path with name to save to resulting images with diff
     * @param deviation allowed deviation while comparing
     * @return true if the the provided image and current image are strictly equal.
     */
    public boolean equalsWithDiff(Snapshot image, String resultingImagePath, double deviation) {
        if (this == image) return true;
        return getImage() != null ? ImageProcessor.imagesAreEqualsWithDiff(getImage(), image.getImage(),resultingImagePath, deviation) : image == null;
    }

    /**
     * @return image hash code
     */
    @Override
    public int hashCode() {
        return getImage() != null ? getImage().hashCode() : 0;
    }
}
