package com.spr.microservice.core;

import io.grpc.stub.StreamObserver;

/**
 * User: rajesh
 * Date: 28/06/18
 * Time: 12:12 PM
 */
public class StreamObserverToObserverRequest implements TriObserver<Object, Object, Object> {

    private final StreamObserver<InvocationRequest> streamObserver;

    public StreamObserverToObserverRequest(StreamObserver<InvocationRequest> streamObserver) {
        this.streamObserver = streamObserver;
    }

    @Override
    public void update(Object data) {
        streamObserver.onNext(new InvocationRequest(data, (byte) 1));
    }

    @Override
    public void update2(Object data) {
        streamObserver.onNext(new InvocationRequest(data, (byte) 2));
    }

    @Override
    public void update3(Object data) {
        streamObserver.onNext(new InvocationRequest(data, (byte) 3));
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
