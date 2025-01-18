package example.co.ontic.ms.client;

import co.ontic.ms.client.MicroServiceClient;
import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.TriObserver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.atomic.AtomicInteger;

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
        TestMicroService testMicroService = testMicroServiceClient.get();
        testMicroService.resetCounter();

        String response = testMicroService.hello("rajesh");
        if (response == null) {
            throw new RuntimeException("un expected response");
        }
        System.out.println(response);
        for (int i = 0; i < 50; i++) {
            testMicroService.helloVoid("asdfff " + i);
        }
        Thread.sleep(5000);
        if (testMicroService.resetCounter() != 51) {
            throw new RuntimeException("Expected call not received to server");
        }
        AtomicInteger counter = new AtomicInteger(0);

        Observer<String> toServer = testMicroService.bidirectionalStreamToServer(new BiObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("bidi string response " + data);
                counter.incrementAndGet();
            }

            @Override
            public void update2(Long data) {
                System.out.println("bidi Long response " + data);
                counter.incrementAndGet();
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
                counter.incrementAndGet();
            }

            @Override
            public void finish() {
                System.out.println("Finish called!!");
                counter.incrementAndGet();
            }
        });
        for (int i = 0; i < 100; i++) {
            toServer.update("rajesh " + i);
        }
        toServer.finish();
        Thread.sleep(5000);
        if (testMicroService.resetCounter() != 101) {
            throw new RuntimeException("Expected call not received to server");
        }
        if (counter.getAndSet(0) != 201) {
            throw new RuntimeException("Expected response not received");
        }

        testMicroService.serverStream(new BiObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("String response (Server Stream)" + data);
                counter.incrementAndGet();
            }

            @Override
            public void update2(Long data) {
                System.out.println("Long response (Server Stream)" + data);
                counter.incrementAndGet();
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
                counter.incrementAndGet();
            }

            @Override
            public void finish() {
                System.out.println("Finish called (Server Stream) !!");
                counter.incrementAndGet();
            }
        });
        Thread.sleep(5000);
        if (testMicroService.resetCounter() != 1) {
            throw new RuntimeException("Expected call not received to server");
        }
        if (counter.getAndSet(0) != 201) {
            throw new RuntimeException("Expected response not received");
        }

        testMicroService.serverStreamWithArgs(new BiObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("String response (Server Stream args)" + data);
                counter.incrementAndGet();
            }

            @Override
            public void update2(Long data) {
                System.out.println("Long response (Server Stream args)" + data);
                counter.incrementAndGet();
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
                counter.incrementAndGet();
            }

            @Override
            public void finish() {
                System.out.println("Finish called (Server Stream) !!");
                counter.incrementAndGet();
            }
        }, 50);
        Thread.sleep(5000);
        if (testMicroService.resetCounter() != 1) {
            throw new RuntimeException("Expected call not received to server");
        }
        if (counter.getAndSet(0) != 101) {
            throw new RuntimeException("Expected response not received");
        }

        TriObserver<String, Long, CustomObject> toServerTri = testMicroService.clientStream();
        for (int i = 0; i < 100; i++) {
            toServerTri.update("rajesh " + i);
            toServerTri.update2((long) i);
            toServerTri.update3(new CustomObject("rajesh " + i, "" + i));
        }
        toServerTri.finish();
        Thread.sleep(5000);
        if (testMicroService.getCounter() != 301) {
            throw new RuntimeException("Expected call not received to server");
        }
    }
}
