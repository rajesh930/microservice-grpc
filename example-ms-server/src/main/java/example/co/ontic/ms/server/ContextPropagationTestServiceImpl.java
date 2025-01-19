package example.co.ontic.ms.server;

import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.TriObserver;
import example.co.ontic.ms.client.ContextPropagationTestService;
import example.co.ontic.ms.client.ExampleUserContextHandler.UserContext;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rajesh
 * @since 19/01/25 17:07
 */
public class ContextPropagationTestServiceImpl implements ContextPropagationTestService {
    private int lastUser;

    @Override
    public String hello(String name) {
        System.out.println("hello " + name);
        captureCurrentContext();
        return "hello " + name;
    }

    @Override
    public void helloVoid(String name) {
        System.out.println("helloVoid " + name);
        captureCurrentContext();
    }

    @Override
    public Observer<String> bidirectionalStreamToServer(BiObserver<String, Long> serverResponse) {
        captureCurrentContext();
        AtomicInteger count = new AtomicInteger(0);
        return new Observer<>() {
            @Override
            public void update(String data) {
                System.out.println("Hi bidirectionalStreamToServer " + data);
                serverResponse.update("Hi bidirectionalStreamToServer " + data);
                serverResponse.update2((long) count.getAndIncrement());
            }

            @Override
            public void error(Throwable t) {
                //noinspection CallToPrintStackTrace
                t.printStackTrace();
                serverResponse.error(t);
            }

            @Override
            public void finish() {
                System.out.println("bidi finish");
                serverResponse.finish();
            }
        };
    }

    @Override
    public void serverStream(BiObserver<String, Long> serverResponse) {
        captureCurrentContext();
        for (int i = 0; i < 100; i++) {
            serverResponse.update("Hi serverStream " + i);
            serverResponse.update2((long) i);
        }
        serverResponse.finish();
    }

    @Override
    public void serverStreamWithArgs(BiObserver<String, Long> serverResponse, String firstName, String lastName) {
        captureCurrentContext();
        for (int i = 0; i < 10; i++) {
            serverResponse.update("Hi serverStreamWithArgs " + firstName + " " + lastName + " " + i);
            serverResponse.update2((long) i);
        }
        serverResponse.finish();
    }

    @Override
    public TriObserver<String, Long, String> clientStream() {
        captureCurrentContext();
        return new TriObserver<>() {
            @Override
            public void update(String data) {
                System.out.println("Client Stream String " + data);
            }

            @Override
            public void update2(Long data) {
                System.out.println("Client Stream Long " + data);
            }

            @Override
            public void update3(String data) {
                System.out.println("Client Stream CustomObject " + data);
            }

            @Override
            public void error(Throwable t) {
                //noinspection CallToPrintStackTrace
                t.printStackTrace();
            }

            @Override
            public void finish() {
                System.out.println("Client Stream Finish!!!");
            }
        };
    }

    @Override
    public int lastUser() {
        return lastUser;
    }

    private void captureCurrentContext() {
        UserContext current = UserContext.current();
        this.lastUser = current.getContextVariable("USER", -1);
    }
}
