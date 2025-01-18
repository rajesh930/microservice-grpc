package example.co.ontic.ms.server;

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
    public TestMicroService testMicroService() {
        return new TestMicroServiceImpl();
    }
    //as many microservice can be registered as above
}