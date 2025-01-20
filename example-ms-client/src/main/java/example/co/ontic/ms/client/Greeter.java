package example.co.ontic.ms.client;

import co.ontic.ms.annotations.MicroService;
import co.ontic.ms.annotations.MicroServiceMethod;
import example.co.ontic.ms.client.proto.HelloWorldProto.HelloReply;
import example.co.ontic.ms.client.proto.HelloWorldProto.HelloRequest;

/**
 * @author rajesh
 * @since 20/01/25 13:08
 */
@MicroService("helloworld.Greeter")
public interface Greeter {
    @MicroServiceMethod(name = "SayHello")
    HelloReply sayHello(HelloRequest request);
}
