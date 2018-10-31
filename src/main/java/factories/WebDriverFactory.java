package factories;

import com.codeborne.selenide.Configuration;
import enviroment.Stand;
import exception.AutotestError;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverFactory {
    private static final Logger LOG = LoggerFactory.getLogger(WebDriverFactory.class);

    public static WebDriver createDriver() {
        Configuration.timeout = Long.parseLong(Stand.getCurrentStand().getConfig().getString("selenide.timeout.sec")) * 1000L;
        Configuration.collectionsTimeout = Long.parseLong(Stand.getCurrentStand().getConfig().getString("selenide.collections.timeout.sec")) * 1000L;
        Configuration.screenshots = false;
        Configuration.savePageSource = false;
        Configuration.fastSetValue = false;
        Configuration.reportsFolder = "target/selenide";

        //RemoteDriver driver = RemoteDriver.getRemoteDriver(System.getProperty("sirius.selenium.driver", "chrome"));
        RemoteDriver driver = RemoteDriver.getRemoteDriver(Stand.getCurrentStand().getConfig().getString("selenium.driver"));
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
        FIREFOX("gecko");
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
                    System.setProperty("webdriver.chrome.driver",
                            Stand.getCurrentStand().getConfig().getString("webdriver.chrome.driver"));
                    LOG.debug("Init chrome driver");
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--ignore-certificate-errors",
                            "--disable-web-security");
                    if (Boolean.valueOf(Stand.getCurrentStand().getConfig().getString("selenium.headless"))) {
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
                case FIREFOX:
                    System.setProperty("webdriver.gecko.driver",
                            Stand.getCurrentStand().getConfig().getString("webdriver.gecko.driver"));
                    LOG.debug("Init gecko driver");
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--ignore-certificate-errors",
                            "--disable-web-security");
                    if (Boolean.valueOf(Stand.getCurrentStand().getConfig().getString("selenium.headless"))) {
                        firefoxOptions.addArguments(
                                "--headless",
                                "--disable-gpu",
                                "--window-size=1920,1080",
                                "--disable-dev-shm-usage",
                                "--no-sandbox");

                    }

                    firefoxOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                    firefoxOptions.setCapability(CapabilityType.HAS_NATIVE_EVENTS, false);
                    firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                    FirefoxDriver firefoxDriver = new FirefoxDriver(firefoxOptions);
                    firefoxDriver.manage().window().maximize();
                    return firefoxDriver;

                default:
                    throw new AutotestError("Это исключение никогда не бросается :-)");
            }
        }

    }
}