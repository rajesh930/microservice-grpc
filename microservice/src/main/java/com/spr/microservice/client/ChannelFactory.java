package com.spr.microservice.client;

import io.grpc.ManagedChannel;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 7:18 PM
 */
public interface ChannelFactory {
    ManagedChannel getOrCreateChannel(String microServiceName);
}
