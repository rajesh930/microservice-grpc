package com.spr.microservice.core;

import io.grpc.stub.StreamObserver;

/**
 * User: rajesh
 * Date: 28/06/18
 * Time: 2:57 PM
 */
public class ObserverToStreamObserverRequest implements StreamObserver<InvocationRequest> {

    private final Observer<Object> observer;

    public ObserverToStreamObserverRequest(Observer<Object> observer) {
        this.observer = observer;
    }

    @Override
    public void onNext(InvocationRequest request) {
        if (request.getChannel() == null || request.getChannel() == 1) {
            observer.update(request.getRequest());
        } else if (request.getChannel() == 2) {
            ((BiObserver<Object, Object>) observer).update2(request.getRequest());
        } else if (request.getChannel() == 3) {
            ((TriObserver<Object, Object, Object>) observer).update3(request.getRequest());
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
