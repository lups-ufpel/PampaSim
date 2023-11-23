import java.util.function.Consumer;

public class OtherViewModel {

    public OtherViewModel() {
        Messenger.getInstance().register("OtherMessage", this::handleOtherMessage);
    }

    private void handleOtherMessage(Object payload) {
        // Lógica para lidar com a mensagem recebida
        System.out.println("OtherViewModel recebeu uma mensagem: " + payload.toString());
    }

    public void cleanUp() {
        Messenger.getInstance().unregister("OtherMessage");
    }
}
