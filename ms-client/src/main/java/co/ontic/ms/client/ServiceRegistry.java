package co.ontic.ms.client;

/**
 * @author rajesh
 * @since 10/01/25 18:18
 */
public interface ServiceRegistry {
    ServiceEndpoint getServiceEndpoint(String serviceName);
}
