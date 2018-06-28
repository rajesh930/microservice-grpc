package com.spr.microservice.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 3:55 PM
 */
public class MicroServiceInfo {
    private final String serviceName;
    private final Map<String, MethodInfo> serviceVsMethodInfos = new HashMap<>();
    private final Map<Method, MethodInfo> methodVsMethodInfos = new HashMap<>();

    public MicroServiceInfo(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Map<String, MethodInfo> getServiceVsMethodInfos() {
        return serviceVsMethodInfos;
    }

    public void addMethodInfo(MethodInfo methodInfo) {
        serviceVsMethodInfos.put(methodInfo.getServiceMethodName(), methodInfo);
        methodVsMethodInfos.put(methodInfo.getMethod(), methodInfo);
    }

    public MethodInfo getMethodInfoByServiceName(String serviceMethodName) {
        return serviceVsMethodInfos.get(serviceMethodName);
    }

    public MethodInfo getMethodInfoByMethod(Method method) {
        return methodVsMethodInfos.get(method);
    }

    public static class MethodInfo {
        private final Method method;
        private final String serviceMethodName;
        private final boolean async;
        private final long timeout;
        private final int retries;
        private final boolean hasStreamingRequest;
        private final boolean hasStreamingResponse;

        public MethodInfo(Method method, String serviceMethodName, boolean async, long timeout, int retries, boolean hasStreamingRequest, boolean hasStreamingResponse) {
            this.method = method;
            this.serviceMethodName = serviceMethodName;
            this.async = async;
            this.timeout = timeout;
            this.retries = retries;
            this.hasStreamingRequest = hasStreamingRequest;
            this.hasStreamingResponse = hasStreamingResponse;
        }

        public Method getMethod() {
            return method;
        }

        public String getServiceMethodName() {
            return serviceMethodName;
        }

        public boolean isAsync() {
            return async;
        }

        public long getTimeout() {
            return timeout;
        }

        public int getRetries() {
            return retries;
        }

        public boolean isHasStreamingRequest() {
            return hasStreamingRequest;
        }

        public boolean isHasStreamingResponse() {
            return hasStreamingResponse;
        }

        public boolean isVoidReturn() {
            return method.getReturnType().equals(Void.TYPE);
        }
    }
}
