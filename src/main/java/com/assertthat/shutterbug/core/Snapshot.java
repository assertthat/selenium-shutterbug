/*
 * Copyright 2016 Glib Briia
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

package com.assertthat.shutterbug.core;

import com.assertthat.shutterbug.utils.file.FileUtil;
import com.assertthat.shutterbug.utils.image.ImageProcessor;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public abstract class Snapshot<T extends Snapshot> {

    private static final String extension = "PNG";
    protected BufferedImage image;
    protected BufferedImage thumbnailImage;
    protected WebDriver driver;
    private String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS").format(new Date())
            + "." + extension.toLowerCase();
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
            fileName = name + "." + extension.toLowerCase();
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
        File thumbnailFile = new File(path.toString(), name);
        thumbnailFile.mkdirs();
        thumbnailImage=ImageProcessor.scale(image,scale);
        FileUtil.writeImage(thumbnailImage, extension, thumbnailFile);
        return self();
    }

    /**
     * Generate a thumbnail of the original screenshot.
     * Will save different thumbnails depends on when it was called in the chain.
     *
     * @param scale to apply
     * @return instance of type Snapshot
     */
    public T withThumbnail(double scale) {
        withThumbnail(Paths.get(location.toString(),"./thumbnails").toString(),"thumb_"+fileName,scale);
        return self();
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
        if (!(o instanceof Snapshot)) return false;

        Snapshot that = (Snapshot) o;

        return getImage() != null ? ImageProcessor.imagesAreEquals(getImage(), that.getImage(), deviation) : that.getImage() == null;
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
     * @return
     */
    @Override
    public int hashCode() {
        return getImage() != null ? getImage().hashCode() : 0;
    }
}
