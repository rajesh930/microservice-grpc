package example.co.ontic.ms.server;

import co.ontic.ms.core.UserContextHandler;
import example.co.ontic.ms.client.ContextPropagationTestService;
import example.co.ontic.ms.client.ExampleUserContextHandler;
import example.co.ontic.ms.client.TestMicroService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author rajesh
 * @since 17/01/25 22:10
 */
@SpringBootApplication
public class MSTestServer {
    public static void main(String[] args) {
        SpringApplication.run(MSTestServer.class, args);
    }

    @Bean
    public UserContextHandler userContextHandler() {
        return new ExampleUserContextHandler();
    }

    @Bean
    public TestMicroService testMicroService() {
        return new TestMicroServiceImpl();
    }

    @Bean
    public ContextPropagationTestService contextPropagationTestService() {
        return new ContextPropagationTestServiceImpl();
    }
    //as many microservice can be registered as above
}