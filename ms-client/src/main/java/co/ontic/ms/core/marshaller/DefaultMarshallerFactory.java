package co.ontic.ms.core.marshaller;

import co.ontic.ms.client.MicroServiceException;
import co.ontic.ms.core.MarshallerFactory;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Request;
import co.ontic.ms.core.Response;
import io.grpc.MethodDescriptor.Marshaller;
import io.grpc.MethodDescriptor.MethodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author rajesh
 * @since 13/01/25 14:27
 */
public class DefaultMarshallerFactory implements MarshallerFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultMarshallerFactory.class);

    @Override
    public Marshaller<Request> getRequestMarshaller(MethodInfo methodInfo) {
        return new LoggingMarshaller<>(createRequestMarshaller(methodInfo));
    }

    @Override
    public Marshaller<Response> getResponseMarshaller(MethodInfo method) {
        return new LoggingMarshaller<>(createResponseMarshaller(method));
    }

    private Marshaller<Request> createRequestMarshaller(MethodInfo methodInfo) {
        if (methodInfo.methodType() == MethodType.UNARY) {
            return new MethodRequestMarshaller(methodInfo);
        } else if (methodInfo.methodType() == MethodType.SERVER_STREAMING) {
            return new MethodRequestMarshaller(methodInfo);
        } else if (methodInfo.methodType() == MethodType.CLIENT_STREAMING) {
            return new ObserverRequestMarshaller(methodInfo);
        } else if (methodInfo.methodType() == MethodType.BIDI_STREAMING) {
            return new ObserverRequestMarshaller(methodInfo);
        }
        throw new MicroServiceException("Can not handle the method [ " + methodInfo + " ]");
    }

    private Marshaller<Response> createResponseMarshaller(MethodInfo methodInfo) {
        if (methodInfo.methodType() == MethodType.UNARY) {
            return new MethodResponseMarshaller(methodInfo);
        } else if (methodInfo.methodType() == MethodType.CLIENT_STREAMING) {
            return new NoOpResponseMarshaller();
        } else if (methodInfo.methodType() == MethodType.SERVER_STREAMING) {
            return new ObserverResponseMarshaller(methodInfo);
        } else if (methodInfo.methodType() == MethodType.BIDI_STREAMING) {
            return new ObserverResponseMarshaller(methodInfo);
        }
        throw new MicroServiceException("Can not handle the method [ " + methodInfo + " ]");
    }

    private static class NoOpResponseMarshaller implements Marshaller<Response> {

        @Override
        public InputStream stream(Response value) {
            return new ByteArrayInputStream(new byte[0]);
        }

        @Override
        public Response parse(InputStream stream) {
            return new Response(null);
        }
    }

    private record LoggingMarshaller<T>(Marshaller<T> delegate) implements Marshaller<T> {

        @Override
            public InputStream stream(T value) {
                try {
                    return delegate.stream(value);
                } catch (Throwable t) {
                    logger.error("Error marshalling payload", t);
                    throw t;
                }
            }

            @Override
            public T parse(InputStream stream) {
                try {
                    return delegate.parse(stream);
                } catch (Throwable t) {
                    logger.error("Error unmarshalling payload", t);
                    throw t;
                }
            }
        }
}
