package co.ontic.ms.client;

import com.google.common.base.MoreObjects;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * @author rajesh
 * @since 10/01/25 18:21
 */
public class ServiceEndpoint {
    /**
     * Server where service is running
     */
    private String address;
    /**
     * If server is accepting tls not plain text
     */
    private Boolean useTLS;
    /**
     * Set the duration without any method call before going to idle mode. In idle mode the channel shuts down
     * all connections. A new method call would take the channel out of idle mode. A channel starts in idle mode.
     * Defaults to 30 minutes.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration idleTimeout;
    /**
     * Sets the time without read activity before sending a keepalive ping. An unreasonably small value might be
     * increased and Long.MAX_VALUE nano seconds or an unreasonably large value will disable keepalive. Defaults to
     * infinite. Clients must receive permission from the service owner before enabling this option.
     * Keepalives can increase the load on services and are commonly "invisible" making it hard to notice when
     * they are causing excessive load. Clients are strongly encouraged to use only as small of a value as necessary.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration keepAliveTime;
    /**
     * Sets the time waiting for read activity after sending a keepalive ping. If the time expires without any read
     * activity on the connection, the connection is considered dead. An unreasonably small value might be
     * increased. Defaults to 20 seconds.
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration keepAliveTimeout;
    /**
     * Disables the retry and hedging subsystem provided by the gRPC library. This is designed for the
     * case when users have their own retry implementation and want to avoid their own retry taking
     * place simultaneously with the gRPC library layer retry.
     */
    private Boolean disableRetry;
    /**
     * Provides a service config to the channel. Contains default service config, or per service config.
     * Refer io.grpc.internal.ManagedChannelImplBuilder for format or
     * <a href="https://github.com/grpc/grpc-java/blob/master/examples/src/main/resources/io/grpc/examples/retrying/retrying_service_config.json">...</a>
     */
    private Map<String, ?> serviceConfig;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getUseTLS() {
        return useTLS;
    }

    public void setUseTLS(Boolean useTLS) {
        this.useTLS = useTLS;
    }

    public Duration getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(Duration idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public Duration getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public Duration getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    public void setKeepAliveTimeout(Duration keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    public Boolean getDisableRetry() {
        return disableRetry;
    }

    public void setDisableRetry(Boolean disableRetry) {
        this.disableRetry = disableRetry;
    }

    public Map<String, ?> getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(Map<String, ?> serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("address", address)
                .add("usePlainText", useTLS)
                .add("idleTimeout", idleTimeout)
                .add("keepAliveTime", keepAliveTime)
                .add("keepAliveTimeout", keepAliveTimeout)
                .add("disableRetry", disableRetry)
                .add("serviceConfig", serviceConfig)
                .toString();
    }
}
