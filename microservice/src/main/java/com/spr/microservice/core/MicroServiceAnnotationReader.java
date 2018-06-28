package com.spr.microservice.core;

import com.spr.microservice.annotations.MicroService;
import com.spr.microservice.annotations.MicroServiceMethod;
import com.spr.microservice.core.MicroServiceInfo.MethodInfo;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 3:33 PM
 */
public class MicroServiceAnnotationReader {
    public static MicroServiceInfo readMicroServiceAnnotations(Class microServiceInterface) {
        MicroService microService = (MicroService) microServiceInterface.getAnnotation(MicroService.class);
        if (microService == null) {
            throw new RuntimeException(microServiceInterface.getName() + " is not annotated with MicroService");
        }
        String serviceName = microService.value();
        MicroServiceInfo microServiceInfo = new MicroServiceInfo(serviceName);

        ReflectionUtils.doWithMethods(microServiceInterface, method -> {
            MicroServiceMethod microServiceMethodAnnotation = method.getAnnotation(MicroServiceMethod.class);
            String serviceMethodName = microServiceMethodAnnotation.name();
            if (StringUtils.isEmpty(serviceMethodName)) {
                serviceMethodName = method.getName();
            }

            boolean async = microServiceMethodAnnotation.async();
            long timeout = microServiceMethodAnnotation.timeout();
            int retries = microServiceMethodAnnotation.retries();
            boolean hasStreamingRequest = false;
            boolean hasStreamingResponse = false;
            if (async) {
                if (Observer.class.isAssignableFrom(method.getReturnType())) {
                    hasStreamingRequest = true;
                }
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length > 0) {
                    if (Observer.class.isAssignableFrom(parameterTypes[0])) {
                        //has observer, return type will be notified using observer
                        hasStreamingResponse = true;
                    }
                }
            }
            MethodInfo methodInfo = new MethodInfo(method, serviceMethodName, async, timeout, retries, hasStreamingRequest, hasStreamingResponse);
            validate(microServiceInterface, methodInfo);
            microServiceInfo.addMethodInfo(methodInfo);
        }, method -> method.getAnnotation(MicroServiceMethod.class) != null);

        return microServiceInfo;
    }

    private static void validate(Class microServiceInterface, MethodInfo methodInfo) {
        Method method = methodInfo.getMethod();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (methodInfo.isAsync()) {
            if (methodInfo.isHasStreamingRequest() && methodInfo.isHasStreamingResponse()) {
                if (parameterTypes.length != 1) {
                    throw new RuntimeException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() +
                            " ] intended for bidirectional streaming, it should return an Observer and should have only one argument of type Observer");
                }
            } else if (methodInfo.isHasStreamingRequest() && !methodInfo.isHasStreamingResponse()) {
                if (parameterTypes.length != 0) {
                    throw new RuntimeException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() +
                            " ] intended for client streaming, it should return an Observer and should have no arguments");
                }
            } else if (!methodInfo.isHasStreamingRequest() && methodInfo.isHasStreamingResponse()) {
                if (!method.getReturnType().equals(Void.TYPE) || parameterTypes.length != 1) {
                    throw new RuntimeException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() +
                            " ] intended for server streaming, it should return void and should have only one argument of type Observer");
                }
            } else if (!method.getReturnType().equals(Void.TYPE)) {
                throw new RuntimeException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() +
                        " ] intended for async without any client or server streaming, it should return void");
            }
        } else {
            if (Observer.class.isAssignableFrom(method.getReturnType())) {
                throw new RuntimeException("Non Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() +
                        " ] can not return an Observer");
            }
            for (Class<?> parameterType : parameterTypes) {
                if (Observer.class.isAssignableFrom(parameterType)) {
                    throw new RuntimeException("Non Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() +
                            " ] can not have any parameter of type Observer");
                }
            }
        }
    }
}
