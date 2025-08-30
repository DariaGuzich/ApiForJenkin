package ui.Drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.time.Duration;
import java.util.UUID;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver getDriver(DriverTypes driverType) {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            driver = createDriver(driverType);
            driverThreadLocal.set(driver);
        }
        return driver;
    }

    private static WebDriver createDriver(DriverTypes driverType) {
        WebDriver driver;
        
        switch (driverType) {
            case Firefox:
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            case Edge:
                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
                break;
            case Chrome:
            default:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();

                // Основные аргументы для Docker/headless режима
                options.addArguments("--headless");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--remote-debugging-port=0");
                options.addArguments("--disable-web-security");
                options.addArguments("--disable-features=VizDisplayCompositor");

                // Уникальная директория пользователя для каждого экземпляра
                options.addArguments("--user-data-dir=/tmp/chrome-user-data-" + UUID.randomUUID().toString());

                driver = new ChromeDriver(options);
        }
        
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error while closing WebDriver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    public static boolean hasActiveDriver() {
        return driverThreadLocal.get() != null;
    }
}