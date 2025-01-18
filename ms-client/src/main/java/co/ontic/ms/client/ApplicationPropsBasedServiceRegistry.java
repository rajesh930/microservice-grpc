package co.ontic.ms.client;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rajesh
 * @since 10/01/25 18:32
 */
public class ApplicationPropsBasedServiceRegistry implements ServiceRegistry {

    private final Map<String, ServiceEndpoint> serviceRegistry;

    public ApplicationPropsBasedServiceRegistry(Map<String, ServiceEndpoint> serviceRegistry) {
        if (serviceRegistry == null) {
            serviceRegistry = new HashMap<>();
        }
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public ServiceEndpoint getServiceEndpoint(String serviceName) {
        return serviceRegistry.get(serviceName);
    }
}
