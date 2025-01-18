package co.ontic.ms.client;

import co.ontic.ms.core.MethodDescriptorProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;

/**
 * @author rajesh
 * @since 16/01/25 12:24
 */
public class ApplicationServices {
    static ChannelFactory defaultChannelFactory;
    static MethodDescriptorProvider methodDescriptorProvider;
    static ObjectMapper objectMapper;
    static ApplicationContext applicationContext;

    public static ChannelFactory getDefaultChannelFactory() {
        return defaultChannelFactory;
    }

    public static MethodDescriptorProvider getMethodDescriptorProvider() {
        return methodDescriptorProvider;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
