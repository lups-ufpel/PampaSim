package org.pampasim.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import org.pampasim.viewModel.SelectSchedulerDialogViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectSchedulerDialogView implements FxmlView<SelectSchedulerDialogViewModel>, Initializable {
    @InjectViewModel
    SelectSchedulerDialogViewModel viewModel;
    
    @FXML
    ChoiceBox<String> schedulerChoiceBox;
    @FXML
    CheckBox preemptionCheckBox;
    @FXML
    Spinner<Integer> quantumSpinner;

    @FXML
    public VBox memorySectionVBox;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Scheduler section
        schedulerChoiceBox.setItems(viewModel.schedulerNameProperty());
        schedulerChoiceBox.valueProperty().bindBidirectional(viewModel.selectedSchedulerProperty());
        preemptionCheckBox.selectedProperty().bindBidirectional(viewModel.preemptiveProperty());
        viewModel.quantumProperty().bind(quantumSpinner.getValueFactory().valueProperty());

        memorySectionVBox.visibleProperty().bind(viewModel.memoryModulePresentProperty());
        memorySectionVBox.managedProperty().bind(viewModel.memoryModulePresentProperty());

        // Memory section
        viewModel.pageSizeProperty().bind(pageSizeSpinner.getValueFactory().valueProperty());
        viewModel.maxPagesPerProcessProperty().bind(maxPagesSpinner.getValueFactory().valueProperty());
        viewModel.framesInRAMProperty().bind(ramFramesSpinner.getValueFactory().valueProperty());
        viewModel.framesInSwapProperty().bind(swapFramesSpinner.getValueFactory().valueProperty());
        viewModel.workingSetWindowProperty().bind(workingSetWindowSpinner.getValueFactory().valueProperty());

        pageReplacementAlgorithmChoiceBox.valueProperty().bindBidirectional(viewModel.pageSubstitutionAlgorithmProperty());

        replacementPolicyChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.globalPageSubstitutionProperty().set("Global".equals(newVal));
        });
        viewModel.globalPageSubstitutionProperty().addListener((obs, oldVal, newVal) -> {
            replacementPolicyChoiceBox.setValue(newVal ? "Global" : "Local");
        });

        pageLoadingPolicyChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.anticipatedPageLoadingProperty().set("Antecipada".equals(newVal));
        });
        viewModel.anticipatedPageLoadingProperty().addListener((obs, oldVal, newVal) -> {
            pageLoadingPolicyChoiceBox.setValue(newVal ? "Antecipada" : "Demanda");
        });

        viewModel.prePagingRangeProperty().bind(loadedPagesCountSpinner.getValueFactory().valueProperty());

        pageAllocationPolicyChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.variablePageAllocationProperty().set("Variável".equals(newVal));
        });
        viewModel.variablePageAllocationProperty().addListener((obs, oldVal, newVal) -> {
            pageAllocationPolicyChoiceBox.setValue(newVal ? "Variável" : "Fixa");
        });

        viewModel.variablePageAllocationTopThresholdProperty().bind(topThresholdSpinner.getValueFactory().valueProperty());
        viewModel.variablePageAllocationBottomThresholdProperty().bind(bottomThresholdSpinner.getValueFactory().valueProperty());

        tlbExistsCheckBox.selectedProperty().bindBidirectional(viewModel.tlbEnabledProperty());
        viewModel.tlbEntriesProperty().bind(tlbEntriesSpinner.getValueFactory().valueProperty());
    }

}
