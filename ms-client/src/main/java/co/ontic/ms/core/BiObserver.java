package co.ontic.ms.core;

/**
 * @author rajesh
 * @since 10/01/25 20:34
 */
public interface BiObserver<T, U> extends Observer<T> {
    /**
     * Receives a value from the target on second channel
     *
     * @param data value received
     */
    void update2(U data);
}