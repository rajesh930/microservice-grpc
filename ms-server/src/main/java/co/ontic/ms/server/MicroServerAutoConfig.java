package co.ontic.ms.server;

import co.ontic.ms.annotations.MicroService;
import co.ontic.ms.core.MethodDescriptorProvider;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Map;

/**
 * @author rajesh
 * @since 16/01/25 21:35
 */
@Configuration
@EnableConfigurationProperties(MicroServerProps.class)
public class MicroServerAutoConfig implements DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(MicroServerAutoConfig.class);

    private final MicroServerProps serverProps;
    private final MethodDescriptorProvider methodDescriptorProvider;
    private Server server;

    @Autowired
    public MicroServerAutoConfig(MicroServerProps serverProps, MethodDescriptorProvider methodDescriptorProvider) {
        this.serverProps = serverProps;
        this.methodDescriptorProvider = methodDescriptorProvider;
    }

    @EventListener
    public void startServer(ApplicationStartedEvent event) {
        String serverName = serverProps.getServerName() == null ? "Not_Provided" : serverProps.getServerName();
        int port = serverProps.getPort() == null ? 50051 : serverProps.getPort();
        ServerBuilder<?> serverBuilder = Grpc.newServerBuilderForPort(port, InsecureServerCredentials.create());
        serverBuilder.maxInboundMetadataSize(serverProps.getMaxInboundMetadataSize());
        serverBuilder.maxInboundMessageSize(serverProps.getMaxInboundMessageSize());

        Map<String, Object> services = event.getApplicationContext().getBeansWithAnnotation(MicroService.class);

        for (Object microService : services.values()) {
            serverBuilder.addService(new MicroServer(microService, methodDescriptorProvider));
        }
        serverBuilder.intercept(new UserContextServerInterceptor());
        server = serverBuilder.build();
        try {
            logger.info("Starting MicroService {} on port {}", serverName, port);
            server.start();
            logger.info("MicroService {} listening on port {}", serverName, port);
        } catch (Exception e) {
            logger.error("Failed to MicroService {}", serverName, e);
            System.exit(-1);
        }
    }

    @Override
    public void destroy() {
        if (server == null) {
            return;
        }
        String serverName = serverProps.getServerName() == null ? "Not_Provided" : serverProps.getServerName();
        try {
            logger.info("Stopping MicroService {}", serverName);
            server.shutdown();
            logger.info("MicroService {} stopped", serverName);
        } catch (Exception e) {
            logger.error("Failed to stop MicroService {}", serverName, e);
        }
    }
}
