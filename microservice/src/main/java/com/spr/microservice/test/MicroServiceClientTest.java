package com.spr.microservice.test;

import com.spr.microservice.client.MicroServiceClient;
import com.spr.microservice.core.BiObserver;
import com.spr.microservice.core.Observer;
import com.spr.microservice.core.TriObserver;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 9:13 PM
 */
public class MicroServiceClientTest {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/spring/microservice-client.xml");
        context.registerShutdownHook();
        MicroServiceClient<TestMicroService> testMicroServiceClient = new MicroServiceClient<>(TestMicroService.class);
        String response = testMicroServiceClient.get().hello("rajesh");
        System.out.println(response);
        for (int i = 0; i < 50; i++) {
            testMicroServiceClient.get().helloVoid("asdfff " + i);
        }
        Observer<String> toServer = testMicroServiceClient.get().bidirectionalStreamToServer(new BiObserver<String, Long>() {
            @Override
            public void update(String data) {
                System.out.println("String response " + data);
            }

            @Override
            public void update2(Long data) {
                System.out.println("Long response " + data);
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

        testMicroServiceClient.get().serverStream(new BiObserver<String, Long>() {
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

        TriObserver<String, Long, CustomObject> toServerTri = testMicroServiceClient.get().clientStream();
        for (int i = 0; i < 100; i++) {
            toServerTri.update("rajesh " + i);
            toServerTri.update2((long) i);
            toServerTri.update3(new CustomObject("rajesh " + i, "" + i));
        }
        toServerTri.finish();
        Thread.sleep(10000);
    }
}
