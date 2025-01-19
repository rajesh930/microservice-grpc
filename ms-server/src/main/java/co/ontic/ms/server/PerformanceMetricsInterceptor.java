package co.ontic.ms.server;

import io.grpc.*;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.util.concurrent.TimeUnit;

/**
 * @author rajesh
 * @since 19/01/25 19:36
 */
public class PerformanceMetricsInterceptor implements ServerInterceptor {

    private final MeterRegistry meterRegistry;

    public PerformanceMetricsInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata headers, ServerCallHandler<ReqT, RespT> next) {
        String methodName = call.getMethodDescriptor().getFullMethodName();
        Timer timer = meterRegistry.timer("micro.server.calls", "method", methodName);

        long startTime = System.nanoTime();
        ServerCall.Listener<ReqT> listener = next.startCall(call, headers);

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(listener) {
            @Override
            public void onComplete() {
                super.onComplete();
                long duration = System.nanoTime() - startTime;
                timer.record(duration, TimeUnit.NANOSECONDS);
            }

            @Override
            public void onCancel() {
                super.onCancel();
                long duration = System.nanoTime() - startTime;
                timer.record(duration, TimeUnit.NANOSECONDS);
            }
        };
    }
}