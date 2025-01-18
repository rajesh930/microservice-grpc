package example.co.ontic.ms.client;

import co.ontic.ms.client.MicroServiceClient;
import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.TriObserver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author rajesh
 * @since 17/01/25 22:59
 */
@SuppressWarnings("CallToPrintStackTrace")
@SpringBootApplication
public class MSTestClient implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MSTestClient.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        MicroServiceClient<TestMicroService> testMicroServiceClient = new MicroServiceClient<>(TestMicroService.class);
        String response = testMicroServiceClient.get().hello("rajesh");
        System.out.println(response);
        Thread.sleep(5000);
        for (int i = 0; i < 50; i++) {
            testMicroServiceClient.get().helloVoid("asdfff " + i);
        }
        Thread.sleep(5000);
        Observer<String> toServer = testMicroServiceClient.get().bidirectionalStreamToServer(new BiObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("bidi string response " + data);
            }

            @Override
            public void update2(Long data) {
                System.out.println("bidi Long response " + data);
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void finish() {
                System.out.println("Finish called!!");
            }
        });
        for (int i = 0; i < 100; i++) {
            toServer.update("rajesh " + i);
        }
        toServer.finish();

        Thread.sleep(5000);
        testMicroServiceClient.get().serverStream(new BiObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("String response (Server Stream)" + data);
            }

            @Override
            public void update2(Long data) {
                System.out.println("Long response (Server Stream)" + data);
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void finish() {
                System.out.println("Finish called (Server Stream) !!");
            }
        });
        Thread.sleep(5000);

        testMicroServiceClient.get().serverStreamWithArgs(new BiObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("String response (Server Stream args)" + data);
            }

            @Override
            public void update2(Long data) {
                System.out.println("Long response (Server Stream args)" + data);
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void finish() {
                System.out.println("Finish called (Server Stream) !!");
            }
        }, 50);
        Thread.sleep(5000);

        TriObserver<String, Long, CustomObject> toServerTri = testMicroServiceClient.get().clientStream();
        for (int i = 0; i < 100; i++) {
            toServerTri.update("rajesh " + i);
            toServerTri.update2((long) i);
            toServerTri.update3(new CustomObject("rajesh " + i, "" + i));
        }
        toServerTri.finish();
        Thread.sleep(1000);
    }
}
