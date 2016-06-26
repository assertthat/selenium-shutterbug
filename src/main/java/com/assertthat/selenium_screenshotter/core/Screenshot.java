package com.assertthat.selenium_screenshotter.core;

import com.assertthat.selenium_screenshotter.utils.file.FileUtil;
import com.assertthat.selenium_screenshotter.utils.image.ImageProcessor;
import com.assertthat.selenium_screenshotter.utils.web.Browser;
import com.assertthat.selenium_screenshotter.utils.web.ScrollStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public abstract class Screenshot<T extends Screenshot<T>> {

    private static final String extension = "PNG";
    protected BufferedImage image;
    protected BufferedImage thumbnailImage;
    protected WebDriver driver;
    private String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"))
            + "." + extension.toLowerCase();
    private Path location = Paths.get("./screenshots/");
    private String title;

    protected abstract T self();

    /**
     * @param name file name of the resulted image
     *             by default will be timestamp in format: 'yyyy_MM_dd_HH_mm_ss_SSS'.
     * @return instance of type Screenshot
     */
    public T withName(String name) {
        if (name != null) {
            fileName = name + "." + extension.toLowerCase();
        }
        return self();
    }

    /**
     * @param title title of the resulted image.
     *              Won't be assigned by default.
     * @return instance of type Screenshot
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
     * @return instance of type Screenshot
     */
    public T withThumbnail(String path, String name, double scale) {
        File thumbnailFile = new File(path.toString(), name);
        thumbnailFile.mkdirs();
        thumbnailImage=ImageProcessor.scale(image,0.5);
        FileUtil.writeImage(thumbnailImage, extension, thumbnailFile);
        return self();
    }

    /**
     * Generate a thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param scale to apply
     * @return instance of type Screenshot
     */
    public T withThumbnail(double scale) {
        withThumbnail(Paths.get(location.toString(),"./thumbnails").toString(),"thumb_"+fileName,scale);
        return self();
    }

    /**
     * Apply gray-and-white filter to the image.
     *
     * @return instance of type Screenshot
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
        screenshotFile.mkdirs();
        if (title != null && !title.isEmpty()) {
            image = ImageProcessor.addTitle(image, title, Color.red, new Font("Serif", Font.BOLD, 20));
        }
        FileUtil.writeImage(image, extension, screenshotFile);
    }

    /**
     * Final method to be called in the chain.
     * Actually saves processed image to the specified path.
     */
    public void save(String path) {
        this.location = Paths.get(path);
        save();
    }

    /**
     * @param o Object to compare with
     * @param deviation allowed deviation while comparing.
     * @return true if the the percentage of differences
     * between current image and provided one is less than or equal to <b>deviation</b>
     */
    public boolean equals(Object o, double deviation) {
        if (this == o) return true;
        if (!(o instanceof Screenshot)) return false;

        Screenshot<?> that = (Screenshot<?>) o;

        return getImage() != null ? ImageProcessor.imagesAreEquals(getImage(), that.getImage(), deviation) : that.getImage() == null;
    }

    /**
     * @param o Object to compare with
     * @return true if the the provided object is of type Screenshot
     * and images are strictly equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Screenshot)) return false;

        Screenshot<?> that = (Screenshot<?>) o;

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
     * @return
     */
    @Override
    public int hashCode() {
        return getImage() != null ? getImage().hashCode() : 0;
    }
}
