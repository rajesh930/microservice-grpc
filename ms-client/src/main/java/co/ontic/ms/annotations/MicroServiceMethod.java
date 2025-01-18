package co.ontic.ms.annotations;

import co.ontic.ms.core.MarshallerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author rajesh
 * @since 10/01/25 19:45
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MicroServiceMethod {
    String name() default "";

    boolean async() default false;

    long timeoutMillis() default -1;

    /**
     * Marshaller factory to create {@link io.grpc.MethodDescriptor.Marshaller Marshaller}, overrides defined at service level
     * If value is MarshallerFactory.class marshaller defined at service level gets used
     */
    Class<? extends MarshallerFactory> marshaller() default MarshallerFactory.class;
}
