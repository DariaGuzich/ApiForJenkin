package ui.PageObjects;

import org.openqa.selenium.WebDriver;

public class BasePage {
    protected WebDriver webDriver;


    public BasePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }
}
