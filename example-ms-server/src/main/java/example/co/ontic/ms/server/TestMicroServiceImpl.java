package example.co.ontic.ms.server;

import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.TriObserver;
import example.co.ontic.ms.client.CustomObject;
import example.co.ontic.ms.client.TestMicroService;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rajesh
 * @since 17/01/25 22:10
 */
@SuppressWarnings("CallToPrintStackTrace")
public class TestMicroServiceImpl implements TestMicroService {
    private final AtomicInteger counter = new AtomicInteger(0);

    public int resetCounter() {
        return counter.getAndSet(0);
    }

    public int getCounter() {
        return counter.get();
    }

    @Override
    public String hello(String name) {
        System.out.println("Request came !!!!!" + name);
        counter.incrementAndGet();
        return "Hi " + name + "!";
    }

    @Override
    public void helloVoid(String name) {
        System.out.println("Request came !!" + name);
        counter.incrementAndGet();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Observer<String> bidirectionalStreamToServer(BiObserver<String, Long> serverResponse) {
        AtomicInteger count = new AtomicInteger(0);
        return new Observer<>() {
            @Override
            public void update(String data) {
                System.out.println("bidi request " + data);
                serverResponse.update("Hi " + data);
                serverResponse.update2((long) count.getAndIncrement());
                counter.incrementAndGet();
            }

            @Override
            public void error(Throwable t) {
                serverResponse.error(t);
                counter.incrementAndGet();
            }

            @Override
            public void finish() {
                serverResponse.finish();
                counter.incrementAndGet();
            }
        };
    }

    @Override
    public void serverStream(BiObserver<String, Long> serverResponse) {
        for (int i = 0; i < 100; i++) {
            serverResponse.update("Hi xyz " + i);
            serverResponse.update2((long) i);
        }
        serverResponse.finish();
        counter.incrementAndGet();
    }

    @Override
    public void serverStreamWithArgs(BiObserver<String, Long> serverResponse, int times) {
        for (int i = 0; i < times; i++) {
            serverResponse.update("Hi abc " + i);
            serverResponse.update2((long) i);
        }
        serverResponse.finish();
        counter.incrementAndGet();
    }

    @Override
    public TriObserver<String, Long, CustomObject> clientStream() {
        return new TriObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("Client Stream String " + data);
                counter.incrementAndGet();
            }

            @Override
            public void update2(Long data) {
                System.out.println("Client Stream Long " + data);
                counter.incrementAndGet();
            }

            @Override
            public void update3(CustomObject data) {
                System.out.println("Client Stream CustomObject " + data);
                counter.incrementAndGet();
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
                counter.incrementAndGet();
            }

            @Override
            public void finish() {
                System.out.println("Client Stream Finish!!!");
                counter.incrementAndGet();
            }
        };
    }
}
