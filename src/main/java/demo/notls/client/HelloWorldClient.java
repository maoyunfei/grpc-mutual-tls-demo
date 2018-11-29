package demo.notls.client;

import com.sohu.media.grpc.demo.proto.GreeterGrpc;
import com.sohu.media.grpc.demo.proto.HelloReply;
import com.sohu.media.grpc.demo.proto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.commons.lang3.RandomStringUtils;

import javax.net.ssl.SSLException;
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

    public HelloWorldClient() throws SSLException {

        this(ManagedChannelBuilder.forAddress("localhost", 50052)
                .usePlaintext()
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
            String user = RandomStringUtils.random(800, true,true);
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
