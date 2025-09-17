package ui.Drivers;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static Properties gridProperties;

    static {
        loadGridProperties();
    }

    private DriverFactory() {
    }

    private static void loadGridProperties() {
        gridProperties = new Properties();
        try (InputStream input = DriverFactory.class.getClassLoader().getResourceAsStream("grid.properties")) {
            if (input != null) {
                gridProperties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Could not load grid.properties: " + e.getMessage());
        }
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
        boolean gridEnabled = Boolean.parseBoolean(gridProperties.getProperty("selenium.grid.enabled", "false"));

        if (gridEnabled) {
            return createRemoteDriver(driverType);
        } else {
            return createLocalDriver(driverType);
        }
    }

    private static WebDriver createRemoteDriver(DriverTypes driverType) {
        String hubUrl = gridProperties.getProperty("selenium.grid.hub.url", "http://selenium-hub:4444");
        boolean localFallback = Boolean.parseBoolean(gridProperties.getProperty("selenium.grid.local.fallback", "true"));

        try {
            WebDriver driver;
            switch (driverType) {
                case Firefox:
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--headless");
                    driver = new RemoteWebDriver(new URL(hubUrl), firefoxOptions);
                    break;
                case Edge:
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--headless");
                    driver = new RemoteWebDriver(new URL(hubUrl), edgeOptions);
                    break;
                case Chrome:
                default:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--headless");
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--disable-gpu");
                    chromeOptions.addArguments("--disable-web-security");
                    chromeOptions.addArguments("--disable-features=VizDisplayCompositor");
                    driver = new RemoteWebDriver(new URL(hubUrl), chromeOptions);
            }

            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;

        } catch (MalformedURLException e) {
            System.err.println("Invalid Grid Hub URL: " + hubUrl);
            if (localFallback) {
                System.out.println("Falling back to local driver...");
                return createLocalDriver(driverType);
            }
            throw new RuntimeException("Failed to create remote driver and local fallback is disabled", e);
        } catch (Exception e) {
            System.err.println("Failed to connect to Selenium Grid: " + e.getMessage());
            if (localFallback) {
                System.out.println("Falling back to local driver...");
                return createLocalDriver(driverType);
            }
            throw new RuntimeException("Failed to create remote driver and local fallback is disabled", e);
        }
    }

    private static WebDriver createLocalDriver(DriverTypes driverType) {
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

                options.addArguments("--headless");
                options.addArguments("--no-sandbox");
                options.addArguments("--disable-dev-shm-usage");
                options.addArguments("--disable-gpu");
                options.addArguments("--remote-debugging-port=0");
                options.addArguments("--disable-web-security");
                options.addArguments("--disable-features=VizDisplayCompositor");

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