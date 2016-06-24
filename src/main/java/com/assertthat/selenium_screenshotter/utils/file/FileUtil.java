package com.assertthat.selenium_screenshotter.utils.file;


import com.assertthat.selenium_screenshotter.utils.web.UnableTakeScreenshotException;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class FileUtil {

    public static String getJsScript(String filePath) {
        try {
            return IOUtils.toString(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
        } catch (IOException e) {
            throw new UnableTakeScreenshotException("Unable to load JS script", e);
        }
    }

    public static void writeImage(BufferedImage imageFile, String extension, File fileToWriteTo) {
        try {
            ImageIO.write(imageFile, extension, fileToWriteTo);
        } catch (IOException e) {
            throw new UnableTakeScreenshotException(e);
        }
    }
}
