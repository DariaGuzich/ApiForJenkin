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
    private static WebDriver webDriver;

    private DriverFactory() {
    }

    public static WebDriver getDriver(DriverTypes driver) {
        if (webDriver == null) {
            switch (driver) {
                case DriverTypes.Firefox:
                    WebDriverManager.firefoxdriver().setup();
                    webDriver = new FirefoxDriver();
                    break;
                case DriverTypes.Edge:
                    WebDriverManager.edgedriver().setup();
                    webDriver = new EdgeDriver();
                    break;
                case DriverTypes.Chrome:
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

                    // Дополнительные стабилизирующие аргументы
                    options.addArguments("--disable-extensions");
                    options.addArguments("--disable-plugins");
                    options.addArguments("--disable-images");
                    options.addArguments("--disable-javascript");
                    options.addArguments("--disable-background-timer-throttling");
                    options.addArguments("--disable-backgrounding-occluded-windows");
                    options.addArguments("--disable-renderer-backgrounding");

                    webDriver = new ChromeDriver(options);
            }
            webDriver.manage().window().maximize();
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }

        return webDriver;
    }

    public static void quitDriver() {
        if (webDriver != null) {
            webDriver.quit();
            webDriver = null;
        }
    }
}