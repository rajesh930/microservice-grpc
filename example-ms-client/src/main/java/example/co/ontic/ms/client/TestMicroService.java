package example.co.ontic.ms.client;


import co.ontic.ms.annotations.MicroService;
import co.ontic.ms.annotations.MicroServiceMethod;
import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.TriObserver;

/**
 * @author rajesh
 * @since 17/01/25 22:10
 */
@MicroService("TestMicroService")
public interface TestMicroService {

    @MicroServiceMethod
    String hello(String name);

    @MicroServiceMethod(async = true)
    void helloVoid(String name);

    @MicroServiceMethod(async = true)
    Observer<String> bidirectionalStreamToServer(BiObserver<String, Long> serverResponse);

    @MicroServiceMethod(async = true)
    void serverStream(BiObserver<String, Long> serverResponse);

    @MicroServiceMethod(async = true)
    void serverStreamWithArgs(BiObserver<String, Long> serverResponse, int times);

    @MicroServiceMethod(async = true)
    TriObserver<String, Long, CustomObject> clientStream();
}
