package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.pampasim.viewModel.SimulationSetupDialogViewModel;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class SimulationSetupDialogView implements FxmlView<SimulationSetupDialogViewModel>, Initializable {
    @InjectViewModel
    SimulationSetupDialogViewModel viewModel;
    
    @FXML
    ChoiceBox<String> schedulerChoiceBox;
    @FXML
    Spinner<Integer> quantumSpinner;

    @FXML
    private TabPane tabPane;
    @FXML
    private DialogPane dialogPane;
    @FXML
    private ButtonType okButtonType;

    // Memory
    @FXML
    private Tab memoryTab;
    @FXML
    public Spinner<Integer> pageSizeSpinner;
    @FXML
    public Spinner<Integer> maxPagesSpinner;
    @FXML
    public Spinner<Integer> ramFramesSpinner;
    @FXML
    public Spinner<Integer> swapFramesSpinner;
    @FXML
    public Spinner<Integer> workingSetWindowSpinner;
    @FXML
    public ChoiceBox<String> pageReplacementAlgorithmChoiceBox;
    @FXML
    public ChoiceBox<String> replacementPolicyChoiceBox;
    @FXML
    public ChoiceBox<String> pageLoadingPolicyChoiceBox;
    @FXML
    public Spinner<Integer> loadedPagesCountSpinner;
    @FXML
    public ChoiceBox<String> pageAllocationPolicyChoiceBox;
    @FXML
    public Spinner<Double> topThresholdSpinner;
    @FXML
    public Spinner<Double> bottomThresholdSpinner;
    @FXML
    public CheckBox tlbExistsCheckBox;
    @FXML
    public Spinner<Integer> tlbEntriesSpinner;
    @FXML
    public Label ramFramesErrorLabel;
    @FXML
    public Label swapFramesErrorLabel;
    
    // File System
    @FXML
    private Tab fileSystemTab;
    @FXML
    private ChoiceBox<String> allocationSchemeChoiceBox;


    private boolean isValid(boolean valid, Spinner<Integer> ramFramesSpinner, Label ramFramesErrorLabel) {
        int ram = ramFramesSpinner.getValue();
        if (!isPowerOfTwo(ram)) {
            ramFramesSpinner.getStyleClass().add("invalid");
            ramFramesErrorLabel.setVisible(true);
            ramFramesErrorLabel.setManaged(true);
            valid = false;
        } else {
            ramFramesSpinner.getStyleClass().removeAll("invalid");
            ramFramesErrorLabel.setVisible(false);
            ramFramesErrorLabel.setManaged(false);
        }
        return valid;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        memoryTab.disableProperty().bind(viewModel.memoryModulePresentProperty().not());
        fileSystemTab.disableProperty().bind(viewModel.fileSystemModulePresentProperty().not());

        // Scheduler section
        schedulerChoiceBox.setItems(viewModel.schedulerNameProperty());
        schedulerChoiceBox.valueProperty().bindBidirectional(viewModel.selectedSchedulerProperty());
        quantumSpinner.getValueFactory().setValue(viewModel.getQuantum());
        viewModel.quantumProperty().bind(quantumSpinner.getValueFactory().valueProperty());


        //memorySectionVBox.visibleProperty().bind(viewModel.memoryModulePresentProperty());
        //memorySectionVBox.managedProperty().bind(viewModel.memoryModulePresentProperty());

        // Memory section
        pageSizeSpinner.getValueFactory().setValue(viewModel.getPageSize());
        maxPagesSpinner.getValueFactory().setValue(viewModel.getMaxPagesPerProcess());
        ramFramesSpinner.getValueFactory().setValue(viewModel.getFramesInRAM());
        swapFramesSpinner.getValueFactory().setValue(viewModel.getFramesInSwap());
        workingSetWindowSpinner.getValueFactory().setValue(viewModel.getWorkingSetWindow());
        loadedPagesCountSpinner.getValueFactory().setValue(viewModel.getPrePagingRange());
        topThresholdSpinner.getValueFactory().setValue(viewModel.getVariablePageAllocationTopThreshold());
        bottomThresholdSpinner.getValueFactory().setValue(viewModel.getVariablePageAllocationBottomThreshold());
        tlbEntriesSpinner.getValueFactory().setValue(viewModel.getTlbEntries());

        viewModel.pageSizeProperty().bind(pageSizeSpinner.getValueFactory().valueProperty());
        viewModel.maxPagesPerProcessProperty().bind(maxPagesSpinner.getValueFactory().valueProperty());
        viewModel.framesInRAMProperty().bind(ramFramesSpinner.getValueFactory().valueProperty());
        viewModel.framesInSwapProperty().bind(swapFramesSpinner.getValueFactory().valueProperty());
        viewModel.workingSetWindowProperty().bind(workingSetWindowSpinner.getValueFactory().valueProperty());
        viewModel.prePagingRangeProperty().bind(loadedPagesCountSpinner.getValueFactory().valueProperty());
        viewModel.variablePageAllocationTopThresholdProperty().bind(topThresholdSpinner.getValueFactory().valueProperty());
        viewModel.variablePageAllocationBottomThresholdProperty().bind(bottomThresholdSpinner.getValueFactory().valueProperty());
        viewModel.tlbEntriesProperty().bind(tlbEntriesSpinner.getValueFactory().valueProperty());

        pageReplacementAlgorithmChoiceBox.setItems(viewModel.pageSubstitutionAlgorithmNameProperty());
        pageReplacementAlgorithmChoiceBox.valueProperty().bindBidirectional(viewModel.pageSubstitutionAlgorithmProperty());

        replacementPolicyChoiceBox.setItems(FXCollections.observableArrayList("Local", "Global"));
        pageLoadingPolicyChoiceBox.setItems(FXCollections.observableArrayList("Demanda", "Antecipada"));
        pageAllocationPolicyChoiceBox.setItems(FXCollections.observableArrayList("Fixa", "Variável"));

        replacementPolicyChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.globalPageSubstitutionProperty().set("Global".equals(newVal));
        });
        viewModel.globalPageSubstitutionProperty().addListener((obs, oldVal, newVal) -> {
            replacementPolicyChoiceBox.setValue(newVal ? "Global" : "Local");
        });

        pageLoadingPolicyChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.anticipatedPageLoadingProperty().set("Antecipada".equals(newVal));
            boolean enablePrePagingRange = pageLoadingPolicyChoiceBox.getValue().equals("Antecipada");
            loadedPagesCountSpinner.setDisable(!enablePrePagingRange);
        });
        viewModel.anticipatedPageLoadingProperty().addListener((obs, oldVal, newVal) -> {
            pageLoadingPolicyChoiceBox.setValue(newVal ? "Antecipada" : "Demanda");
        });

        pageAllocationPolicyChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.variablePageAllocationProperty().set("Variável".equals(newVal));
            boolean enableThresholds = "Variável".equals(newVal);
            topThresholdSpinner.setDisable(!enableThresholds);
            bottomThresholdSpinner.setDisable(!enableThresholds);
        });
        viewModel.variablePageAllocationProperty().addListener((obs, oldVal, newVal) -> {
            pageAllocationPolicyChoiceBox.setValue(newVal ? "Variável" : "Fixa");
        });

        tlbExistsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            tlbEntriesSpinner.setDisable(!newVal);
        });
        tlbExistsCheckBox.selectedProperty().bindBidirectional(viewModel.tlbEnabledProperty());

        replacementPolicyChoiceBox.setValue(viewModel.globalPageSubstitutionProperty().get() ? "Global" : "Local");
        pageLoadingPolicyChoiceBox.setValue(viewModel.anticipatedPageLoadingProperty().get() ? "Antecipada" : "Demanda");
        pageAllocationPolicyChoiceBox.setValue(viewModel.variablePageAllocationProperty().get() ? "Variável" : "Fixa");

        tlbEntriesSpinner.setDisable(!tlbExistsCheckBox.isSelected());
        boolean enableThresholds = pageAllocationPolicyChoiceBox.getValue().equals("Variável");
        topThresholdSpinner.setDisable(!enableThresholds);
        bottomThresholdSpinner.setDisable(!enableThresholds);

        boolean enablePrePagingRange = pageLoadingPolicyChoiceBox.getValue().equals("Antecipada");
        loadedPagesCountSpinner.setDisable(!enablePrePagingRange);

        // file system section
        allocationSchemeChoiceBox.setItems(viewModel.allocationSchemeNameProperty());
        allocationSchemeChoiceBox.valueProperty().bindBidirectional(viewModel.allocationSchemeProperty());





        final Button okButton = (Button) dialogPane.lookupButton(okButtonType);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {

            // Perform validation
            boolean valid = true;
            valid = isValid(valid, ramFramesSpinner, ramFramesErrorLabel);
            valid = isValid(valid, swapFramesSpinner, swapFramesErrorLabel);

            if (!valid) {
                event.consume();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Configuração Inválida");
                alert.setHeaderText("Erro nos parâmetros de memória");
                alert.setContentText("Os quadros de RAM e Swap devem ser potências de 2.");
                alert.showAndWait();

                tabPane.getSelectionModel().select(memoryTab);
            }
        });
    }

    private static boolean isPowerOfTwo(int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }


}
