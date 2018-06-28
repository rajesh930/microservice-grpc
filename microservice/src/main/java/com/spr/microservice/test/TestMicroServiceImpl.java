package com.spr.microservice.test;

import com.spr.microservice.core.BiObserver;
import com.spr.microservice.core.Observer;
import com.spr.microservice.core.TriObserver;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 2:56 PM
 */
public class TestMicroServiceImpl implements TestMicroService {
    @Override
    public String hello(String name) {
        System.out.println("Request came !!!!!" + name);
        return "Hi " + name + "!";
    }

    @Override
    public void helloVoid(String name) {
        System.out.println("Request came !!" + name);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Observer<String> bidirectionalStreamToServer(BiObserver<String, Long> serverResponse) {
        AtomicInteger count = new AtomicInteger(0);
        return new Observer<String>() {
            @Override
            public void update(String data) {
                serverResponse.update("Hi " + data);
                serverResponse.update2((long) count.incrementAndGet());
            }

            @Override
            public void error(Throwable t) {
                serverResponse.error(t);
            }

            @Override
            public void finish() {
                serverResponse.finish();
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
    }

    @Override
    public TriObserver<String, Long, CustomObject> clientStream() {
        return new TriObserver<String, Long, CustomObject>() {
            @Override
            public void update(String data) {
                System.out.println("Client Stream String " + data);
            }

            @Override
            public void update2(Long data) {
                System.out.println("Client Stream Long " + data);
            }

            @Override
            public void update3(CustomObject data) {
                System.out.println("Client Stream CustomObject " + data);
            }

            @Override
            public void error(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void finish() {
                System.out.println("Client Stream Finish!!!");
            }
        };
    }
}
