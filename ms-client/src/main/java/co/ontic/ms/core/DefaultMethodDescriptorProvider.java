package co.ontic.ms.core;

import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import io.grpc.MethodDescriptor;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rajesh
 * @since 12/01/25 17:43
 */
public class DefaultMethodDescriptorProvider implements MethodDescriptorProvider {
    /**
     * Not using service name in key as method is always unique across service
     */
    private final ConcurrentHashMap<Method, MethodDescriptor<Request, Response>> methodDescriptors = new ConcurrentHashMap<>();

    @Override
    public MethodDescriptor<Request, Response> getMethodDescriptor(String serviceName, MethodInfo methodInfo) {
        return methodDescriptors.computeIfAbsent(methodInfo.method(), method -> createMethodDescriptor(serviceName, methodInfo));
    }

    private MethodDescriptor<Request, Response> createMethodDescriptor(String serviceName, MethodInfo methodInfo) {
        MethodDescriptor.Builder<Request, Response> methodDescBuilder = MethodDescriptor.newBuilder();
        methodDescBuilder.setFullMethodName(serviceName + "/" + methodInfo.serviceMethodName());
        methodDescBuilder.setType(methodInfo.methodType());
        methodDescBuilder.setRequestMarshaller(methodInfo.marshallerFactory().getRequestMarshaller(methodInfo));
        methodDescBuilder.setResponseMarshaller(methodInfo.marshallerFactory().getResponseMarshaller(methodInfo));
        return methodDescBuilder.build();
    }
}
