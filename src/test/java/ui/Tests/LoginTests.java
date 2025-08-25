package ui.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.PageObjects.LoginPage;
import ui.PageObjects.MainPage;

public class LoginTests extends BaseTests {

    private static final Logger logger = LoggerFactory.getLogger(LoginTests.class);

    @Test
    public void validLoginTest() {
        LoginPage loginPage = new LoginPage(webDriver);
        logger.info("Выполнение входа с учётными данными: Admin / admin123");
        loginPage.login("Admin", "admin123");
        MainPage mainPage = new MainPage(webDriver);
        Assertions.assertTrue(mainPage.isDropDownProfileDisplayed(), "Main page wasn't properly opened!");
    }

    @Test
    public void invalidLoginTest() {
        LoginPage loginPage = new LoginPage(webDriver);
        logger.info("Выполнение входа с учётными данными: wrongName / wrongPassword");
        loginPage.login("wrongName", "wrongPassword");
        Assertions.assertTrue(loginPage.isErrorMessageDisplayed(), "Error message is expected to be displayed!");
    }

    @Test
    public void invalidLoginTest_MustFail() {
        LoginPage loginPage = new LoginPage(webDriver);
        logger.info("Выполнение входа с учётными данными: wrongName / wrongPassword");
        loginPage.login("wrongName", "wrongPassword");
        Assertions.assertFalse(loginPage.isErrorMessageDisplayed(), "Error message is expected to be displayed!");
    }
}
