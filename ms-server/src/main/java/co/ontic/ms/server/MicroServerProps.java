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
}
