package co.ontic.ms.core;

import com.google.common.base.MoreObjects;
import io.grpc.MethodDescriptor.MethodType;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rajesh
 * @since 11/01/25 12:37
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
        serviceVsMethodInfos.put(methodInfo.serviceMethodName(), methodInfo);
        methodVsMethodInfos.put(methodInfo.method(), methodInfo);
    }

    public MethodInfo getMethodInfoByServiceName(String serviceMethodName) {
        return serviceVsMethodInfos.get(serviceMethodName);
    }

    public MethodInfo getMethodInfoByMethod(Method method) {
        return methodVsMethodInfos.get(method);
    }

    public record MethodInfo(Method method,
                             String serviceMethodName,
                             MethodType methodType,
                             MarshallerFactory marshallerFactory,
                             boolean async,
                             long timeout) {

        public boolean isVoidReturn() {
            return method.getReturnType().equals(Void.TYPE);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("method", method)
                    .add("serviceMethodName", serviceMethodName)
                    .add("methodType", methodType)
                    .add("marshallerFactory", marshallerFactory)
                    .add("async", async)
                    .add("timeout", timeout)
                    .toString();
        }
    }

}
