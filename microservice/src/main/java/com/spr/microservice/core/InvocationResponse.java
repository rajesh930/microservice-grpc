package com.spr.microservice.core;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 5:08 PM
 */
public class InvocationResponse {
    private final Object response;
    private final Byte channel;

    public InvocationResponse(Object response) {
        this(response, null);
    }

    public InvocationResponse(Object response, Byte channel) {
        this.response = response;
        this.channel = channel;
    }

    public Object getResponse() {
        return response;
    }

    public Byte getChannel() {
        return channel;
    }
}
