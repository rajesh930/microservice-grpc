package com.spr.microservice.server;

import com.spr.microservice.core.InvocationRequest;
import com.spr.microservice.core.InvocationResponse;
import com.spr.microservice.core.MicroServiceInfo.MethodInfo;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;

/**
 * User: rajesh
 * Date: 28/06/18
 * Time: 11:56 AM
 */
public class UnaryCallHandler implements ServerCallHandler<InvocationRequest, InvocationResponse> {
    private final MethodInfo methodInfo;
    private final Object microService;
    private final ServerCallHandler<InvocationRequest, InvocationResponse> delegate;

    public UnaryCallHandler(MethodInfo methodInfo, Object microService) {
        this.methodInfo = methodInfo;
        this.microService = microService;
        this.delegate = createHandler();
    }

    @Override
    public ServerCall.Listener<InvocationRequest> startCall(ServerCall<InvocationRequest, InvocationResponse> call, Metadata headers) {
        return delegate.startCall(call, headers);
    }

    public static Object doAsyncCall(ManagedChannel channel, MethodDescriptor<InvocationRequest, InvocationResponse> methodDescriptor, Object[] args) {
        channel.newCall(methodDescriptor, CallOptions.DEFAULT);
        ClientCalls.asyncUnaryCall(channel.newCall(methodDescriptor, CallOptions.DEFAULT), new InvocationRequest(args), null);
        return null;
    }

    public static Object doBlockingCall(ManagedChannel channel, MethodDescriptor<InvocationRequest, InvocationResponse> methodDescriptor, Object[] args) {
        InvocationResponse invocationResponse = ClientCalls.blockingUnaryCall(channel, methodDescriptor, CallOptions.DEFAULT, new InvocationRequest(args));
        return invocationResponse.getResponse();
    }

    private ServerCallHandler<InvocationRequest, InvocationResponse> createHandler() {
        return ServerCalls.asyncUnaryCall(
                new ServerCalls.UnaryMethod<InvocationRequest, InvocationResponse>() {
                    @Override
                    public void invoke(InvocationRequest request, StreamObserver<InvocationResponse> responseObserver) {
                        try {
                            Object response = methodInfo.getMethod().invoke(microService, (Object[]) request.getRequest());
                            if (!methodInfo.isVoidReturn() && !methodInfo.isAsync()) {
                                responseObserver.onNext(new InvocationResponse(response));
                                responseObserver.onCompleted();
                            } else if (methodInfo.isVoidReturn() && !methodInfo.isAsync()) {
                                responseObserver.onNext(new InvocationResponse(null));
                                responseObserver.onCompleted();
                            }
                        } catch (Throwable t) {
                            responseObserver.onError(t);
                        }
                    }
                });
    }
}
