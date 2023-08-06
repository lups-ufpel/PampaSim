package simuladorso.GUI;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;

public class CreateProcessDialog extends Dialog<String> {
    private Label processTypeLbl;
    private ComboBox<String> processTypeCbx;
    private static final String[] processTypes = {
            "CPU-Bound",
            "IO-Bound",
            "CPU-IO-Bound"
    };

    public CreateProcessDialog() {
        super();
        setTitle("New process");
        setResizable(true);

        processTypeLbl = new Label("Process type:");

        processTypeCbx = new ComboBox<>();
        processTypeCbx.getItems().addAll(processTypes);
    }
}