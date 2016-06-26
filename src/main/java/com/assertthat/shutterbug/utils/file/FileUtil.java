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

package com.assertthat.shutterbug.utils.file;


import com.assertthat.shutterbug.utils.web.UnableTakeScreenshotException;
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
