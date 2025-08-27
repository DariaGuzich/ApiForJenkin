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
    private static final Object lock = new Object(); // Добавляем объект для синхронизации

    private DriverFactory() {
    }

    public static WebDriver getDriver(DriverTypes driver) {
        // Двойная проверка блокировки (Double-Checked Locking)
        if (webDriver == null) {
            synchronized (lock) {
                if (webDriver == null) { // Повторная проверка внутри блокировки
                    switch (driver) {
                        case Firefox:
                            WebDriverManager.firefoxdriver().setup();
                            webDriver = new FirefoxDriver();
                            break;
                        case Edge:
                            WebDriverManager.edgedriver().setup();
                            webDriver = new EdgeDriver();
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

                            webDriver = new ChromeDriver(options);
                    }
                    webDriver.manage().window().maximize();
                    webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                }
            }
        }

        return webDriver;
    }

    public static void quitDriver() {
        synchronized (lock) { // Синхронизируем закрытие драйвера
            if (webDriver != null) {
                try {
                    webDriver.quit();
                } catch (Exception e) {
                    System.err.println("Error while closing WebDriver: " + e.getMessage());
                } finally {
                    webDriver = null; // Обязательно обнуляем ссылку даже при ошибке
                }
            }
        }
    }
}