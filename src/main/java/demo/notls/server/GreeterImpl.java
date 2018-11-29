package demo.notls.server;

import com.sohu.media.grpc.demo.proto.GreeterGrpc;
import com.sohu.media.grpc.demo.proto.HelloReply;
import com.sohu.media.grpc.demo.proto.HelloRequest;
import io.grpc.stub.StreamObserver;

/**
 * Description
 *
 * @author maoyunfei
 * @date 2018-11-22
 */
public class GreeterImpl extends GreeterGrpc.GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }
}
