package co.ontic.ms.core.observers;

import co.ontic.ms.core.Request;
import co.ontic.ms.core.TriObserver;
import io.grpc.stub.StreamObserver;

/**
 * @author rajesh
 * @since 14/01/25 20:30
 */
public class RequestToStreamObserver implements TriObserver<Object, Object, Object> {

    private final StreamObserver<Request> streamObserver;

    public RequestToStreamObserver(StreamObserver<Request> streamObserver) {
        this.streamObserver = streamObserver;
    }

    @Override
    public void update(Object data) {
        streamObserver.onNext(new Request(0, data));
    }

    @Override
    public void update2(Object data) {
        streamObserver.onNext(new Request(1, data));
    }

    @Override
    public void update3(Object data) {
        streamObserver.onNext(new Request(2, data));
    }

    @Override
    public void error(Throwable t) {
        streamObserver.onError(t);
    }

    @Override
    public void finish() {
        streamObserver.onCompleted();
    }
}
