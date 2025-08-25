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
        logger.info("Создание экземпляра WebDriver (Chrome)");
        webDriver = DriverFactory.getDriver(DriverTypes.Chrome);
        logger.info("WebDriver успешно инициализирован");
    }
}