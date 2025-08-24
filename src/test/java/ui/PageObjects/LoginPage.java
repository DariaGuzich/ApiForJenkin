package ui.PageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final String BASE_URL = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    private By usernameField = By.name("username");
    private By passwordField = By.name("password");
    private By loginButton = By.xpath("//button[@type='submit']");
    private By errorMessage = By.xpath("//p[contains(@class,'oxd-alert-content-text')]");

    public LoginPage(WebDriver webDriver) {
        super(webDriver);
    }

    public void enterUsername(String username){
        webDriver.findElement(usernameField).sendKeys(username);
    }

    public void enterPassword(String password){
        webDriver.findElement(passwordField).sendKeys(password);
    }

    public void clickLoginButton(){
        webDriver.findElement(loginButton).click();
    }

    public boolean isErrorMessageDisplayed(){
        return webDriver.findElement(errorMessage).isDisplayed();
    }

    public void login(String username, String password) {
        webDriver.navigate().to(BASE_URL);
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
}
