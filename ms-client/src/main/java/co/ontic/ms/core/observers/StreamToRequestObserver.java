package co.ontic.ms.core.observers;

import co.ontic.ms.core.BiObserver;
import co.ontic.ms.core.Observer;
import co.ontic.ms.core.Request;
import co.ontic.ms.core.TriObserver;
import io.grpc.stub.StreamObserver;

/**
 * @author rajesh
 * @since 14/01/25 20:49
 */
public class StreamToRequestObserver implements StreamObserver<Request> {
    private final Observer<Object> observer;

    public StreamToRequestObserver(Observer<Object> observer) {
        this.observer = observer;
    }

    @Override
    public void onNext(Request request) {
        if (request.channel() == 0) {
            observer.update(request.payload());
        } else if (request.channel() == 1) {
            ((BiObserver<Object, Object>) observer).update2(request.payload());
        } else if (request.channel() == 2) {
            ((TriObserver<Object, Object, Object>) observer).update3(request.payload());
        } else {
            throw new RuntimeException("Unknown channel for Observer");
        }
    }

    @Override
    public void onError(Throwable t) {
        observer.error(t);
    }

    @Override
    public void onCompleted() {
        observer.finish();
    }

}
