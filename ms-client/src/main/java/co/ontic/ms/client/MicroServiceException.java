package co.ontic.ms.client;

/**
 * @author rajesh
 * @since 10/01/25 19:11
 */
public class MicroServiceException extends RuntimeException {

    public MicroServiceException(String message) {
        super(message);
    }

    public MicroServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
