package co.ontic.ms.core;

/**
 * @author rajesh
 * @since 10/01/25 20:28
 */
public interface Observer<T> {
    /**
     * Receives a value from the target.
     *
     * @param data value received
     */
    void update(T data);

    /**
     * Receives a terminating error from the target.
     *
     * @param t error received
     */
    void error(Throwable t);

    /**
     * Receives a notification of success from the target.
     */
    void finish();
}
