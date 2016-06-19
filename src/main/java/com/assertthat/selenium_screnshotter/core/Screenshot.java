package com.assertthat.selenium_screnshotter.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openqa.selenium.Point;
import org.openqa.selenium.Dimension;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.assertthat.selenium_screnshotter.utils.ImageProcessor;
import com.assertthat.selenium_screnshotter.utils.WebDriverHelper;
import com.assertthat.selenium_screnshotter.utils.WebElementWrapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public abstract class Screenshot<T extends Screenshot<T>> {

    protected BufferedImage image;
    private static final String extension = "PNG";
    private String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSS"))
            +"."+extension.toLowerCase();
    private Path location = Paths.get("./screenshots/");
    private String title;
    protected WebDriver driver;

    protected abstract T self();

	public T withName(String name){
		if(name!=null){
			fileName = name + "." + extension.toLowerCase();
		}
		return self();
	}
	
	public T withTitle(String title){
		this.title = title;
		return self();
	}
	
	public T saveTo(Path path){
		if (Files.exists(path)){
			this.location = path;
		}
		return self();
	}
	
	public T saveTo(String path){
		if (path !=null){
			saveTo(Paths.get(path));
		}
		return self();
	}

    public T monochrome(){
        this.image = ImageProcessor.convertToGrayAndWhite(this.image);
        return self();
    }

    protected void setImage(BufferedImage image){
        self().image = image;
    }

    protected BufferedImage getImage(){
        return image;
    }


	public void take(){
        try {
			File screenshotFile = new File(location.toString(), fileName);
			screenshotFile.mkdirs();
            if(title!=null && !title.isEmpty()){
                image = ImageProcessor.addTitle(image, title, Color.red, new Font("Serif", Font.BOLD, 20));
            }
            ImageIO.write(image, extension, screenshotFile);
        } catch (IOException e) {
            e.printStackTrace();
            //TODO
        }
    }

    public static WebPageScreenshot page(WebDriver driver){
        WebPageScreenshot webPageScreenshot = new WebPageScreenshot(driver);
        try {
            webPageScreenshot.setImage(WebDriverHelper.takeScreenshot(driver));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO
        }
        return webPageScreenshot;
    }

    public static WebElementScreenshot element(WebDriver driver, WebElement element){
        WebElementScreenshot el = new WebElementScreenshot(driver, element);
        try {
            WebDriverHelper.scrollToElement(driver,element);
            el.setImage(WebDriverHelper.takeScreenshot(driver));
            WebElementWrapper wrapper = new WebElementWrapper(element);
            Object[] rect = WebDriverHelper.getBoundingClientRect(element, driver);
            Point start = (Point) rect[0];
            Dimension size = (Dimension) rect[1];
            el.setImage(ImageProcessor.getElement(el.getImage(), wrapper.getWidth(),wrapper.getHeight(), start.getX(),start.getY()));
        } catch (IOException e) {
            e.printStackTrace();
            //TODO
        }
        return el;
    }

}