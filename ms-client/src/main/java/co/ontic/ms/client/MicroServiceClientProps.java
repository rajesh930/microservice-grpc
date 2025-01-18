package co.ontic.ms.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * @author rajesh
 * @since 09/01/25 14:40
 */
@ConfigurationProperties(prefix = "ontic.ms.client")
public class MicroServiceClientProps {
    @NestedConfigurationProperty
    private Map<String, ServiceEndpoint> serviceRegistry;

    public Map<String, ServiceEndpoint> getServiceRegistry() {
        return serviceRegistry;
    }

    public void setServiceRegistry(Map<String, ServiceEndpoint> serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
