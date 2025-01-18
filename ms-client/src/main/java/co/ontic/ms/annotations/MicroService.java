package co.ontic.ms.annotations;

import co.ontic.ms.core.MarshallerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rajesh
 * @since 10/01/25 19:44
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MicroService {
    /**
     * Name of microservice, this is the service name sent in grpc payload,
     * If left blank, class simple name is used
     */
    String value() default "";

    /**
     * Marshaller factory to create {@link io.grpc.MethodDescriptor.Marshaller Marshaller}
     */
    Class<? extends MarshallerFactory> marshaller() default MarshallerFactory.class;
}

