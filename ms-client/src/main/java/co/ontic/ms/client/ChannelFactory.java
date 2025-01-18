package co.ontic.ms.client;

import io.grpc.ManagedChannel;

/**
 * @author rajesh
 * @since 09/01/25 14:23
 */
public interface ChannelFactory {
    ManagedChannel getOrCreateChannel(String microServiceName);
}
