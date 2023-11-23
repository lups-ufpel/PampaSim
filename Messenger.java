import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Messenger <T>{
    private static Messenger instance;
    private Map<String, Consumer<Object>> registeredActions;

    private Messenger() {
        registeredActions = new HashMap<>();
    }

    public static Messenger getInstance(int tipo) {
        if (instance == null) {
            if(tipo == 0){
                instance = new Messenger<String>();
            }
            else{instance = new Messenger();
            }
        }
        return instance;
    }

    public void send (String messageKey, T payload) {
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
