import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Messenger {
    private static Messenger instance;
    private Map<String, Consumer<Object>> registeredActions;

    private Messenger() {
        registeredActions = new HashMap<>();
    }

    public static Messenger getInstance() {
        if (instance == null) {
            instance = new Messenger();
        }
        return instance;
    }

    public void send(String messageKey, Object payload) {
        if (registeredActions.containsKey(messageKey)) {
            registeredActions.get(messageKey).accept(payload);
        }
    }

    public void register(String messageKey, Consumer<Object> action) {
        registeredActions.put(messageKey, action);
    }

    public void unregister(String messageKey) {
        registeredActions.remove(messageKey);
    }
}
