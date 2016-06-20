package com.assertthat.selenium_screnshotter.utils.file;


import com.assertthat.selenium_screnshotter.utils.web.UnableTakeScreenshotException;
import org.apache.commons.io.IOUtils;

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
}
