package demo.tls.client;

import com.sohu.media.grpc.demo.proto.GreeterGrpc;
import com.sohu.media.grpc.demo.proto.HelloReply;
import com.sohu.media.grpc.demo.proto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.commons.lang3.RandomStringUtils;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Description
 *
 * @author maoyunfei
 * @date 2018-11-22
 */
public class HelloWorldClient {
    private static final Logger logger = Logger.getLogger(HelloWorldClient.class.getName());

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    private static SslContext buildSslContext() throws SSLException {
        String clientCertChainFilePath = "sslcert/client.crt";
        String clientPrivateKeyFilePath = "sslcert/client.pem";
        String trustCertCollectionFilePath = "sslcert/ca.crt";

        ClassLoader classLoader = HelloWorldClient.class.getClassLoader();

        SslContextBuilder builder = GrpcSslContexts.forClient();
        builder.keyManager(new File(classLoader.getResource(clientCertChainFilePath).getFile()), new File(classLoader.getResource(clientPrivateKeyFilePath).getFile()));
        builder.trustManager(new File(classLoader.getResource(trustCertCollectionFilePath).getFile()));

        return builder.build();
    }

    public HelloWorldClient() throws SSLException {

        this(NettyChannelBuilder.forAddress("localhost", 50051)
                .negotiationType(NegotiationType.TLS)
                .sslContext(buildSslContext())
                .build());
    }

    HelloWorldClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * Say hello to server.
     */
    public void greet(String name) {
//        logger.info("Will try to greet " + name + " ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
//        logger.info("Greeting: " + response.getMessage());
    }

    public static void main(String[] args) throws Exception {

        HelloWorldClient client = new HelloWorldClient();

        try {
            String user = RandomStringUtils.random(3200, true,true);
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
                client.greet(user);
            }
            long end = System.currentTimeMillis();
            System.out.println("10000 times costs " + (end - start));
        } finally {
            client.shutdown();
        }
    }
}
