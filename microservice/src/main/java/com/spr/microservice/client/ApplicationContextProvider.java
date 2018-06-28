package com.spr.microservice.client;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 7:13 PM
 */
public class ApplicationContextProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null) {
            throw new RuntimeException("Application context requested before spring initialized");
        }
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        //noinspection AccessStaticViaInstance
        this.applicationContext = applicationContext;
    }
}
