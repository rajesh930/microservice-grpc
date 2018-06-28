package com.spr.microservice.core;

import com.spr.microservice.core.MicroServiceInfo.MethodInfo;
import io.grpc.MethodDescriptor;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 7:43 PM
 */
public class DefaultMethodDescriptorProvider implements MethodDescriptorProvider {

    @Override
    public MethodDescriptor<InvocationRequest, InvocationResponse> createMethodDescriptor(String serviceName, MethodInfo methodInfo) {
        MethodDescriptor.Builder<InvocationRequest, InvocationResponse> methodDescBuilder = MethodDescriptor.newBuilder();
        methodDescBuilder.setFullMethodName(serviceName + "/" + methodInfo.getServiceMethodName());
        if (methodInfo.isHasStreamingRequest() && !methodInfo.isHasStreamingResponse()) {
            methodDescBuilder.setType(MethodDescriptor.MethodType.CLIENT_STREAMING);
        } else if (!methodInfo.isHasStreamingRequest() && methodInfo.isHasStreamingResponse()) {
            methodDescBuilder.setType(MethodDescriptor.MethodType.SERVER_STREAMING);
        } else if (methodInfo.isHasStreamingRequest() && methodInfo.isHasStreamingResponse()) {
            methodDescBuilder.setType(MethodDescriptor.MethodType.BIDI_STREAMING);
        } else {
            methodDescBuilder.setType(MethodDescriptor.MethodType.UNARY);
        }
        methodDescBuilder.setRequestMarshaller(createRequestMarshaller(serviceName, methodInfo));
        methodDescBuilder.setResponseMarshaller(createResponseMarshaller(serviceName, methodInfo));
        return methodDescBuilder.build();
    }

    @SuppressWarnings("UnusedParameters")
    protected MethodDescriptor.Marshaller<InvocationRequest> createRequestMarshaller(String serviceName, MethodInfo methodInfo) {
        return XStreamMarshaller.REQUEST_MARSHALLER;
    }

    @SuppressWarnings("UnusedParameters")
    protected MethodDescriptor.Marshaller<InvocationResponse> createResponseMarshaller(String serviceName, MethodInfo methodInfo) {
        return XStreamMarshaller.RESPONSE_MARSHALLER;
    }
}
