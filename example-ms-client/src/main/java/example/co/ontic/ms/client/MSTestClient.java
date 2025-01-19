package example.co.ontic.ms.client;

import co.ontic.ms.client.MicroServiceClient;
import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.TriObserver;
import co.ontic.ms.core.UserContextHandler;
import example.co.ontic.ms.client.ExampleUserContextHandler.UserContext;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rajesh
 * @since 17/01/25 22:59
 */
@SuppressWarnings("CallToPrintStackTrace")
@SpringBootApplication
public class MSTestClient implements CommandLineRunner {

    @Bean
    public UserContextHandler userContextHandler() {
        return new ExampleUserContextHandler();
    }

    public static void main(String[] args) {
        SpringApplication.run(MSTestClient.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        UserContext context = UserContext.startNew();
        try {
            testCallsSuccess();
            testContextPropagation();
        } finally {
            context.stop();
        }
    }

    private void testCallsSuccess() throws InterruptedException {
        MicroServiceClient<TestMicroService> testMicroServiceClient = new MicroServiceClient<>(TestMicroService.class);
        TestMicroService testMicroService = testMicroServiceClient.get();
        testMicroService.resetCounter();

        String response = testMicroService.hello("rajesh");
        if (response == null) {
            throw new RuntimeException("un expected response");
        }
        System.out.println(response);
        if (testMicroService.resetCounter() != 1) {
            throw new RuntimeException("Expected call not received to server");
        }
        for (int i = 0; i < 50; i++) {
            testMicroService.helloVoid("asdfff " + i);
        }
        Thread.sleep(5000);
        if (testMicroService.resetCounter() != 50) {
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
        if (testMicroService.resetCounter() != 102) {
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
        if (testMicroService.getCounter() != 302) {
            throw new RuntimeException("Expected call not received to server");
        }
    }

    private void testContextPropagation() throws InterruptedException {
        MicroServiceClient<ContextPropagationTestService> contextPropagationService = new MicroServiceClient<>(ContextPropagationTestService.class);
        ContextPropagationTestService contextPropagation = contextPropagationService.get();

        int userId = setRandomUserInContext();
        String response = contextPropagation.hello("rajesh");
        if (response == null) {
            throw new RuntimeException("un expected response");
        }
        System.out.println(response);
        Assert.isTrue(contextPropagation.lastUser() == userId, "Expected user not received");

        userId = setRandomUserInContext();
        contextPropagation.helloVoid("asdfff ");
        Thread.sleep(1000);
        Assert.isTrue(contextPropagation.lastUser() == userId, "Expected user not received");

        AtomicInteger counter = new AtomicInteger(0);

        userId = setRandomUserInContext();
        Observer<String> toServer = contextPropagation.bidirectionalStreamToServer(new BiObserver<>() {
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
        Assert.isTrue(contextPropagation.lastUser() == userId, "Expected user not received");
        for (int i = 0; i < 100; i++) {
            toServer.update("rajesh " + i);
        }
        toServer.finish();
        Thread.sleep(1000);
        if (counter.getAndSet(0) != 201) {
            throw new RuntimeException("Expected response not received");
        }

        userId = setRandomUserInContext();
        contextPropagation.serverStream(new BiObserver<>() {
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
        Assert.isTrue(contextPropagation.lastUser() == userId, "Expected user not received");
        if (counter.getAndSet(0) != 201) {
            throw new RuntimeException("Expected response not received");
        }

        userId = setRandomUserInContext();
        contextPropagation.serverStreamWithArgs(new BiObserver<>() {
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
        }, "rajesh", "kumar");

        Thread.sleep(5000);
        Assert.isTrue(contextPropagation.lastUser() == userId, "Expected user not received");
        if (counter.getAndSet(0) != 21) {
            throw new RuntimeException("Expected response not received");
        }

        userId = setRandomUserInContext();
        TriObserver<String, Long, String> toServerTri = contextPropagation.clientStream();
        Assert.isTrue(contextPropagation.lastUser() == userId, "Expected user not received");
        for (int i = 0; i < 100; i++) {
            toServerTri.update("rajesh " + i);
            toServerTri.update2((long) i);
            toServerTri.update3("rajesh " + i);
        }
        toServerTri.finish();
        Thread.sleep(5000);
    }

    private final Random random = new Random();

    private int setRandomUserInContext() {
        int userId = random.nextInt();
        UserContext.current().setContextVariable("USER", userId);
        System.out.println("user id " + userId);
        return userId;
    }
}
