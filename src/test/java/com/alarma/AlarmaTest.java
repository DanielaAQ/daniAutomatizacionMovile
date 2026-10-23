package com.alarma;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlarmaTest {

    static final String APPIUM_SERVER = "http://127.0.0.1:4723";
    static final String DEVICE_NAME = "emulator-5554";
    static final String APP_PACKAGE = "com.google.android.deskclock";
    static final String APP_ACTIVITY = "com.android.deskclock.DeskClock";

    static final String ID_TAB_ALARMAS = APP_PACKAGE + ":id/tab_menu_alarm";
    static final String ID_BOTON_AGREGAR = APP_PACKAGE + ":id/fab";
    static final String ID_HORA_ALARMA = APP_PACKAGE + ":id/digital_clock";

    AndroidDriver driver;

    @BeforeEach
    void setUp() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(DEVICE_NAME)
                .setAppPackage(APP_PACKAGE)
                .setAppActivity(APP_ACTIVITY)
                .setNoReset(true);

        driver = new AndroidDriver(URI.create(APPIUM_SERVER).toURL(), options);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void crearAlarma() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Paso 1: ir a la pestaña Alarmas (por id, independiente del idioma)
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id(ID_TAB_ALARMAS))).click();

        // Paso 2: contar las alarmas existentes
        int alarmasAntes = driver.findElements(AppiumBy.id(ID_HORA_ALARMA)).size();

        // Paso 3: tocar el botón "+" para agregar una alarma
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.id(ID_BOTON_AGREGAR))).click();

        // Paso 4: confirmar la hora con "Aceptar" / "OK"
        wait.until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator(
                "new UiSelector().textMatches(\"(?i)^(ok|aceptar)$\")"))).click();

        // Paso 5: verificar que se creó una alarma nueva
        wait.until(d -> d.findElements(AppiumBy.id(ID_HORA_ALARMA)).size() > alarmasAntes);
        int alarmasDespues = driver.findElements(AppiumBy.id(ID_HORA_ALARMA)).size();

        File captura = driver.getScreenshotAs(OutputType.FILE);
        Files.copy(captura.toPath(), Path.of("evidencia_alarma_creada.png"), StandardCopyOption.REPLACE_EXISTING);

        assertEquals(alarmasAntes + 1, alarmasDespues);
    }
}
