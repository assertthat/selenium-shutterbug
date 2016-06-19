package com.assertthat.selenium_screnshotter.core;

import java.awt.Color;
import java.awt.Font;

import com.assertthat.selenium_screnshotter.utils.ImageProcessor;
import com.assertthat.selenium_screnshotter.utils.WebElementWrapper;
import org.openqa.selenium.*;

/**
 * Created by Glib_Briia on 17/06/2016.
 */
public class WebPageScreenshot extends Screenshot<WebPageScreenshot>{

	WebPageScreenshot(WebDriver driver){
        this.driver = driver;
    }
	
	public WebPageScreenshot highlight(WebElement element){
		highlight(element, Color.red, 3);
		return this;
	}

	public WebPageScreenshot highlight(WebElement element, Color color, int lineWidth){
        WebElementWrapper wrapper = new WebElementWrapper(element);
		image = ImageProcessor.highlight(image,wrapper.getWidth(), wrapper.getHeight(), wrapper.getX(),wrapper.getY(), color, lineWidth);
		return this;
	}
	
	public WebPageScreenshot highlightWithText(WebElement element, String text){
        highlightWithText(element, Color.red, 3, text, Color.red, new Font("Serif", Font.BOLD, 20));
		return this;
	}

	public WebPageScreenshot highlightWithText(WebElement element, Color elementColor, int lineWidth, String text, Color textColor, Font textFont){
        WebElementWrapper wrapper = new WebElementWrapper(element);
		highlight(element, elementColor, 0);
		image = ImageProcessor.addText(image, wrapper.getX(), wrapper.getY()- textFont.getSize()/2, text, textColor, textFont);
		return this;
	}
	
	public WebPageScreenshot blur(){
		image = ImageProcessor.blur(image);
		return this;
	}
	
	public WebPageScreenshot blur(WebElement element){
        WebElementWrapper wrapper = new WebElementWrapper(element);
        image = ImageProcessor.blurArea(image, wrapper.getWidth(), wrapper.getHeight(), wrapper.getX(),wrapper.getY());
        return this;
	}
	
	public WebPageScreenshot blurExcept(WebElement element){
        WebElementWrapper wrapper = new WebElementWrapper(element);
		image = ImageProcessor.blurExceptArea(image, wrapper.getWidth(), wrapper.getHeight(), wrapper.getX(),wrapper.getY());
		return this;
	}

    @Override
    protected WebPageScreenshot self() {
        return this;
    }

}