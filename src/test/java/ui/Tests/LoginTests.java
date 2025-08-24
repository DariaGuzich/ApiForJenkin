package ui.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.PageObjects.LoginPage;
import ui.PageObjects.MainPage;

public class LoginTests extends BaseTests {

    @Test
    public void validLoginTest() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.login("Admin", "admin123");
        MainPage mainPage = new MainPage(webDriver);
        Assertions.assertTrue(mainPage.isDropDownProfileDisplayed(), "Main page wasn't properly opened!");
    }

    @Test
    public void invalidLoginTest() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.login("wrongName", "wrongPassword");
        Assertions.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message is expected to be displayed!");
    }

    @Test
    public void invalidLoginTest_MustFail() {
        LoginPage loginPage = new LoginPage(webDriver);
        loginPage.login("wrongName", "wrongPassword");
        Assertions.assertFalse(loginPage.isErrorMessageDisplayed(), "Error message is expected to be displayed!");
    }
}
