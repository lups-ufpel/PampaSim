package org.pampasim.memory.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.memory.MemoryModule;
import org.pampasim.memory.viewmodel.ProcessMemoryInfoViewModel;
import org.pampasim.resources.dialog.UpsertProcessDialogService;
import org.pampasim.resources.memory.MemoryProcessCreationData;
import org.pampasim.resources.view.UpsertProcessDialogView;
import org.pampasim.resources.viewmodel.UpsertProcessDialogViewModel;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class MemoryInfoView implements FxmlView<ProcessMemoryInfoViewModel>, Initializable {
    private static final Logger LOGGER = LogManager.getLogger(MemoryInfoView.class);
    @InjectViewModel
    ProcessMemoryInfoViewModel viewModel;
    UpsertProcessDialogViewModel parentVM;

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
    private Label validAddressRangeLabel;
    @FXML
    private Button randomizeAccessesButton;
    @FXML
    private Button randomizeAllAccessesButton;

    @Getter
    private final IntegerProperty pageSize = new SimpleIntegerProperty(0);

    private final List<MemoryAccessEntry> accessEntries = new ArrayList<>();
    private DialogPane dialogPane;

    private static class MemoryAccessEntry {
        TextField addressField;
        CheckBox modifiesCheck;
        HBox container;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addAccessButton.setOnAction(event -> tryAddMemoryAccess());
        loopAccessCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.setLoopAccessList(newVal);
        });

        viewModel.processSizeProperty().bind(
                processSizeSpinner.getValueFactory().valueProperty());
        viewModel.processSizeProperty().addListener(
                (value, old, cur) -> {
                    updateValidAddressRange();
                });
        randomizeAccessesButton.setOnAction(e -> generateRandomAccesses());
        randomizeAllAccessesButton.setOnAction(e -> generateAllRandomAccesses());
        pageSize.set(MemoryConfig.getPageSize());
    }

    public Parent moduleInitializer(ViewTuple<UpsertProcessDialogView, UpsertProcessDialogViewModel> parentDialog) {
        this.parentVM = parentDialog.getViewModel();
        this.dialogPane = (DialogPane)parentDialog.getView();

        accessEntries.clear();
        memoryAccessesContainer.getChildren().clear();
        viewModel.clearAccesses();
        this.getPageSize().setValue(MemoryConfig.getPageSize());

        Consumer<Number> durationListener = duration -> {
            trimAccessesToDuration(duration.intValue());
        };
        parentVM.processDurationProperty().subscribe(durationListener);

        var previousModuleCreationDataOpt = this.parentVM.getModuleInfo().get(MemoryModule.class).previousValue();
        previousModuleCreationDataOpt.ifPresent(creationData -> {
            var memCD = (MemoryProcessCreationData) creationData;
            this.processSizeSpinner.getValueFactory().valueProperty().setValue((int) memCD.getPageCount());
            this.loopAccessCheckBox.selectedProperty().set(memCD.isLoopAccessList());
            var addressAccessList = memCD.getAddressAccessList().getAddress();

            ensureMemoryAccessEntryCapacity(addressAccessList.size());
            for (int i = 0; i < addressAccessList.size(); i++) {
                int address = addressAccessList.get(i).intValue();
                MemoryAccessEntry accessEntry = accessEntries.get(i);
                accessEntry.addressField.textProperty().set(String.valueOf(address));
                accessEntry.modifiesCheck.selectedProperty()
                        .setValue(memCD.getModifyPages().getPageId().contains((long) (address / pageSize.get())));
            }
        });
        this.parentVM.getModuleInfo().computeIfPresent(MemoryModule.class, (k,v) ->
        { return new UpsertProcessDialogService.ModuleTuple(v.initializer(), v.generator(), Optional.empty()); });

        Button okButtonNode = (Button) dialogPane.lookupButton(parentDialog.getCodeBehind().okButton);
        okButtonNode.addEventFilter(javafx.event.ActionEvent.ACTION, this::applyConfig);
        updateValidAddressRange();
        return this.memorySectionVBox;
    }

    private void tryAddMemoryAccess() {
        int duration = parentVM.getProcessDuration();
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

    public void updateValidAddressRange() {
        this.pageSize.set(MemoryConfig.getPageSize());
        int pageSizeBytes = this.getPageSize().get();
        int processPages = viewModel.processSizeProperty().get();
        if (pageSizeBytes <= 0 || processPages <= 0) {
            validAddressRangeLabel.setText("Endereços válidos do processo: configuração indisponível");
            return;
        }
        long maxAddress = ((long) processPages * pageSizeBytes) - 1;
        validAddressRangeLabel.setText("Endereços válidos do processo: 0 - " + maxAddress);
    }

    private void generateRandomAccesses() {
        int pageSizeBytes = this.getPageSize().get();
        int processPages = processSizeSpinner.getValue();
        int maxAddress = processPages * pageSizeBytes;

        for (MemoryAccessEntry entry : accessEntries) {
            int randomAddress = (int) (Math.random() * maxAddress);
            boolean modifies = Math.random() < 0.5;
            entry.addressField.setText(String.valueOf(randomAddress));
            entry.modifiesCheck.setSelected(modifies);
        }
    }

    private void ensureMemoryAccessEntryCapacity(int capacity) {
        var currentSize = memoryAccessesContainer.getChildren().size();
        for (int i = currentSize; i < capacity; i++) {
            tryAddMemoryAccess();
        }
    }

    private void generateAllRandomAccesses() {
        ensureMemoryAccessEntryCapacity(this.parentVM.getProcessDuration());
        generateRandomAccesses();
    }

    void applyConfig(ActionEvent event) {
        viewModel.clearAccesses();

        viewModel.parseAndSetFileBackedPages(fileBackedPagesField.getText());
        for (MemoryAccessEntry entry : accessEntries) {
            try {
                int address = Integer.parseInt(entry.addressField.getText());
                boolean modifies = entry.modifiesCheck.isSelected();
                viewModel.addAccess(address, modifies);
            } catch (NumberFormatException e) {
                event.consume();
                // Handle invalid input
                showAlert("Acesso Inválido", "Acesso " + entry + "é inválido, informe um endereço válido");
                return; // Prevent dialog from closing
            }
        }
        viewModel.setLoopAccessList(loopAccessCheckBox.isSelected());
    }
}
