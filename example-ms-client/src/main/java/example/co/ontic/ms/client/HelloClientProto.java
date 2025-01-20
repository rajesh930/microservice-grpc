package example.co.ontic.ms.client;

import co.ontic.ms.client.MicroServiceClient;
import example.co.ontic.ms.client.proto.HelloWorldProto.HelloReply;
import example.co.ontic.ms.client.proto.HelloWorldProto.HelloRequest;
import io.grpc.StatusRuntimeException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author rajesh
 * @since 20/01/25 13:06
 */
@SpringBootApplication
public class HelloClientProto implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HelloClientProto.class, args);
    }

    @Override
    public void run(String... args) {
        MicroServiceClient<Greeter> greeter = new MicroServiceClient<>(Greeter.class);
        String name = "fdjhDDJHSDHSD";
        System.out.println("Will try to greet " + name);
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = greeter.get().sayHello(request);
        } catch (StatusRuntimeException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
            return;
        }
        System.out.println("Greeting: " + response.getMessage());
    }
}
