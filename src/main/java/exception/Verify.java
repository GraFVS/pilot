package exception;

import enviroment.Init;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.fail;

public class Verify {

    private static final Logger LOG = LoggerFactory.getLogger(Verify.class);

    public static void verifyTrue(String message, boolean flag) {
        addNonCriticalError(message, !flag);
    }

    public static void verifyFalse(String message, boolean flag) {
        addNonCriticalError(message, flag);
    }

    private static void addNonCriticalError(String message, boolean flag) {
        try {
            if (flag) fail(message);
        } catch (AssertionError e) {
            attachTrow(e);
            LOG.error(message);
            if (Init.driverStarted()) {
                attachScreenshot();
            }
            fire(e);
        }
    }

    private static void fire(Throwable throvv) {
        Allure.getLifecycle().updateStep((StepResult e) -> {
            Allure.getLifecycle().startStep(randomUUID().toString(), new StepResult()
                    .withName(throvv.getMessage())
                    .withStatus(Status.FAILED));
            Allure.getLifecycle().stopStep();
            e.setStatus(Status.BROKEN);
        });
        Allure.getLifecycle().stopStep();
    }

    @Attachment
            (value = "Stacktrace", fileExtension = ".txt")
    private static byte[] attachTrow(Error error) {
        return Arrays.toString(error.getStackTrace()).getBytes();
    }

    @Attachment
            (value = "Screenshot", fileExtension = ".jpg")
    private static byte[] attachScreenshot() {
        return ((TakesScreenshot) Init.getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
