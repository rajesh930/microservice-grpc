package co.ontic.ms.server;

import co.ontic.ms.annotations.MicroService;
import co.ontic.ms.core.*;
import co.ontic.ms.core.MicroServiceInfo.MethodInfo;
import co.ontic.ms.core.handlers.BidiCallHandler;
import co.ontic.ms.core.handlers.ClientStreamingHandler;
import co.ontic.ms.core.handlers.ServerStreamingHandler;
import co.ontic.ms.core.handlers.UnaryCallHandler;
import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;

/**
 * @author rajesh
 * @since 17/01/25 20:52
 */
public class MicroServer implements BindableService {

    private final Object microService;
    private final MethodDescriptorProvider methodDescriptorProvider;
    private final MicroServiceInfo microServiceInfo;

    public MicroServer(Object microService, MethodDescriptorProvider methodDescriptorProvider) {
        this.microService = microService;
        this.methodDescriptorProvider = methodDescriptorProvider;
        Class<?> microServiceInterface = findMicroServiceInterface(microService);
        if (microServiceInterface == null) {
            throw new RuntimeException(microService.getClass().getName() + " does implement any interface having @MicroService annotation");
        }
        this.microServiceInfo = MicroServiceAnnotationReader.getMicroServiceAnnotations(microServiceInterface);
    }

    @Override
    public ServerServiceDefinition bindService() {
        ServerServiceDefinition.Builder serviceDefinition = ServerServiceDefinition.builder(microServiceInfo.getServiceName());
        for (MethodInfo methodInfo : microServiceInfo.getServiceVsMethodInfos().values()) {
            MethodDescriptor<Request, Response> methodDesc =
                    methodDescriptorProvider.getMethodDescriptor(microServiceInfo.getServiceName(), methodInfo);
            switch (methodDesc.getType()) {
                case UNARY:
                    serviceDefinition.addMethod(methodDesc, new UnaryCallHandler(methodInfo, microService));
                    break;
                case BIDI_STREAMING:
                    serviceDefinition.addMethod(methodDesc, new BidiCallHandler(methodInfo, microService));
                    break;
                case SERVER_STREAMING:
                    serviceDefinition.addMethod(methodDesc, new ServerStreamingHandler(methodInfo, microService));
                    break;
                case CLIENT_STREAMING:
                    serviceDefinition.addMethod(methodDesc, new ClientStreamingHandler(methodInfo, microService));
                    break;

            }
        }
        return serviceDefinition.build();
    }

    /**
     * Find interface which has @MicroService annotation defined. It is expected that microService implements one and
     * only one interface having annotation @MicroService. If it implements more than one, there is not guarantee which
     * one will be picked for create grpc service definition. If it implements none, that is an error condition
     *
     * @return first interface which has @MicroService annotation
     */
    private Class<?> findMicroServiceInterface(Object microService) {
        Class<?>[] interfaces = microService.getClass().getInterfaces();
        for (Class<?> apiInterface : interfaces) {
            MicroService microServiceAnnotation = apiInterface.getAnnotation(MicroService.class);
            if (microServiceAnnotation != null) {
                return apiInterface;
            }
        }
        return null;
    }
}
