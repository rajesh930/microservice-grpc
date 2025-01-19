package co.ontic.ms.client;

import co.ontic.ms.core.MethodDescriptorProvider;
import co.ontic.ms.core.UserContextHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationContext;

/**
 * @author rajesh
 * @since 16/01/25 12:24
 */
public class ApplicationServices {
    private static ChannelFactory defaultChannelFactory;
    private static MethodDescriptorProvider methodDescriptorProvider;
    private static ObjectMapper objectMapper;
    private static ApplicationContext applicationContext;
    private static UserContextHandler userContextHandler;

    static void setDefaultChannelFactory(ChannelFactory defaultChannelFactory) {
        ApplicationServices.defaultChannelFactory = defaultChannelFactory;
    }

    public static ChannelFactory getDefaultChannelFactory() {
        return defaultChannelFactory;
    }

    static void setMethodDescriptorProvider(MethodDescriptorProvider methodDescriptorProvider) {
        ApplicationServices.methodDescriptorProvider = methodDescriptorProvider;
    }

    public static MethodDescriptorProvider getMethodDescriptorProvider() {
        return methodDescriptorProvider;
    }

    static void setObjectMapper(ObjectMapper objectMapper) {
        ApplicationServices.objectMapper = objectMapper;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    static void setApplicationContext(ApplicationContext applicationContext) {
        ApplicationServices.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static UserContextHandler getUserContextHandler() {
        return userContextHandler;
    }

    static void setUserContextHandler(UserContextHandler userContextHandler) {
        ApplicationServices.userContextHandler = userContextHandler;
    }
}
