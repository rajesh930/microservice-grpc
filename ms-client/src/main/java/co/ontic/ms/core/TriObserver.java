package co.ontic.ms.core;

/**
 * @author rajesh
 * @since 10/01/25 20:35
 */
public interface TriObserver<T, U, V> extends BiObserver<T, U> {
    /**
     * Receives a value from the target on third channel
     *
     * @param data value received
     */
    void update3(V data);
}