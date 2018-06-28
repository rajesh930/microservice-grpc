package com.spr.microservice.client;

import com.spr.microservice.core.*;
import com.spr.microservice.core.MicroServiceInfo.MethodInfo;
import com.spr.microservice.server.BidiCallHandler;
import com.spr.microservice.server.ClientStreamingHandler;
import com.spr.microservice.server.ServerStreamingHandler;
import com.spr.microservice.server.UnaryCallHandler;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 6:01 PM
 */
public class MicroServiceClient<T> implements InvocationHandler {
    private final Class<T> service;
    private T proxy;
    private MicroServiceInfo microServiceInfo;
    private LazySpringService<ChannelFactory> channelFactory = new LazySpringService<>(ChannelFactory.class);
    private LazySpringService<MethodDescriptorProvider> methodDescriptorProvider = new LazySpringService<>(MethodDescriptorProvider.class);
    private ConcurrentHashMap<Method, MethodDescriptor<InvocationRequest, InvocationResponse>> methodDescriptors = new ConcurrentHashMap<>();

    public MicroServiceClient(Class<T> service) {
        this.service = service;
    }

    public T get() {
        if (proxy != null) {
            return proxy;
        }
        synchronized (this) {
            if (proxy != null) {
                return proxy;
            }
            this.microServiceInfo = MicroServiceAnnotationReader.readMicroServiceAnnotations(service);
            //noinspection unchecked
            this.proxy = (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, this);
        }
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodInfo methodInfo = microServiceInfo.getMethodInfoByMethod(method);
        ManagedChannel channel = channelFactory.get().getOrCreateChannel(microServiceInfo.getServiceName());
        MethodDescriptor<InvocationRequest, InvocationResponse> methodDescriptor = methodDescriptors.computeIfAbsent(
                method, key -> methodDescriptorProvider.get().createMethodDescriptor(microServiceInfo.getServiceName(), methodInfo));

        if (methodInfo.isAsync()) {
            if (methodInfo.isHasStreamingRequest() && methodInfo.isHasStreamingResponse()) {
                return BidiCallHandler.doAsyncCall(channel, methodDescriptor, args);
            } else if (!methodInfo.isHasStreamingRequest() && methodInfo.isHasStreamingResponse()) {
                return ServerStreamingHandler.doAsyncCall(channel, methodDescriptor, args);
            } else if (methodInfo.isHasStreamingRequest() && !methodInfo.isHasStreamingResponse()) {
                return ClientStreamingHandler.doAsyncCall(channel, methodDescriptor);
            } else {
                return UnaryCallHandler.doAsyncCall(channel, methodDescriptor, args);
            }
        } else {
            return UnaryCallHandler.doBlockingCall(channel, methodDescriptor, args);
        }
    }
}
