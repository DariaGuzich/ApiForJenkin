package ui.Tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import ui.Drivers.DriverFactory;
import ui.Drivers.DriverTypes;
import ui.Utils.TestResultExtension;

@ExtendWith(TestResultExtension.class)
public class BaseTests {
    protected WebDriver webDriver;

    @BeforeEach
    public void initialize() {
        webDriver = DriverFactory.getDriver(DriverTypes.Chrome);
    }
}
