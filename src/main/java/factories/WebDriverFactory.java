package factories;

import com.codeborne.selenide.Configuration;
import exception.AutotestError;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverFactory {
    private static final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

    public static WebDriver createDriver() {
        //Selenide config   TODO: вынести в проперти
        Configuration.timeout = 4 * 1000L;
        Configuration.collectionsTimeout = 10 * 1000L;
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
        Configuration.fastSetValue = false;
        Configuration.reportsFolder = "target/selenide";

        RemoteDriver driver = RemoteDriver.getRemoteDriver(System.getProperty("sirius.selenium.driver", "chrome"));
        for (int i = 1; i <= 3; i++) {
            LOG.info("Attempt #" + i + " to start web driver");
            try {
                return driver.init();
            } catch (UnreachableBrowserException e) {
                LOG.error("Failed to create web driver. Attempt number {}", i, e);
            }
        }
        throw new AutotestError("Failed to create driver.");
    }

    private enum RemoteDriver {
        CHROME("chrome"),
        PHANTOM_JS("phantom-js");
        final String name;

        RemoteDriver(String browserName) {
            this.name = browserName;
        }

        private static RemoteDriver getRemoteDriver(String browserName) {
            for (RemoteDriver remoteDriver : RemoteDriver.values()) {
                if (remoteDriver.name.equalsIgnoreCase(browserName)) {
                    return remoteDriver;
                }
            }
            return CHROME;
        }


        public WebDriver init() {
            switch (this) {
                case CHROME:
                    System.setProperty("webdriver.chrome.driver", "src/test/resources/driver/chromedriver.exe");        //TODO Убрать это в проперти
                    LOG.debug("Init chrome driver");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--ignore-certificate-errors",
                            "--disable-web-security");
                    if (Boolean.valueOf(System.getProperty("sirius.selenium.headless", "false"))) {
                        options.addArguments(
                                "--headless",
                                "--disable-gpu",
                                "--window-size=1920,1080",
                                "--disable-dev-shm-usage",
                                "--no-sandbox");

                    } else {
                        options.addArguments("start-maximized");
                    }
                    options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                    options.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);
                    options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                    ChromeDriverService service = new ChromeDriverService.Builder()
                            .usingAnyFreePort()
                            .build();
                    return new ChromeDriver(service, options);
                default:
                    throw new AutotestError("Это исключение никогда не бросается :-)");
            }
        }

    }
}