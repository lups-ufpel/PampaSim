import java.util.function.Consumer;

public class OtherViewModel {

    public int contador =0;

    public OtherViewModel() {
        Messenger.getInstance().register("OtherMessage", this::handleOtherMessage);
    }

    private void handleOtherMessage(String payload) {
        // Lógica para lidar com a mensagem recebida
        System.out.println("OtherViewModel recebeu uma mensagem: " + payload);
        contador = 10;
    }

    public void cleanUp() {
        Messenger.getInstance().unregister("OtherMessage");
    }
}
