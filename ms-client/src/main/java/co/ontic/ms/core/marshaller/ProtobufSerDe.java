package co.ontic.ms.core.marshaller;

import co.ontic.ms.client.MicroServiceException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;

import java.lang.reflect.Method;

/**
 * @author rajesh
 * @since 15/01/25 19:25
 */
public class ProtobufSerDe implements SerDe {
    private final Class<? extends Message> protoMessageType;
    private final Parser<?> parser;

    public ProtobufSerDe(Class<? extends Message> protoMessageType) {
        try {
            this.protoMessageType = protoMessageType;
            Method parserMethod = protoMessageType.getMethod("parser");
            this.parser = (Parser<?>) parserMethod.invoke(null);
        } catch (Exception e) {
            throw new MicroServiceException("Failed to get parser for [ " + protoMessageType + " ]", e);
        }
    }

    @Override
    public byte[] serialize(Object obj) {
        return ((Message) obj).toByteArray();
    }

    @Override
    public Object deSerialize(byte[] bytes) {
        try {
            return parser.parseFrom(bytes);
        } catch (Exception e) {
            throw new MicroServiceException("Failed to parse data of [ " + protoMessageType + " ]", e);
        }
    }
}
