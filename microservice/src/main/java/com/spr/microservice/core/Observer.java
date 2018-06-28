package com.spr.microservice.core;

/**
 * User: rajesh
 * Date: 10/02/16
 * Time: 4:18 PM
 */
public interface Observer<T> {
    void update(T data);

    void error(Throwable t);

    void finish();
}
