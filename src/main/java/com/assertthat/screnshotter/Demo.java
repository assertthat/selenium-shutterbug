package com.assertthat.screnshotter;

import com.assertthat.screnshotter.core.Screenshot;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by Glib_Briia on 18/06/2016.
 */
public class Demo {

    public static void main(String ... args){
        System.setProperty("webdriver.chrome.driver","D:\\Downloads\\chromedriver_win32\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.amazon.co.uk/");
        WebElement element = driver.findElement(By.className("navFooterBackToTopText"));
        Screenshot.page(driver).highlight(element).highlightWithText(element,"This is the text").withTitle("PAHHEHEHEHE").withName("dhfdjfhdjfdk").take();
        driver.quit();
    }
}