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
    public ServiceRegistry microServiceRegistry(MicroServiceClientProps microServiceClientProps) {
        return new ApplicationPropsBasedServiceRegistry(microServiceClientProps.getServiceRegistry());
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
