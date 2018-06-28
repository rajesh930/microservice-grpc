package com.spr.microservice.server;

import com.spr.microservice.core.*;
import com.spr.microservice.core.MicroServiceInfo.MethodInfo;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;

/**
 * User: rajesh
 * Date: 28/06/18
 * Time: 4:54 PM
 */
public class ServerStreamingHandler implements ServerCallHandler<InvocationRequest, InvocationResponse> {
    private final MethodInfo methodInfo;
    private final Object microService;
    private final ServerCallHandler<InvocationRequest, InvocationResponse> delegate;

    public ServerStreamingHandler(MethodInfo methodInfo, Object microService) {
        this.methodInfo = methodInfo;
        this.microService = microService;
        this.delegate = createHandler();
    }

    @Override
    public ServerCall.Listener<InvocationRequest> startCall(ServerCall<InvocationRequest, InvocationResponse> call, Metadata headers) {
        return delegate.startCall(call, headers);
    }

    public static Object doAsyncCall(ManagedChannel channel, MethodDescriptor<InvocationRequest, InvocationResponse> methodDescriptor, Object[] args) {
        //noinspection unchecked
        Observer<Object> observer = (Observer<Object>) args[0];
        args[0] = null;
        ClientCalls.asyncServerStreamingCall(channel.newCall(methodDescriptor, CallOptions.DEFAULT),
                new InvocationRequest(args),
                new ObserverToStreamObserverResponse(observer));
        return null;
    }

    private ServerCallHandler<InvocationRequest, InvocationResponse> createHandler() {
        return ServerCalls.asyncServerStreamingCall(
                new ServerCalls.ServerStreamingMethod<InvocationRequest, InvocationResponse>() {
                    @Override
                    public void invoke(InvocationRequest request, StreamObserver<InvocationResponse> responseObserver) {
                        try {
                            Object[] args = (Object[]) request.getRequest();
                            args[0] = new StreamObserverToObserverResponse(responseObserver);
                            methodInfo.getMethod().invoke(microService, args);
                        } catch (Throwable t) {
                            responseObserver.onError(t);
                        }
                    }
                });
    }
}
