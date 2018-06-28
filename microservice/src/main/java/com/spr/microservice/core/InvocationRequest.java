package com.spr.microservice.core;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 5:00 PM
 */
public class InvocationRequest {
    private final Object request;
    private final Byte channel;

    public InvocationRequest(Object request) {
        this(request, null);
    }

    public InvocationRequest(Object request, Byte channel) {
        this.request = request;
        this.channel = channel;
    }

    public Object getRequest() {
        return request;
    }

    public Byte getChannel() {
        return channel;
    }
}
