package co.ontic.ms.core.handlers;

import co.ontic.ms.client.ApplicationServices;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.*;
import co.ontic.ms.core.observers.RequestToStreamObserver;
import co.ontic.ms.core.observers.StreamToRequestObserver;
import co.ontic.ms.core.observers.StreamToResponseObserver.NoopStreamObserver;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author rajesh
 * @since 14/01/25 20:36
 */
public class ClientStreamingHandler implements ServerCallHandler<Request, Response> {
    private static final Logger logger = LoggerFactory.getLogger(ClientStreamingHandler.class);

    private final MethodInfo methodInfo;
    private final Object microService;
    private final ServerCallHandler<Request, Response> delegate;

    public ClientStreamingHandler(MethodInfo methodInfo, Object microService) {
        this.methodInfo = methodInfo;
        this.microService = microService;
        this.delegate = createHandler();
    }

    @Override
    public ServerCall.Listener<Request> startCall(ServerCall<Request, Response> call, Metadata headers) {
        return delegate.startCall(call, headers);
    }

    public static TriObserver<Object, Object, Object> doAsyncCall(
            ManagedChannel channel, MethodDescriptor<Request, Response> methodDescriptor, CallOptions callOptions) {
        StreamObserver<Request> streamObserver = ClientCalls.asyncClientStreamingCall(channel.newCall(methodDescriptor, callOptions),
                new NoopStreamObserver<>());
        return new RequestToStreamObserver(streamObserver);
    }

    private ServerCallHandler<Request, Response> createHandler() {
        return ServerCalls.asyncClientStreamingCall(
                responseObserver -> {
                    Supplier<StreamToRequestObserver> supplier = () -> {
                        try {
                            //noinspection unchecked
                            Observer<Object> observer = (Observer<Object>) methodInfo.method().invoke(microService);
                            return new StreamToRequestObserver(observer);
                        } catch (Throwable t) {
                            logger.error("Error calling {}", methodInfo.method(), t);
                            responseObserver.onError(t);
                            return null;
                        }
                    };
                    if (ApplicationServices.getUserContextHandler() != null) {
                        byte[] userContext = UserContextHandler.userContext.get();
                        return ApplicationServices.getUserContextHandler().executeInContext(userContext, supplier);
                    } else {
                        return supplier.get();
                    }
                });
    }
}
