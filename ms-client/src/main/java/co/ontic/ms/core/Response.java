package co.ontic.ms.core;

/**
 * Wrapper object of response message
 *
 * @author rajesh
 * @since 14/01/25 19:32
 */
public record Response(int channel, Object payload) {
    public Response(Object payload) {
        this(0, payload);
    }
}
