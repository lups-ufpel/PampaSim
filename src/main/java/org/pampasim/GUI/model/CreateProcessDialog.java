package org.pampasim.GUI.model;

import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import java.io.IOException;

public class CreateProcessDialog extends Dialog<ButtonType> {

    private final IntegerProperty burstProperty = new SimpleIntegerProperty();
    private final IntegerProperty priorityProperty = new SimpleIntegerProperty();
    private final IntegerProperty timeProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>();

    @FXML
    Spinner<Integer> burstSpinner;
    @FXML
    Spinner<Integer> prioritySpinner;
    @FXML
    Spinner<Integer> timeSpinner;
    @FXML
    private ColorPicker colorPicker;
    public CreateProcessDialog() {
        super();
        this.setTitle("Create Process Dialog");
        buildDialog();
    }


    private void buildDialog() {
        FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("/fxml/createProcessDialog.fxml"));
        dialogLoader.setController(this);
        try {
            VBox content = dialogLoader.load();
            getDialogPane().setContent(content);
        } catch (IOException e) {
            System.out.println("fudeuuuuuu");
            e.printStackTrace();
            // Handle IOException here
        }
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                burstProperty.set(burstSpinner.getValue());
                priorityProperty.set(prioritySpinner.getValue());
                timeProperty.set(timeSpinner.getValue());
                colorProperty.set(colorPicker.getValue());
                return ButtonType.OK;
            }
            return null;
        });
    }
    public IntegerProperty burstProperty() {
        return burstProperty;
    }

    public IntegerProperty priorityProperty() {
        return priorityProperty;
    }
    public IntegerProperty timeProperty() {
        return timeProperty;
    }
    public ObjectProperty<Color> colorProperty() {
        return colorProperty;
    }
}
