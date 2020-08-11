# selenium-shutterbug

[![Build Status](https://travis-ci.org/assertthat/selenium-shutterbug.svg?branch=master)](https://travis-ci.org/assertthat/selenium-shutterbug)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.assertthat/selenium-shutterbug/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.assertthat/selenium-shutterbug)

## Synopsis

Selenium Shutterbug is a utility library written in Java for making screenshots using [Selenium WebDriver](http://www.seleniumhq.org/projects/webdriver/ "SeleniumHQ WebDriver page") and further customizing, comparing and processing them with the help of  [Java AWT](https://en.wikipedia.org/wiki/Abstract_Window_Toolkit "AWT wiki").

The idea behind the project is to make testers life easier by enabling them to create descriptive screenshots which, in some cases, could be directly attached to the bug reports or serve as a source of information about system state at a specific moment of time. 

## Installation

The project is available in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22selenium-shutterbug%22 "Maven Central project location")

##### Maven dependency

```xml
<dependency>
    <groupId>com.assertthat</groupId>
    <artifactId>selenium-shutterbug</artifactId>
    <version>1.0</version>
    <exclusions>
        <exclusion>
	    <groupId>org.seleniumhq.selenium</groupId>
	    <artifactId>selenium-java</artifactId>
	</exclusion>
     </exclusions>
</dependency>

```
##### Using Gradle

```
compile ('com.assertthat:selenium-shutterbug:1.0') {
    exclude group: "org.seleniumhq.selenium", name: "selenium-java"
    }
```

##### Using SBT

```
"com.assertthat" % "selenium-shutterbug" % "1.0" exclude("org.seleniumhq
.selenium", "selenium-java"),
```

## Code Example
 
Below are some basic examples of usage.

### Page screenshots
- Take screenshot and save to default location (./screenshots/):
```java
  Shutterbug.shootPage(driver).save();
```
- Take screenshot and specify location to save to:
```java
  Shutterbug.shootPage(driver).save("C:\\testing\\screenshots\\");
```
- Wait for condition before taking screenshot:
```java
   Shutterbug.wait(visibilityOfElementLocated(By.id("someId")), 5).shootPage(driver, Capture.FULL).save();
```
- Take screenshot and scroll in both directions (Will make full page screenshot in Chrome):
```java
  Shutterbug.shootPage(driver, Capture.FULL_SCROLL).save();
```
- Take screenshot and scroll in both directions with half a second scrolling timeout (Will make full page screenshot in Chrome) and use devicePixelRatio - for retina displays:
```java
  Shutterbug.shootPage(driver, Capture.FULL_SCROLL ,500,true).save();
```
- Take screenshot of the whole page using Chrome DevTools. This is applicable for Chrome only. Use this one instead of  ScrollStrategy.WHOLE_PAGE if page has sticky header or any other sticky elements. 
```java
  Shutterbug.shootPage(driver, Capture.FULL,true).save();
```
### WebElement screenshots

- Take screenshot of specified WebElement only:
```java
  Shutterbug.shootElement(driver, element).save();
```

### Screenshots comparison

- Compare screenshot taken with the expected one with specified deviation rate:
```java
  Shutterbug.shootPage(driver).equals(otherImage,0.1);
```
- Compare screenshot taken with the expected one with specified deviation rate and create new image with differences highlighted:
```java
  Shutterbug.shootPage(driver).equalsWithDiff(otherImage,pathToNewImage,0.1);
```
- Compare screenshot taken with the expected one and create new image with differences highlighted:
```java
  Shutterbug.shootPage(driver).equalsWithDiff(otherImage,pathToNewImage);
```

### Screenshots Thumbnails
- Take screenshot and save thumbnail as well (with specified resize ratio):
```java
  Shutterbug.shootPage(driver).withThumbnail(0.4).save();
```

### Frame screenshots
- Take screenshot of scrollable frame locatable by supplied `frameID`:
```java
Shutterbug.shootFrame(driver, "frameID", Capture.FULL_SCROLL).save();
```

- Take screenshot of scrollable frame web element:
```java
Shutterbug.shootFrame(driver, frameWebElement, Capture.FULL_SCROLL).save();
```

### Scrollable WebElements  screenshots


- Take screenshot of scrollable web element. Horizontal capture only:
```java
Shutterbug.shootElement(driver, webElement, Capture.HORIZONTAL_SCROLL).save();
```

### Operations chaining

To demonstrate how it all can be pieced together the example follows:
```java
    System.setProperty("webdriver.chrome.driver", "your path to  chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.google.com/imghp");
        WebElement googleLogo = driver.findElement(By.id("hplogo"));
        WebElement searchBtn = driver.findElement(By.id("sblsbb"));
        WebElement searchBox = driver.findElement(By.className("gsfi"));

        searchBox.sendKeys("Testing");

        Shutterbug.shootPage(driver)
                .blur(searchBox)
                .highlight(searchBtn)
                .monochrome(googleLogo)
                .highlightWithText(googleLogo, Color.blue, 3, "Monochromed logo",Color.blue, new Font("SansSerif", Font.BOLD, 20))
                .highlightWithText(searchBox, "Blurred secret words")
                .withTitle("Google home page - " + new Date())
                .withName("home_page")
                .withThumbnail(0.7)
                .save("C:\\testing\\screenshots\\");
        driver.quit();
```
### Available capture types

 `VIEWPORT` - capture visible part of the viewport only
 
 `FULL` - full page screenshot using devtools
 
 `FULL_SCROLL` - full page/element/frame screenshot using scroll & stitch method
 
 `VERTICAL_SCROLL` - vertical scroll page/element/frame screenshot using scroll
  & stitch method
  
 `HORIZONTAL_SCROLL` - horizontal scroll page/element/frame screenshot using
  scroll & stitch method

## Contributing

For details please read [CONTRIBUTING](https://github.com/assertthat/selenium-shutterbug/blob/master/CONTRIBUTING.md "CONTRIBUTING")

## License

 Code released under the [MIT license](https://github.com/assertthat/selenium-shutterbug/blob/master/LICENSE "MIT license")
