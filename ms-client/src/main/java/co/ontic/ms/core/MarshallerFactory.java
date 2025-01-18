package co.ontic.ms.core;

import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import io.grpc.MethodDescriptor.Marshaller;

/**
 * @author rajesh
 * @since 12/01/25 20:53
 */
public interface MarshallerFactory {
    /**
     * Create request marshaller for given method
     * GRPC would work as long as marshaller on both client and server side accepts same protocol
     * Marshaller should be able to support external client or server following standard grpc/protobuf specification
     *
     * @param method method for which marshaller required
     * @return the request marshaller
     */
    Marshaller<Request> getRequestMarshaller(MethodInfo method);

    /**
     * Create response marshaller for given method
     * GRPC would work as long as marshaller on both client and server side accepts same protocol
     * Marshaller should be able to support external client or server following standard grpc/protobuf specification
     *
     * @param method method for which marshaller required
     * @return the response marshaller
     */
    Marshaller<Response> getResponseMarshaller(MethodInfo method);
}
