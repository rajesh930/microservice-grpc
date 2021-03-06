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
 * Time: 12:07 PM
 */
public class ClientStreamingHandler implements ServerCallHandler<InvocationRequest, InvocationResponse> {

    private final MethodInfo methodInfo;
    private final Object microService;
    private final ServerCallHandler<InvocationRequest, InvocationResponse> delegate;

    public ClientStreamingHandler(MethodInfo methodInfo, Object microService) {
        this.methodInfo = methodInfo;
        this.microService = microService;
        this.delegate = createHandler();
    }

    @Override
    public ServerCall.Listener<InvocationRequest> startCall(ServerCall<InvocationRequest, InvocationResponse> call, Metadata headers) {
        return delegate.startCall(call, headers);
    }

    public static TriObserver<Object, Object, Object> doAsyncCall(
            ManagedChannel channel, MethodDescriptor<InvocationRequest, InvocationResponse> methodDescriptor) {
        StreamObserver<InvocationRequest> streamObserver = ClientCalls.asyncClientStreamingCall(channel.newCall(methodDescriptor, CallOptions.DEFAULT), null);
        return new StreamObserverToObserverRequest(streamObserver);
    }

    private ServerCallHandler<InvocationRequest, InvocationResponse> createHandler() {
        return ServerCalls.asyncClientStreamingCall(
                new ServerCalls.ClientStreamingMethod<InvocationRequest, InvocationResponse>() {
                    @Override
                    public StreamObserver<InvocationRequest> invoke(StreamObserver<InvocationResponse> responseObserver) {
                        try {
                            //noinspection unchecked
                            Observer<Object> observer = (Observer<Object>) methodInfo.getMethod().invoke(microService);
                            return new ObserverToStreamObserverRequest(observer);
                        } catch (Throwable t) {
                            responseObserver.onError(t);
                            return null;
                        }
                    }
                });
    }
}
