package co.ontic.ms.core.handlers;

import co.ontic.ms.client.ApplicationServices;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.Request;
import co.ontic.ms.core.Response;
import co.ontic.ms.core.UserContextHandler;
import co.ontic.ms.core.observers.ResponseToStreamObserver;
import co.ontic.ms.core.observers.StreamToResponseObserver;
import io.grpc.*;
import io.grpc.stub.ClientCalls;
import io.grpc.stub.ServerCalls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author rajesh
 * @since 12/01/25 17:34
 */
public class ServerStreamingHandler implements ServerCallHandler<Request, Response> {
    private static final Logger logger = LoggerFactory.getLogger(ServerStreamingHandler.class);

    private final MethodInfo methodInfo;
    private final Object microService;
    private final ServerCallHandler<Request, Response> delegate;

    public ServerStreamingHandler(MethodInfo methodInfo, Object microService) {
        this.methodInfo = methodInfo;
        this.microService = microService;
        this.delegate = createHandler();
    }

    @Override
    public ServerCall.Listener<Request> startCall(ServerCall<Request, Response> call, Metadata headers) {
        return delegate.startCall(call, headers);
    }

    public static void doAsyncCall(ManagedChannel channel, MethodDescriptor<Request, Response> methodDescriptor, Object[] args) {
        //noinspection unchecked
        Observer<Object> observer = (Observer<Object>) args[0];
        args[0] = null;
        ClientCalls.asyncServerStreamingCall(channel.newCall(methodDescriptor, CallOptions.DEFAULT),
                new Request(args),
                new StreamToResponseObserver(observer));
    }

    private ServerCallHandler<Request, Response> createHandler() {
        return ServerCalls.asyncServerStreamingCall(
                (request, responseObserver) -> {
                    Supplier<Void> supplier = () -> {
                        try {
                            Object[] args = (Object[]) request.payload();
                            args[0] = new ResponseToStreamObserver(responseObserver);
                            methodInfo.method().invoke(microService, args);
                        } catch (Throwable t) {
                            logger.error("Error calling {}", methodInfo.method(), t);
                            responseObserver.onError(t);
                        }
                        return null;
                    };
                    if (ApplicationServices.getUserContextHandler() != null) {
                        byte[] userContext = UserContextHandler.userContext.get();
                        ApplicationServices.getUserContextHandler().executeInContext(userContext, supplier);
                    } else {
                        supplier.get();
                    }
                });
    }
}
