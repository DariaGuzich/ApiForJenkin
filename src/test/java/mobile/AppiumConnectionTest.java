package mobile;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppiumConnectionTest {

    @Test
    public void testAppiumServerConnection() throws IOException {
        System.out.println("Тестируем подключение к Appium серверу...");

        String appiumHost = System.getProperty("appium.host", "localhost");
        String appiumPort = System.getProperty("appium.port", "4723");
        String appiumUrl = "http://" + appiumHost + ":" + appiumPort + "/status";

        System.out.println("Подключаемся к: " + appiumUrl);

        URL url = new URL(appiumUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        System.out.println("Ответ от Appium: " + responseCode);

        assertEquals(200, responseCode, "Appium сервер должен отвечать HTTP 200");
        System.out.println("✅ Подключение к Appium серверу успешно!");
    }
}