package com.spr.microservice.test;

import com.spr.microservice.annotations.MicroService;
import com.spr.microservice.annotations.MicroServiceMethod;
import com.spr.microservice.core.BiObserver;
import com.spr.microservice.core.Observer;
import com.spr.microservice.core.TriObserver;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 9:00 PM
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
    TriObserver<String, Long, CustomObject> clientStream();
}
