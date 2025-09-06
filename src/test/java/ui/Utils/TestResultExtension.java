package ui.Utils;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import ui.Drivers.DriverFactory;
import ui.Drivers.DriverTypes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TestResultExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if (DriverFactory.hasActiveDriver()) {
            WebDriver driver = DriverFactory.getDriver(DriverTypes.Chrome);
            takeScreenshot(driver, context.getDisplayName());
        }
        DriverFactory.quitDriver();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        DriverFactory.quitDriver();
    }

    private void takeScreenshot(WebDriver driver, String testName) {
        if (driver instanceof TakesScreenshot) {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String fileName = testName.replaceAll("[^a-zA-Z0-9]", "_") + "_" + timestamp + ".png";
            Path destination = Path.of("screenshots", fileName);

            try {
                Files.createDirectories(destination.getParent());
                Files.copy(screenshot.toPath(), destination);
                System.out.println("📸 Screenshot saved to: " + destination.toAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}