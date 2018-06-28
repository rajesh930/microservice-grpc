package com.spr.microservice.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 8:29 PM
 */
public class DefaultChannelFactory implements ChannelFactory {
    private ConcurrentHashMap<String, ManagedChannel> channels = new ConcurrentHashMap<>();

    @Override
    public ManagedChannel getOrCreateChannel(String microServiceName) {
        return channels.computeIfAbsent(microServiceName, microServiceName1 ->
                ManagedChannelBuilder.forAddress(getHost(microServiceName1), getPort(microServiceName1)).usePlaintext().build());
    }

    protected String getHost(String microServiceName) {
        return System.getenv(microServiceName + ".host");
    }

    protected int getPort(String microServiceName) {
        return Integer.parseInt(System.getenv(microServiceName + ".port"));
    }
}
