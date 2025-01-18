package co.ontic.ms.core.handlers;

import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.Request;
import co.ontic.ms.core.Response;
import co.ontic.ms.core.TriObserver;
import co.ontic.ms.core.observers.RequestToStreamObserver;
import co.ontic.ms.core.observers.ResponseToStreamObserver;
import co.ontic.ms.core.observers.StreamToRequestObserver;
import co.ontic.ms.core.observers.StreamToResponseObserver;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rajesh
 * @since 10/01/25 20:04
 */
public class BidiCallHandler implements ServerCallHandler<Request, Response> {
    private static final Logger logger = LoggerFactory.getLogger(BidiCallHandler.class);

    private final MethodInfo methodInfo;
    private final Object microService;
    private final ServerCallHandler<Request, Response> delegate;

    public BidiCallHandler(MethodInfo methodInfo, Object microService) {
        this.methodInfo = methodInfo;
        this.microService = microService;
        this.delegate = createHandler();
    }

    @Override
    public ServerCall.Listener<Request> startCall(ServerCall<Request, Response> call, Metadata headers) {
        return delegate.startCall(call, headers);
    }

    public static TriObserver<Object, Object, Object> doAsyncCall(
            ManagedChannel channel, MethodDescriptor<Request, Response> methodDescriptor, Object[] args) {
        //noinspection unchecked
        Observer<Object> observer = (Observer<Object>) args[0];
        StreamObserver<Request> streamObserver = ClientCalls.asyncBidiStreamingCall(channel.newCall(methodDescriptor, CallOptions.DEFAULT),
                new StreamToResponseObserver(observer));
        return new RequestToStreamObserver(streamObserver);

    }

    private ServerCallHandler<Request, Response> createHandler() {
        return ServerCalls.asyncBidiStreamingCall(
                responseObserver -> {
                    try {
                        //noinspection unchecked
                        Observer<Object> observer = (Observer<Object>) methodInfo.method().invoke(microService,
                                new ResponseToStreamObserver(responseObserver));
                        return new StreamToRequestObserver(observer);
                    } catch (Throwable t) {
                        logger.error("Error calling {}", methodInfo.method(), t);
                        responseObserver.onError(t);
                        return null;
                    }
                });
    }
}
