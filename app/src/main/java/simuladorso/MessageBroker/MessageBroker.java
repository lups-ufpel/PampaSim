package simuladorso.MessageBroker;

import simuladorso.Logger.Logger;

public class MessageBroker implements Runnable {
    private final Logger logger = new Logger();

    public MessageBroker() {

    }

    @Override
    public void run() {

    }

    public Logger getLogger() {
        return logger;
    }

    public synchronized void handleMessage(Message message) {
        logger.debug(String.format("MessageBroker received: %s", message.toString()));


    }
}
