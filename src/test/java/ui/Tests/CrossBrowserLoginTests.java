package ui.Tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.Drivers.DriverFactory;
import ui.Drivers.DriverTypes;
import ui.PageObjects.LoginPage;
import ui.PageObjects.MainPage;
import ui.Utils.TestResultExtension;

@ExtendWith(TestResultExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class CrossBrowserLoginTests {

    protected static final Logger logger = LoggerFactory.getLogger(CrossBrowserLoginTests.class);

    @Test
    public void validLoginTest_Chrome() {
        runValidLoginTest(DriverTypes.Chrome);
    }

    @Test
    public void validLoginTest_Firefox() {
        runValidLoginTest(DriverTypes.Firefox);
    }

    @Test
    public void validLoginTest_Edge() {
        runValidLoginTest(DriverTypes.Edge);
    }

    @Test
    public void invalidLoginTest_Chrome() {
        runInvalidLoginTest(DriverTypes.Chrome);
    }

    @Test
    public void invalidLoginTest_Firefox() {
        runInvalidLoginTest(DriverTypes.Firefox);
    }

    @Test
    public void invalidLoginTest_Edge() {
        runInvalidLoginTest(DriverTypes.Edge);
    }

    private void runValidLoginTest(DriverTypes browserType) {
        logger.info("Запуск теста valid login в браузере: {}", browserType);

        WebDriver webDriver = DriverFactory.getDriver(browserType);

        LoginPage loginPage = new LoginPage(webDriver);
        logger.info("Выполнение входа с учётными данными: Admin / admin123 в браузере {}", browserType);
        loginPage.login("Admin", "admin123");

        MainPage mainPage = new MainPage(webDriver);
        Assertions.assertTrue(mainPage.isDropDownProfileDisplayed(),
                String.format("Main page wasn't properly opened in %s!", browserType));

        logger.info("Тест успешно пройден в браузере: {}", browserType);
    }

    private void runInvalidLoginTest(DriverTypes browserType) {
        logger.info("Запуск теста invalid login в браузере: {}", browserType);

        WebDriver webDriver = DriverFactory.getDriver(browserType);

        LoginPage loginPage = new LoginPage(webDriver);
        logger.info("Выполнение входа с неверными учётными данными в браузере {}", browserType);
        loginPage.login("wrongName", "wrongPassword");

        Assertions.assertTrue(loginPage.isErrorMessageDisplayed(),
                String.format("Error message is expected to be displayed in %s!", browserType));

        logger.info("Тест успешно пройден в браузере: {}", browserType);
    }
}