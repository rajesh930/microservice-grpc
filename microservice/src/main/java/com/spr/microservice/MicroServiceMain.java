package com.spr.microservice;

import com.spr.microservice.server.MicroServiceServerImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.Map;

/**
 * User: rajesh
 * Date: 27/06/18
 * Time: 1:11 PM
 */
public class MicroServiceMain {
    private static final Logger logger = LoggerFactory.getLogger(MicroServiceMain.class);
    private static String microServiceName;
    private static int port;
    private static Server server;

    public static void main(String[] args) {
        microServiceName = System.getenv("MICRO_SERVICE_NAME");
        if (microServiceName == null || microServiceName.trim().equals("")) {
            logger.error("Environment variable [ MICRO_SERVICE_NAME ] not defined");
            System.exit(-1);
        }
        String portStr = System.getenv("MICRO_SERVICE_PORT");
        if (portStr == null || portStr.trim().equals("")) {
            logger.error("Environment variable [ MICRO_SERVICE_PORT ] not defined");
            System.exit(-1);
        }
        port = Integer.parseInt(portStr);

        logger.info("Initializing MicroService through spring...");
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/spring/microservice-" + microServiceName + ".xml");
        context.registerShutdownHook();
        logger.info("MicroService Spring Initialized");

        Map<String, MicroServiceServerImpl> microServices = context.getBeansOfType(MicroServiceServerImpl.class);

        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(port);
        for (MicroServiceServerImpl microServiceServer : microServices.values()) {
            serverBuilder.addService(microServiceServer);
        }
        server = serverBuilder.build();
        try {
            start();
        } catch (Exception e) {
            logger.error("Failed to start server", e);
            System.exit(-1);
        }
        try {
            blockUntilShutdown();
        } catch (Exception e) {
            logger.error("Could not wait for server to shutdown", e);
        }
    }

    private static void start() throws IOException {
        logger.info("Starting MicroService {} on port {}", microServiceName, port);
        server.start();
        logger.info("MicroService {} listening on port {}", microServiceName, port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("Shutting down MicroService ");
                server.shutdown();
                logger.info("GreeterServer shut down");
            }
        });
    }

    private static void blockUntilShutdown() throws InterruptedException {
        server.awaitTermination();
    }
}
