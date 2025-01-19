package co.ontic.ms.client;

import co.ontic.ms.core.UserContextClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static java.lang.Boolean.TRUE;

/**
 * @author rajesh
 * @since 10/01/25 18:05
 */
public class DefaultChannelFactory implements ChannelFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultChannelFactory.class);

    private final ServiceRegistry serviceRegistry;
    private final ConcurrentHashMap<String, ManagedChannel> channels = new ConcurrentHashMap<>();

    public DefaultChannelFactory(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public ManagedChannel getOrCreateChannel(String microServiceName) {
        ServiceEndpoint serviceEndpoint = serviceRegistry.getServiceEndpoint(microServiceName);
        if (serviceEndpoint == null) {
            throw new MicroServiceException("Endpoint not found for service [ " + microServiceName + " ]");
        }
        String serverAddress = serviceEndpoint.getAddress();
        return channels.computeIfAbsent(microServiceName + "_" + serverAddress, s -> buildChannel(serviceEndpoint));
    }

    private ManagedChannel buildChannel(ServiceEndpoint serviceEndpoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Creating channel for endpoint: {}", serviceEndpoint);
        }
        ManagedChannelBuilder<?> managedChannelBuilder = ManagedChannelBuilder.forTarget(serviceEndpoint.getAddress());
        if (TRUE.equals(serviceEndpoint.getUseTLS())) {
            managedChannelBuilder.useTransportSecurity();
        } else {
            managedChannelBuilder.usePlaintext();
        }
        if (serviceEndpoint.getIdleTimeout() != null) {
            managedChannelBuilder.idleTimeout(serviceEndpoint.getIdleTimeout().toMinutes(), TimeUnit.MINUTES);
        }
        if (serviceEndpoint.getKeepAliveTime() != null) {
            managedChannelBuilder.keepAliveTime(serviceEndpoint.getKeepAliveTime().toMinutes(), TimeUnit.MINUTES);
        }
        if (serviceEndpoint.getKeepAliveTimeout() != null) {
            managedChannelBuilder.keepAliveTimeout(serviceEndpoint.getKeepAliveTimeout().toSeconds(), TimeUnit.SECONDS);
        }
        if (TRUE.equals(serviceEndpoint.getDisableRetry())) {
            managedChannelBuilder.disableRetry();
        }
        if (serviceEndpoint.getServiceConfig() != null) {
            managedChannelBuilder.defaultServiceConfig(serviceEndpoint.getServiceConfig());
        }
        if (ApplicationServices.getUserContextHandler() != null) {
            managedChannelBuilder.intercept(new UserContextClientInterceptor(ApplicationServices.getUserContextHandler()));
        }
        return managedChannelBuilder.build();
    }
}
