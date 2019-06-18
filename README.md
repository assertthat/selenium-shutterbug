# selenium-shutterbug

[![Build Status](https://travis-ci.org/assertthat/selenium-shutterbug.svg?branch=master)](https://travis-ci.org/assertthat/selenium-shutterbug)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.assertthat/selenium-shutterbug/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.assertthat/selenium-shutterbug)

## Synopsis

Selenium Shutterbug is a utility library written in Java for making screenshots using [Selenium WebDriver](http://www.seleniumhq.org/projects/webdriver/ "SeleniumHQ WebDriver page") and further customizing, comparing and processing them with the help of  [Java AWT](https://en.wikipedia.org/wiki/Abstract_Window_Toolkit "AWT wiki").

## Code Example

Screenshot of the page with scrolling (for Chrome to make screenshot of the whole page but not viewport only):
```
Shutterbug.shootPage(driver, ScrollStrategy.WHOLE_PAGE).save("C:\\testing\\screenshots\\");
```
Highlighting, adding titles, text, etc:
```
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
```
More examples [here](https://github.com/assertthat/selenium-shutterbug/wiki/Examples-of-usage) 

## Motivation

The idea behind the project is to make testers life easier by enabling them to create descriptive screenshots which, in some cases, could be directly attached to the bug reports or serve as a source of information about system state at a specific moment of time. 

## Installation

The project is available in [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22selenium-shutterbug%22 "Maven Central project location")

##### Maven dependency

```xml
<dependency>
    <groupId>com.assertthat</groupId>
    <artifactId>selenium-shutterbug</artifactId>
    <version>x.x</version>
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
compile ('com.assertthat:selenium-shutterbug:x.x') {
    exclude group: "org.seleniumhq.selenium", name: "selenium-java"
    }
```

##### Using SBT

```
"com.assertthat" % "selenium-shutterbug" % "x.x" exclude("org.seleniumhq.selenium", "selenium-java"),
```
## Contributing

For details please read [CONTRIBUTING](https://github.com/assertthat/selenium-shutterbug/blob/master/CONTRIBUTING.md "CONTRIBUTING")

## License

 Code released under the [MIT license](https://github.com/assertthat/selenium-shutterbug/blob/master/LICENSE "MIT license")
