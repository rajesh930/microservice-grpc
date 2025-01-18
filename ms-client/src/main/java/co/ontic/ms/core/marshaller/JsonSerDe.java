package co.ontic.ms.core.marshaller;

import co.ontic.ms.client.ApplicationServices;
import co.ontic.ms.client.MicroServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;

/**
 * @author rajesh
 * @since 15/01/25 19:25
 */
public class JsonSerDe implements SerDe {
    private final JavaType javaType;

    public JsonSerDe(Type type) {
        this.javaType = ApplicationServices.getObjectMapper().constructType(type);
    }

    @Override
    public byte[] serialize(Object obj) {
        try {
            return ApplicationServices.getObjectMapper().writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new MicroServiceException(MessageFormat.format("Error serializing [ {0} ]", obj), e);
        }
    }

    @Override
    public Object deSerialize(byte[] bytes) {
        try {
            return ApplicationServices.getObjectMapper().readValue(bytes, javaType);
        } catch (IOException e) {
            throw new MicroServiceException(MessageFormat.format("Error deSerializing [ {0} ]", javaType), e);
        }
    }
}
