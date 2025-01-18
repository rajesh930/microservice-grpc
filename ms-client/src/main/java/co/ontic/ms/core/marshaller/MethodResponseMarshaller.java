package co.ontic.ms.core.marshaller;

import co.ontic.ms.client.MicroServiceException;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Response;
import com.google.protobuf.Message;
import io.grpc.MethodDescriptor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * @author rajesh
 * @since 15/01/25 18:12
 */
public class MethodResponseMarshaller implements MethodDescriptor.Marshaller<Response> {
    private final SerDe serDe;
    private final MethodInfo methodInfo;

    public MethodResponseMarshaller(MethodInfo methodInfo) {
        this.methodInfo = methodInfo;
        if (methodInfo.isVoidReturn()) {
            serDe = null;
        } else {
            this.serDe = createSerDeForType(methodInfo.method().getGenericReturnType());
        }
    }

    private SerDe createSerDeForType(Type type) {
        if (type instanceof Class<?> && ((Class<?>) type).isAssignableFrom(Message.class)) {
            //noinspection unchecked
            return new ProtobufSerDe((Class<? extends Message>) type);
        } else {
            return new JsonSerDe(type);
        }
    }

    @Override
    public InputStream stream(Response response) {
        if (serDe == null) {
            return new ByteArrayInputStream(new byte[0]);
        } else {
            return new ByteArrayInputStream(serDe.serialize(response.payload()));
        }
    }

    @Override
    public Response parse(InputStream stream) {
        try {
            if (serDe == null) {
                return new Response(null);
            } else {
                return new Response(serDe.deSerialize(stream.readAllBytes()));
            }
        } catch (IOException e) {
            throw new MicroServiceException("Error reading data from stream for [ " + methodInfo + " ]", e);
        }
    }
}
