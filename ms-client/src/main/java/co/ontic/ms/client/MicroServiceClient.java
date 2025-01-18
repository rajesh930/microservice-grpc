package co.ontic.ms.client;

import co.ontic.ms.core.MicroServiceAnnotationReader;
import co.ontic.ms.core.MicroServiceInfo;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.Request;
import co.ontic.ms.core.Response;
import co.ontic.ms.core.handlers.BidiCallHandler;
import co.ontic.ms.core.handlers.ClientStreamingHandler;
import co.ontic.ms.core.handlers.ServerStreamingHandler;
import co.ontic.ms.core.handlers.UnaryCallHandler;
import io.grpc.ManagedChannel;
import io.grpc.MethodDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import static io.grpc.MethodDescriptor.MethodType.*;

/**
 * @author rajesh
 * @since 09/01/25 14:21
 */
public class MicroServiceClient<T> implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(MicroServiceClient.class);

    private final Class<T> service;
    private String serviceName;
    private Supplier<ChannelFactory> channelFactory;

    private T proxy;
    private MicroServiceInfo microServiceInfo;

    public MicroServiceClient(Class<T> service) {
        this(service, null, null);
    }

    public MicroServiceClient(Class<T> service, String serviceName) {
        this(service, serviceName, null);
    }

    public MicroServiceClient(Class<T> service, String serviceName, Supplier<ChannelFactory> channelFactory) {
        this.service = service;
        this.serviceName = serviceName;
        this.channelFactory = channelFactory;
    }

    public T get() {
        if (proxy != null) {
            return proxy;
        }
        synchronized (this) {
            if (proxy != null) {
                return proxy;
            }
            this.microServiceInfo = MicroServiceAnnotationReader.getMicroServiceAnnotations(service);
            if (serviceName == null) {
                serviceName = this.microServiceInfo.getServiceName();
            }
            if (channelFactory == null) {
                channelFactory = ApplicationServices::getDefaultChannelFactory;
            }
            //noinspection unchecked
            this.proxy = (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, this);
        }
        return proxy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        MethodInfo methodInfo = microServiceInfo.getMethodInfoByMethod(method);
        ManagedChannel channel = channelFactory.get().getOrCreateChannel(serviceName); //use given service to get channel

        MethodDescriptor<Request, Response> methodDescriptor = ApplicationServices.getMethodDescriptorProvider().getMethodDescriptor(
                microServiceInfo.getServiceName(), methodInfo);
        if (logger.isDebugEnabled()) {
            logger.debug("Calling service [ {} ] on channel [ {} ]", methodDescriptor.getFullMethodName(), channel);
        }
        try {
            if (methodInfo.methodType() == BIDI_STREAMING) {
                return BidiCallHandler.doAsyncCall(channel, methodDescriptor, args);
            } else if (methodInfo.methodType() == SERVER_STREAMING) {
                ServerStreamingHandler.doAsyncCall(channel, methodDescriptor, args);
                return null;
            } else if (methodInfo.methodType() == CLIENT_STREAMING) {
                return ClientStreamingHandler.doAsyncCall(channel, methodDescriptor);
            } else if (methodInfo.methodType() == UNARY) {
                if (methodInfo.async()) {
                    UnaryCallHandler.doAsyncCall(channel, methodDescriptor, args);
                    return null;
                } else {
                    return UnaryCallHandler.doBlockingCall(channel, methodDescriptor, args);
                }
            } else {
                throw new MicroServiceException("Can not handle the method [ " + methodInfo + " ]");
            }
        } catch (Throwable t) {
            logger.error("Service call [ {} ] failed on channel [ {} ] failed", methodDescriptor.getFullMethodName(), channel, t);
            throw t;
        }
    }
}
