package co.ontic.ms.core.observers;

import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.Response;
import co.ontic.ms.core.TriObserver;
import io.grpc.stub.StreamObserver;

/**
 * @author rajesh
 * @since 13/01/25 17:43
 */
public class StreamToResponseObserver implements StreamObserver<Response> {
    private final Observer<Object> delegate;

    public StreamToResponseObserver(Observer<Object> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void onNext(Response response) {
        if (response.channel() == 0) {
            delegate.update(response.payload());
        } else if (response.channel() == 1) {
            ((BiObserver<Object, Object>) delegate).update2(response.payload());
        } else if (response.channel() == 2) {
            ((TriObserver<Object, Object, Object>) delegate).update3(response.payload());
        } else {
            throw new RuntimeException("Unknown channel for Observer");
        }
    }

    @Override
    public void onError(Throwable t) {
        delegate.error(t);
    }

    @Override
    public void onCompleted() {
        delegate.finish();
    }

    public static class NoopStreamObserver<T> implements StreamObserver<T> {
        @Override
        public void onNext(T value) {
        }

        @Override
        public void onError(Throwable t) {
        }

        @Override
        public void onCompleted() {
        }
    }
}
