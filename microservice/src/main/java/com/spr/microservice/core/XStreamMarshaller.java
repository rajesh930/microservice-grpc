package com.spr.microservice.core;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.XomDriver;
import io.grpc.MethodDescriptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 4:39 PM
 */
public class XStreamMarshaller<T> implements MethodDescriptor.Marshaller<T> {
    private static final XStream xStream = new XStream(new XomDriver());

    static {
        xStream.ignoreUnknownElements();
    }

    public static final XStreamMarshaller<InvocationRequest> REQUEST_MARSHALLER = new XStreamMarshaller<>();
    public static final XStreamMarshaller<InvocationResponse> RESPONSE_MARSHALLER = new XStreamMarshaller<>();

    private XStreamMarshaller() {
    }

    @Override
    public InputStream stream(T t) {
        byte[] bytes = xStream.toXML(t).getBytes(Charset.forName("UTF-8"));
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public T parse(InputStream inputStream) {
        //noinspection unchecked
        return (T) xStream.fromXML(inputStream);
    }
}
