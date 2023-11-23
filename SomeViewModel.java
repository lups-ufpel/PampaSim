import java.util.function.Consumer;

public class SomeViewModel {

    public SomeViewModel() {
        Messenger.getInstance().register("SomeMessage", this::handleSomeMessage);
    }

    private void handleSomeMessage(Object payload) {
        // Lógica para lidar com a mensagem recebida
        System.out.println("SomeViewModel recebeu uma mensagem: " + payload.toString());
    }

    public void sendMessageToOtherViewModel() {
        Messenger.getInstance().send("OtherMessage", "Mensagem de SomeViewModel");
    }

    public void cleanUp() {
        Messenger.getInstance().unregister("SomeMessage");
    }
}
