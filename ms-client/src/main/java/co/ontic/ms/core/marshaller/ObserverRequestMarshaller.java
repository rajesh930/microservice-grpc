package co.ontic.ms.core.marshaller;

import co.ontic.ms.client.MicroServiceException;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Request;
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
public class ObserverRequestMarshaller implements MethodDescriptor.Marshaller<Request> {
    private final SerDe[] serDes;
    private final boolean grpcCompliant;

    public ObserverRequestMarshaller(MethodInfo methodInfo) {
        ParameterizedType returnType = (ParameterizedType) methodInfo.method().getGenericReturnType();
        Type[] channelTypes = returnType.getActualTypeArguments();
        this.serDes = new SerDe[channelTypes.length];
        for (int i = 0; i < channelTypes.length; i++) {
            serDes[i] = createSerDeForType(channelTypes[i]);
        }
        grpcCompliant = serDes.length == 1;
    }

    private SerDe createSerDeForType(Type type) {
        if (type instanceof Class<?> && Message.class.isAssignableFrom((Class<?>) type)) {
            //noinspection unchecked
            return new ProtobufSerDe((Class<? extends Message>) type);
        } else {
            return new JsonSerDe(type);
        }
    }

    @Override
    public InputStream stream(Request request) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
        if (grpcCompliant) {
            byte[] bytes = serDes[0].serialize(request.payload());
            bos.writeBytes(bytes);
        } else {
            bos.write(request.channel());
            byte[] bytes = serDes[request.channel()].serialize(request.payload());
            bos.writeBytes(bytes);
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    public Request parse(InputStream in) {
        try {
            if (grpcCompliant) {
                return new Request(serDes[0].deSerialize(in.readAllBytes()));
            } else {
                int channel = in.read();
                return new Request(channel, serDes[channel].deSerialize(in.readAllBytes()));
            }
        } catch (IOException e) {
            throw new MicroServiceException("Error reading data from stream", e);
        }
    }
}
