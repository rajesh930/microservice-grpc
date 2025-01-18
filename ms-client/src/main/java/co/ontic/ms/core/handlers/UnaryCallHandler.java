package co.ontic.ms.core.handlers;

import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Request;
import co.ontic.ms.core.Response;
import co.ontic.ms.core.observers.StreamToResponseObserver.NoopStreamObserver;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unary calls where client sends a request and receives a response
 * e.g. Object call(Object.. args)
 * or no response as tall
 * e.g. void call(Object.. args)
 *
 * @author rajesh
 * @since 12/01/25 17:35
 */
public class UnaryCallHandler implements ServerCallHandler<Request, Response> {
    private static final Logger logger = LoggerFactory.getLogger(UnaryCallHandler.class);
    private final MethodInfo methodInfo;
    private final Object microService;
    private final ServerCallHandler<Request, Response> delegate;

    public UnaryCallHandler(MethodInfo methodInfo, Object microService) {
        this.methodInfo = methodInfo;
        this.microService = microService;
        this.delegate = createHandler();
    }

    @Override
    public ServerCall.Listener<Request> startCall(ServerCall<Request, Response> call, Metadata headers) {
        return delegate.startCall(call, headers);
    }

    public static void doAsyncCall(ManagedChannel channel, MethodDescriptor<Request, Response> methodDescriptor, Object[] args) {
        ClientCalls.asyncUnaryCall(channel.newCall(methodDescriptor, CallOptions.DEFAULT), new Request(args), new NoopStreamObserver<>());
    }

    public static Object doBlockingCall(ManagedChannel channel, MethodDescriptor<Request, Response> methodDescriptor, Object[] args) {
        Response response = ClientCalls.blockingUnaryCall(channel, methodDescriptor, CallOptions.DEFAULT, new Request(args));
        return response.payload();
    }

    private ServerCallHandler<Request, Response> createHandler() {
        return ServerCalls.asyncUnaryCall(
                (request, responseObserver) -> {
                    try {
                        Object response = methodInfo.method().invoke(microService, (Object[]) request.payload());
                        if (!methodInfo.isVoidReturn() && !methodInfo.async()) {
                            responseObserver.onNext(new Response(response));
                        } else {
                            responseObserver.onNext(new Response(null));
                        }
                        responseObserver.onCompleted();
                    } catch (Throwable t) {
                        logger.error("Error calling {}", methodInfo.method(), t);
                        responseObserver.onError(t);
                    }
                });
    }
}
