package ui.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MainPage extends BasePage{

    public MainPage(WebDriver webDriver) {
        super(webDriver);
    }

    private By dropDownProfile = By.className("oxd-userdropdown-tab");

    public boolean isDropDownProfileDisplayed(){
        return webDriver.findElement(dropDownProfile).isDisplayed();
    }
}
