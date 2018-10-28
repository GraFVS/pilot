package exception;

/**
 * Error for test exceptions
 *
 * @author sbt-sidochenko-vv
 */
public class AutotestError extends AssertionError {

    public AutotestError(String message) {
        super(message);
    }

    public AutotestError(String message, Throwable cause) {
        super(message, cause);
    }

    public AutotestError(Throwable cause) {
        super(cause);
    }

}
