package com.spr.microservice.core;

import io.grpc.stub.StreamObserver;

/**
 * User: rajesh
 * Date: 28/06/18
 * Time: 2:57 PM
 */
public class ObserverToStreamObserverResponse implements StreamObserver<InvocationResponse> {

    private final Observer<Object> observer;

    public ObserverToStreamObserverResponse(Observer<Object> observer) {
        this.observer = observer;
    }

    @Override
    public void onNext(InvocationResponse response) {
        if (response.getChannel() == null || response.getChannel() == 1) {
            observer.update(response.getResponse());
        } else if (response.getChannel() == 2) {
            ((BiObserver<Object, Object>) observer).update2(response.getResponse());
        } else if (response.getChannel() == 3) {
            ((TriObserver<Object, Object, Object>) observer).update3(response.getResponse());
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
