package co.ontic.ms.client;

import co.ontic.ms.core.DefaultMethodDescriptorProvider;
import co.ontic.ms.core.MethodDescriptorProvider;
import co.ontic.ms.core.UserContextHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * @author rajesh
 * @since 09/01/25 14:30
 */
@Configuration
@EnableConfigurationProperties(MicroServiceClientProps.class)
public class MicroServiceClientAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public ServiceRegistry microServiceRegistry(MicroServiceClientProps microServiceClientProps) throws IOException {
        Map<String, ServiceEndpoint> serviceRegistry = microServiceClientProps.getServiceRegistry();
        if (serviceRegistry != null) {
            for (Map.Entry<String, ServiceEndpoint> entry : serviceRegistry.entrySet()) {
                try (InputStream serviceConfig = MicroServiceClientAutoConfig.class.getResourceAsStream(entry.getKey() + "_serviceConfig.json")) {
                    if (serviceConfig != null) {
                        //noinspection unchecked
                        entry.getValue().setServiceConfig(jacksonObjectMapper().readValue(serviceConfig, Map.class));
                    }
                }
            }
        }
        return new ApplicationPropsBasedServiceRegistry(serviceRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public ChannelFactory defaultChannelFactory(ServiceRegistry serviceRegistry) {
        return new DefaultChannelFactory(serviceRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public MethodDescriptorProvider defaultMethodDescriptorProvider() {
        return new DefaultMethodDescriptorProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper jacksonObjectMapper() {
        return new ObjectMapper();
    }

    @EventListener
    public void setupClient(ContextRefreshedEvent event) {
        ApplicationServices.setDefaultChannelFactory(event.getApplicationContext().getBean(
                "defaultChannelFactory", ChannelFactory.class));
        ApplicationServices.setMethodDescriptorProvider(event.getApplicationContext().getBean(
                "defaultMethodDescriptorProvider", MethodDescriptorProvider.class));
        ApplicationServices.setObjectMapper(event.getApplicationContext().getBean(
                "jacksonObjectMapper", ObjectMapper.class));
        ApplicationServices.setApplicationContext(event.getApplicationContext());

        Map<String, UserContextHandler> beansOfType = event.getApplicationContext().getBeansOfType(UserContextHandler.class);
        beansOfType.values().stream().findFirst().ifPresent(ApplicationServices::setUserContextHandler);
    }
}
