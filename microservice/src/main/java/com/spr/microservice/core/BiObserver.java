package com.spr.microservice.core;

/**
 * User: rajesh
 * Date: 20/07/17
 * Time: 1:22 PM
 */
public interface BiObserver<T, U> extends Observer<T> {
    void update2(U data);
}
