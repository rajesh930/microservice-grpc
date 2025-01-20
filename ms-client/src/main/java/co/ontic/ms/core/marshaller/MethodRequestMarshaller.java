package co.ontic.ms.core.marshaller;

import co.ontic.ms.client.MicroServiceException;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.Request;
import com.google.protobuf.Message;
import io.grpc.MethodDescriptor.Marshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Marshaller which marshall method arguments
 *
 * @author rajesh
 * @since 15/01/25 18:12
 */
public class MethodRequestMarshaller implements Marshaller<Request> {
    private final SerDe[] serDes;
    private final boolean grpcCompliant;

    public MethodRequestMarshaller(MethodInfo methodInfo) {
        Type[] args = methodInfo.method().getGenericParameterTypes();
        this.serDes = new SerDe[args.length];
        for (int i = 0; i < args.length; i++) {
            serDes[i] = createSerDeForType(args[i]);
        }
        grpcCompliant = serDes.length == 0
                || serDes.length == 1
                || (serDes.length == 2 && serDes[0] == null);
    }

    private SerDe createSerDeForType(Type type) {
        if (Observer.class.isAssignableFrom(type instanceof Class<?> ?
                (Class<?>) type :
                (Class<?>) ((ParameterizedType) type).getRawType())) {
            return null;
        } else if (type instanceof Class<?> && Message.class.isAssignableFrom((Class<?>) type)) {
            //noinspection unchecked
            return new ProtobufSerDe((Class<? extends Message>) type);
        } else {
            return new JsonSerDe(type);
        }
    }

    @Override
    public InputStream stream(Request request) {
        Object[] args = (Object[]) request.payload();
        if (args == null || args.length == 0) {
            return new ByteArrayInputStream(new byte[0]);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
        for (int i = 0; i < args.length; i++) {
            if (serDes[i] == null) {
                continue;
            }
            byte[] bytes = serDes[i].serialize(args[i]);
            if (grpcCompliant) {
                bos.writeBytes(bytes);
            } else {
                bos.write((byte) (bytes.length >>> 24));
                bos.write((byte) (bytes.length >>> 16));
                bos.write((byte) (bytes.length >>> 8));
                bos.write((byte) (bytes.length));
                bos.writeBytes(bytes);
            }
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }

    @Override
    public Request parse(InputStream in) {
        try {
            Object[] args = new Object[serDes.length];
            for (int i = 0; i < serDes.length; i++) {
                if (serDes[i] == null) {
                    args[i] = null;
                    continue;
                }
                if (grpcCompliant) {
                    args[i] = serDes[i].deSerialize(in.readAllBytes());
                } else {
                    int size = ((in.read() << 24) + (in.read() << 16) + (in.read() << 8) + (in.read()));
                    args[i] = serDes[i].deSerialize(in.readNBytes(size));
                }
            }
            return new Request(args);
        } catch (IOException e) {
            throw new MicroServiceException("Error reading data from stream", e);
        }
    }
}
