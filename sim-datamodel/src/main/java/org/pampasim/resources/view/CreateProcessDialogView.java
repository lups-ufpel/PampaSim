package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pampasim.resources.viewmodel.CreateProcessDialogViewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateProcessDialogView implements FxmlView<CreateProcessDialogViewModel>, Initializable {

    @InjectViewModel
    CreateProcessDialogViewModel viewModel;

    @FXML
    Spinner<Integer> startSpinner;
    @FXML
    Spinner<Integer> durationSpinner;
    @FXML
    Spinner<Integer> prioritySpinner;
    @FXML
    ColorPicker colorPicker;

    @FXML
    VBox memoryAccessesContainer;
    @FXML
    Button addAccessButton;
    @FXML
    CheckBox loopAccessCheckBox;
    @FXML
    public VBox memorySectionVBox;
    @FXML
    public Spinner<Integer> processSizeSpinner;
    @FXML
    public TextField fileBackedPagesField;
    @FXML
    public ButtonType okButton;

    @FXML
    private DialogPane dialogPane;

    private final List<MemoryAccessEntry> accessEntries = new ArrayList<>();

    private static class MemoryAccessEntry {
        TextField addressField;
        CheckBox modifiesCheck;
        HBox container;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        viewModel.processStartProperty().bind(startSpinner.getValueFactory().valueProperty());
        viewModel.processDurationProperty().bind(durationSpinner.getValueFactory().valueProperty());
        viewModel.processPriorityProperty().bind(prioritySpinner.getValueFactory().valueProperty());
        colorPicker.valueProperty().bindBidirectional(viewModel.colorHexProperty());

        // Memory section visibility
        memorySectionVBox.visibleProperty().bind(viewModel.memoryModulePresentProperty());
        memorySectionVBox.managedProperty().bind(viewModel.memoryModulePresentProperty());

        addAccessButton.setOnAction(event -> tryAddMemoryAccess());

        Button okButtonNode = (Button) dialogPane.lookupButton(okButton);
        okButtonNode.setOnAction(event -> {
            if (viewModel.isMemoryModulePresent()) {
                // Clear existing accesses
                viewModel.getMemoryInfo().clearAccesses();

                // Parse and store file-backed pages
                viewModel.getMemoryInfo().parseAndSetFileBackedPages(fileBackedPagesField.getText());

                // Store all memory accesses
                for (MemoryAccessEntry entry : accessEntries) {
                    try {
                        int address = Integer.parseInt(entry.addressField.getText());
                        boolean modifies = entry.modifiesCheck.isSelected();
                        viewModel.getMemoryInfo().addAccess(address, modifies);
                    } catch (NumberFormatException e) {
                        // Handle invalid input
                        showAlert("Invalid Input", "Please enter valid numbers for memory addresses");
                        return; // Prevent dialog from closing
                    }
                }

                // Store loop setting
                viewModel.getMemoryInfo().setLoopAccessList(loopAccessCheckBox.isSelected());
            }
        });

        loopAccessCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (viewModel.isMemoryModulePresent()) {
                viewModel.getMemoryInfo().setLoopAccessList(newVal);
            }
        });

        ChangeListener<Number> durationListener = (obs, oldVal, newVal) -> {
            trimAccessesToDuration(newVal.intValue());
        };
        durationSpinner.getValueFactory().valueProperty().addListener(durationListener);

        viewModel.memoryModulePresentProperty().addListener((obs, wasActive, isActive) -> {
            if (isActive) {
                viewModel.getMemoryInfo().processSizeProperty().bind(
                        processSizeSpinner.getValueFactory().valueProperty());
            }
        });
    }

    private void tryAddMemoryAccess() {
        int duration = durationSpinner.getValue();
        int currentAccesses = memoryAccessesContainer.getChildren().size();
        if (currentAccesses >= duration) {
            showAlert("Limite de acessos atingido", "Você não pode adicionar mais acessos do que a duração do processo.");
            return;
        }
        addMemoryAccessEntry();
    }

    private void addMemoryAccessEntry() {
        FontIcon trashIcon = new FontIcon("bi-trash");
        trashIcon.setIconColor(javafx.scene.paint.Color.RED);
        trashIcon.setIconSize(16);

        Button removeButton = new Button();
        removeButton.setGraphic(trashIcon);
        removeButton.setTooltip(new Tooltip("Remover acesso"));

        Label indexLabel = new Label();
        TextField addressField = new TextField();
        addressField.setPromptText("Endereço");

        CheckBox modifiesPageCheck = new CheckBox("Modifica página");

        HBox accessEntry = new HBox(10, removeButton, indexLabel, addressField, modifiesPageCheck);

        // Create and store the entry
        MemoryAccessEntry entry = new MemoryAccessEntry();
        entry.addressField = addressField;
        entry.modifiesCheck = modifiesPageCheck;
        entry.container = accessEntry;
        accessEntries.add(entry);

        removeButton.setOnAction(e -> {
            int index = memoryAccessesContainer.getChildren().indexOf(accessEntry);
            memoryAccessesContainer.getChildren().remove(accessEntry);
            accessEntries.remove(entry);
            updateAccessIndices();
        });

        memoryAccessesContainer.getChildren().add(accessEntry);
        updateAccessIndices();
    }

    private void updateAccessIndices() {
        for (int i = 0; i < memoryAccessesContainer.getChildren().size(); i++) {
            HBox entry = (HBox) memoryAccessesContainer.getChildren().get(i);
            Label indexLabel = (Label) entry.getChildren().get(1);
            indexLabel.setText("Acesso " + (i + 1) + ":");
        }
    }

    private void trimAccessesToDuration(int max) {
        while (memoryAccessesContainer.getChildren().size() > max) {
            MemoryAccessEntry entry = accessEntries.removeLast();
            memoryAccessesContainer.getChildren().remove(entry.container);
        }
        updateAccessIndices();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
