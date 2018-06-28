package com.spr.microservice.client;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 7:20 PM
 */
public class LazySpringService<T> {

    private final Class<? extends T> serviceClass;
    private volatile T serviceInstance;

    public LazySpringService(Class<? extends T> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public T get() {
        if (serviceInstance != null) {
            return serviceInstance;
        }
        synchronized (this) {
            if (serviceInstance != null) {
                return serviceInstance;
            }
            serviceInstance = ApplicationContextProvider.getApplicationContext().getBean(serviceClass);
        }
        return serviceInstance;
    }
}
