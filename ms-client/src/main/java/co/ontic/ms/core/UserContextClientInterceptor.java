package co.ontic.ms.core;

import io.grpc.*;

import static co.ontic.ms.core.UserContextHandler.userContextHeader;

/**
 * @author rajesh
 * @since 19/01/25 12:47
 */
public class UserContextClientInterceptor implements ClientInterceptor {

    private final UserContextHandler handler;

    public UserContextClientInterceptor(UserContextHandler handler) {
        this.handler = handler;
    }

    @Override
    public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {

        ClientCall<ReqT, RespT> clientCall = next.newCall(method, callOptions);

        return new ForwardingClientCall.SimpleForwardingClientCall<>(clientCall) {
            @Override
            public void start(Listener<RespT> responseListener, Metadata headers) {
                headers.put(userContextHeader, handler.userContext()); // Add the metadata
                super.start(responseListener, headers);
            }
        };
    }
}
