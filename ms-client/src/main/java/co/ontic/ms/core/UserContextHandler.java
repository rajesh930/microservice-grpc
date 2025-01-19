package co.ontic.ms.core;

import io.grpc.Context;
import io.grpc.Metadata;

import java.util.function.Supplier;

/**
 * If any user context needs to passed to server, a bean of this should be registered in spring
 *
 * @author rajesh
 * @since 19/01/25 12:50
 */
public interface UserContextHandler {
    Metadata.Key<byte[]> userContextHeader = Metadata.Key.of("user-context-bin", Metadata.BINARY_BYTE_MARSHALLER);
    Context.Key<byte[]> userContext = Context.key("user-context");

    /**
     * User context in bytes array, by keeping in bytes array provider can the minimize the header size, for example if user context is number
     */
    byte[] userContext();

    /**
     * Run callable in given context, implementer is supposed to start internal thread local user context with given value and stop after
     * callable execution and return the result from callable
     */
    <T> T executeInContext(byte[] userContext, Supplier<T> callable);
}
