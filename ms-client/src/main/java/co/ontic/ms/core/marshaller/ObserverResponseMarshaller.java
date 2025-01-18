package co.ontic.ms.core.marshaller;

import co.ontic.ms.client.MicroServiceException;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Response;
import com.google.protobuf.Message;
import io.grpc.MethodDescriptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author rajesh
 * @since 15/01/25 18:12
 */
public class ObserverResponseMarshaller implements MethodDescriptor.Marshaller<Response> {
    private final SerDe[] serDes;
    private final boolean grpcCompliant;

    public ObserverResponseMarshaller(MethodInfo methodInfo) {
        ParameterizedType observerType = (ParameterizedType) methodInfo.method().getGenericParameterTypes()[0];
        Type[] channelTypes = observerType.getActualTypeArguments();
        this.serDes = new SerDe[channelTypes.length];
        for (int i = 0; i < channelTypes.length; i++) {
            serDes[i] = createSerDeForType(channelTypes[i]);
        }
        grpcCompliant = serDes.length == 1;
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        if (grpcCompliant) {
            byte[] bytes = serDes[0].serialize(response.payload());
            bos.writeBytes(bytes);
        } else {
            bos.write(response.channel());
            byte[] bytes = serDes[response.channel()].serialize(response.payload());
            bos.writeBytes(bytes);
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    public Response parse(InputStream in) {
        try {
            if (grpcCompliant) {
                return new Response(serDes[0].deSerialize(in.readAllBytes()));
            } else {
                int channel = in.read();
                return new Response(channel, serDes[channel].deSerialize(in.readAllBytes()));
            }
        } catch (IOException e) {
            throw new MicroServiceException("Error reading data from stream", e);
        }
    }
}
