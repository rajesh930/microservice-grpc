package co.ontic.ms.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author rajesh
 * @since 09/01/25 14:40
 */
@ConfigurationProperties(prefix = "ontic.ms.server")
public class MicroServerProps {
    /**
     * Name of micro server
     */
    private String serverName;

    /**
     * Port on which server should listen
     */
    private Integer port;

    /**
     * Max inbound metadata/header size
     */
    private int maxInboundMetadataSize = 30 * 1024;

    /**
     * Max inbound message size
     */
    private int maxInboundMessageSize = 10 * 1024 * 1024;

    /**
     * If performance metric should be captured
     */
    private boolean perfEnabled = true;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public int getMaxInboundMetadataSize() {
        return maxInboundMetadataSize;
    }

    public void setMaxInboundMetadataSize(int maxInboundMetadataSize) {
        this.maxInboundMetadataSize = maxInboundMetadataSize;
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }

    public void setMaxInboundMessageSize(int maxInboundMessageSize) {
        this.maxInboundMessageSize = maxInboundMessageSize;
    }

    public boolean isPerfEnabled() {
        return perfEnabled;
    }

    public void setPerfEnabled(boolean perfEnabled) {
        this.perfEnabled = perfEnabled;
    }
}
