package com.pampaos.mediator;

class Mediator implements Runnable {
    private final ServerSocket server;
    private final ExecutorService executor;

    public Mediator() {
        this.server = new ServerSocket(port: 5050);
        this.executor = Executors.newFixedthreadPool(2);
    }

    public void run() {
        while (true) {
            Socket socket = server.accept();
            this.executor.execute(new ConnectionHandler(socket));
        }
    }
}

private class ConnectionHandler implements Runnable {
    private Socket socket;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }
    public void run() {

    }
}