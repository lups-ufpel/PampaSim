package org.pampaos.mediator;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;

import org.pampaos.mediator.services.MediatorService;

public class App
{
    public static void main( String[] args ) throws IOException, InterruptedException
    {
        Server server = ServerBuilder.forPort(5050)
                .addService(new MediatorService())
                .build();

        server.start();

        // Colocar logger aqui

        server.awaitTermination();
    }
}
