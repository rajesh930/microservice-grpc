package co.ontic.ms.core;

import co.ontic.ms.annotations.MicroService;
import co.ontic.ms.annotations.MicroServiceMethod;
import co.ontic.ms.client.MicroServiceException;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.marshaller.DefaultMarshallerFactory;
import io.grpc.MethodDescriptor.MethodType;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

import static io.grpc.MethodDescriptor.MethodType.*;

/**
 * @author rajesh
 * @since 10/01/25 19:41
 */
public class MicroServiceAnnotationReader {
    private static final ConcurrentHashMap<Class<?>, MicroServiceInfo> microServices = new ConcurrentHashMap<>();
    private static final MarshallerFactory defaultMarshallerFactory = new DefaultMarshallerFactory();

    public static MicroServiceInfo getMicroServiceAnnotations(Class<?> microServiceInterface) {
        return microServices.computeIfAbsent(microServiceInterface, c -> readMicroServiceAnnotations(microServiceInterface));
    }

    private static MicroServiceInfo readMicroServiceAnnotations(Class<?> microServiceInterface) {
        MicroService microService = microServiceInterface.getAnnotation(MicroService.class);
        if (microService == null) {
            throw new RuntimeException(microServiceInterface.getName() + " is not annotated with MicroService");
        }
        String serviceName = microService.value();
        MarshallerFactory serviceMarshallerFactory = getMarshallerFactory(microService.marshaller(), defaultMarshallerFactory);
        MicroServiceInfo microServiceInfo = new MicroServiceInfo(serviceName);

        ReflectionUtils.doWithMethods(microServiceInterface, method -> {
            MicroServiceMethod microServiceMethodAnnotation = method.getAnnotation(MicroServiceMethod.class);
            String serviceMethodName = microServiceMethodAnnotation.name();
            if (!StringUtils.hasText(serviceMethodName)) {
                serviceMethodName = method.getName();
            }

            boolean async = microServiceMethodAnnotation.async();
            long timeout = microServiceMethodAnnotation.timeoutMillis();
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
            MethodType methodType;
            if (hasStreamingRequest && hasStreamingResponse) {
                methodType = BIDI_STREAMING;
            } else if (hasStreamingRequest) {
                methodType = CLIENT_STREAMING;
            } else if (hasStreamingResponse) {
                methodType = SERVER_STREAMING;
            } else {
                methodType = UNARY;
            }

            MethodInfo methodInfo = new MethodInfo(
                    method,
                    serviceMethodName,
                    methodType,
                    getMarshallerFactory(microServiceMethodAnnotation.marshaller(), serviceMarshallerFactory),
                    async, timeout);
            validate(microServiceInterface, methodInfo);
            microServiceInfo.addMethodInfo(methodInfo);
        }, method -> method.getAnnotation(MicroServiceMethod.class) != null);

        return microServiceInfo;
    }

    private static MarshallerFactory getMarshallerFactory(Class<? extends MarshallerFactory> marshallerClass, MarshallerFactory defaultFactory) {
        MarshallerFactory marshallerFactory = defaultFactory;
        if (marshallerClass != MarshallerFactory.class) {
            try {
                marshallerFactory = marshallerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return marshallerFactory;
    }

    private static void validate(Class<?> microServiceInterface, MethodInfo methodInfo) {
        Method method = methodInfo.method();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (methodInfo.async()) {
            if (methodInfo.methodType() == BIDI_STREAMING) {
                if (parameterTypes.length != 1) {
                    throw new MicroServiceException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() + " ]" +
                            " intended for bidirectional streaming, it should return an Observer and should have only one argument of type Observer");
                }
            } else if (methodInfo.methodType() == CLIENT_STREAMING) {
                if (parameterTypes.length != 0) {
                    throw new RuntimeException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() + " ]" +
                            " intended for client streaming, it should return an Observer and should have no arguments");
                }
            } else if (methodInfo.methodType() == SERVER_STREAMING) {
                if (!method.getReturnType().equals(Void.TYPE) || parameterTypes.length == 0) {
                    throw new RuntimeException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() + " ]" +
                            " intended for server streaming, it should return void and should have first argument of type Observer");
                }
            } else if (methodInfo.methodType() == UNARY && !method.getReturnType().equals(Void.TYPE)) {
                throw new RuntimeException("Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() + " ]" +
                        " intended for async without any client or server streaming, it should return void");
            }
        } else {
            if (Observer.class.isAssignableFrom(method.getReturnType())) {
                throw new RuntimeException("Non Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() + " ]" +
                        " can not return an Observer");
            }
            for (Class<?> parameterType : parameterTypes) {
                if (Observer.class.isAssignableFrom(parameterType)) {
                    throw new RuntimeException("Non Async method [ " + method.getName() + " ] in class [ " + microServiceInterface.getName() + " ]" +
                            " can not have any parameter of type Observer");
                }
            }
        }
    }
}
