package co.ontic.ms.server;

import co.ontic.ms.core.UserContextHandler;
import io.grpc.*;

/**
 * @author rajesh
 * @since 19/01/25 14:32
 */
public class UserContextServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        byte[] userContext = headers.get(UserContextHandler.userContextHeader);
        if (userContext != null) {
            Context context = Context.current().withValue(UserContextHandler.userContext, userContext);
            return Contexts.interceptCall(context, call, headers, next);
        } else {
            return next.startCall(call, headers);
        }
    }
}
