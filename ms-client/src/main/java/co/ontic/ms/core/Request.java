package co.ontic.ms.core;

/**
 * Wrapper object of request message
 *
 * @author rajesh
 * @since 14/01/25 19:32
 */
public record Request(int channel, Object payload) {
    public Request(Object payload) {
        this(0, payload);
    }
}
