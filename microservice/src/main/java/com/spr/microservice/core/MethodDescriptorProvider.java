package com.spr.microservice.core;

import io.grpc.MethodDescriptor;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 7:43 PM
 */
public interface MethodDescriptorProvider {
    MethodDescriptor<InvocationRequest, InvocationResponse> createMethodDescriptor(String serviceName, MicroServiceInfo.MethodInfo methodInfo);
}
