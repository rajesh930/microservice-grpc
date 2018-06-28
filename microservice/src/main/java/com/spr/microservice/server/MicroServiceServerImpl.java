package com.spr.microservice.server;

import com.spr.microservice.annotations.MicroService;
import com.spr.microservice.core.*;
import com.spr.microservice.core.MicroServiceInfo.MethodInfo;
import io.grpc.BindableService;
import io.grpc.MethodDescriptor;
import io.grpc.ServerServiceDefinition;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 1:25 PM
 */
public class MicroServiceServerImpl implements BindableService {
    private final Object microService;
    private final MethodDescriptorProvider methodDescriptorProvider;
    private MicroServiceInfo microServiceInfo;

    public MicroServiceServerImpl(Object microService, MethodDescriptorProvider methodDescriptorProvider) {
        this.microService = microService;
        this.methodDescriptorProvider = methodDescriptorProvider;
        Class microServiceInterface = findMicroServiceInterface(microService);
        if (microServiceInterface == null) {
            throw new RuntimeException(microService.getClass().getName() + " does implement any interface having @MicroService annotation");
        }
        this.microServiceInfo = MicroServiceAnnotationReader.readMicroServiceAnnotations(microServiceInterface);
    }

    @Override
    public ServerServiceDefinition bindService() {
        ServerServiceDefinition.Builder serviceDefinition = ServerServiceDefinition.builder(microServiceInfo.getServiceName());
        for (MethodInfo methodInfo : microServiceInfo.getServiceVsMethodInfos().values()) {
            MethodDescriptor<InvocationRequest, InvocationResponse> methodDesc =
                    methodDescriptorProvider.createMethodDescriptor(microServiceInfo.getServiceName(), methodInfo);
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
    private Class findMicroServiceInterface(Object microService) {
        Class[] interfaces = microService.getClass().getInterfaces();
        for (Class apiInterface : interfaces) {
            MicroService microServiceAnnotation = (MicroService) apiInterface.getAnnotation(MicroService.class);
            if (microServiceAnnotation != null) {
                return apiInterface;
            }
        }
        return null;
    }
}
