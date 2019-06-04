/*
 *  Copyright (c) 2016, Glib Briia  <a href="mailto:glib.briia@assertthat.com">Glib Briia</a>
 *  Distributed under the terms of the MIT License
 */

package com.assertthat.selenium_shutterbug.utils.file;


import com.assertthat.selenium_shutterbug.utils.web.UnableTakeSnapshotException;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class FileUtil {

    public static String getJsScript(String filePath) {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            if (is == null) {
                // This is needed to load the files in an OSGI environment when enclosed in a bundle
                is = FileUtil.class.getClassLoader().getResourceAsStream(filePath);
            }
            // if the input stream is still null, this will avoid a non descriptive null pointer exception
            if (is == null) new UnableTakeSnapshotException("Unable to load JS script, unable to locate resource stream.");
            return IOUtils.toString(is);
        } catch (IOException e) {
            throw new UnableTakeSnapshotException("Unable to load JS script", e);
        }
    }

    public static void writeImage(BufferedImage imageFile, String extension, File fileToWriteTo) {
        try {
            ImageIO.write(imageFile, extension, fileToWriteTo);
        } catch (IOException e) {
            throw new UnableSaveSnapshotException(e);
        }
    }
}
