package simuladorso.MessageBroker;

import simuladorso.Logger.Logger;

import java.util.HashMap;

public class MessageBroker implements Runnable {
    private Logger logger;

    private final HashMap<String, Messager> party = new HashMap<>();

    public MessageBroker() {

    }

    public void subscribe(String id, Messager messager) {
        
    }

    @Override
    public void run() {

    }
}
