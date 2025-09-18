package ui.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.Drivers.DriverFactory;
import ui.Drivers.DriverTypes;
import ui.Utils.TestResultExtension;

@ExtendWith(TestResultExtension.class)
public class BaseTests {
    protected WebDriver webDriver;
    protected static final Logger logger = LoggerFactory.getLogger(BaseTests.class);

    @BeforeEach
    public void initialize() {
        logger.info("Инициализация теста...");
        String browserType = System.getProperty("browser.type", "CHROME");
        logger.info("Создание экземпляра WebDriver ({})", browserType);
        webDriver = DriverFactory.getDriver();
        logger.info("WebDriver успешно инициализирован для браузера {}", browserType);
    }
}