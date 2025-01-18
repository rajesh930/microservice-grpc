package co.ontic.ms.core;

import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import io.grpc.MethodDescriptor;

/**
 * @author rajesh
 * @since 12/01/25 17:41
 */
public interface MethodDescriptorProvider {
    MethodDescriptor<Request, Response> getMethodDescriptor(String serviceName, MethodInfo methodInfo);
}
