package com.pampaos.mediator;

import io.grpc.ServerBuilder;

class Mediator {
    private final Server server;

    public Mediator() {
        this.server = ServerBuilder.forPort(5050)
                .addService(new PampaOsImpl())
                .build();

        this.server.start();
    }
}