package mobile;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AndroidAppTest {

    private AndroidDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        UiAutomator2Options options = new UiAutomator2Options();
        options.setPlatformName("Android");

        // Используем переменную окружения для устройства или эмулятора
        String deviceName = System.getProperty("device.name", "android-emulator");
        options.setDeviceName(deviceName);

        // Для реального устройства - получаем UDID из adb devices
        String udid = System.getProperty("device.udid", ""); // Реальный UDID устройства
        if (!udid.isEmpty()) {
            options.setCapability("appium:udid", udid);
        }

        options.setPlatformVersion("13");
        options.setAutomationName("UiAutomator2");

        // Открываем домашний экран и ищем доступные приложения
        options.setAppPackage("com.google.android.apps.nexuslauncher");
        options.setAppActivity("com.google.android.apps.nexuslauncher.NexusLauncherActivity");

        // Дополнительные настройки
        options.setCapability("appium:newCommandTimeout", 300);
        options.setCapability("appium:noReset", true);

        // Настройки для работы с удаленным устройством
        options.setCapability("appium:connectHardwareKeyboard", true);
        options.setCapability("appium:systemPort", 8200);

        String appiumHost = System.getProperty("appium.host", "localhost");
        String appiumPort = System.getProperty("appium.port", "4723");
        driver = new AndroidDriver(new URL("http://" + appiumHost + ":" + appiumPort), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    @Test
    public void testHomeScreenAndOpenApp() throws InterruptedException {
        System.out.println("Начинаем тест домашнего экрана Android...");

        // Проверяем, что домашний экран загрузился
        WebElement homeScreen = driver.findElement(By.xpath("//*[@content-desc='Home']"));
        assertTrue(homeScreen.isDisplayed(), "Домашний экран должен быть виден");
        System.out.println("Домашний экран успешно загружен!");

        // Проверим наличие иконки Phone
        WebElement phoneApp = driver.findElement(By.xpath("//*[@text='Phone']"));
        assertTrue(phoneApp.isDisplayed(), "Приложение Phone должно быть видно");
        System.out.println("Приложение Phone найдено!");

        // Кликаем на Phone
        phoneApp.click();
        Thread.sleep(3000);
        System.out.println("Приложение Phone успешно открылось!");

        // Возвращаемся на домашний экран
        driver.pressKey(new KeyEvent(AndroidKey.HOME));
        Thread.sleep(1000);
        System.out.println("Вернулись на домашний экран");
    }


    @AfterEach
    public void tearDown() throws InterruptedException {
        if (driver != null) {
            // Принудительно возвращаемся на домашний экран
            driver.pressKey(new KeyEvent(AndroidKey.HOME));
            Thread.sleep(500);

            // Закрываем все фоновые приложения
            driver.pressKey(new KeyEvent(AndroidKey.APP_SWITCH));
            Thread.sleep(1000);
            driver.pressKey(new KeyEvent(AndroidKey.HOME));

            driver.quit();
        }
    }
}