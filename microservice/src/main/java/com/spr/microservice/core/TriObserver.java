package com.spr.microservice.core;

/**
 * User: rajesh
 * Date: 20/07/17
 * Time: 1:24 PM
 */
public interface TriObserver<T, U, V> extends BiObserver<T, U> {
    void update3(V data);
}
