package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import org.kordamp.ikonli.javafx.FontIcon;

import org.pampasim.resources.viewmodel.CreateProcessDialogViewModel;
import org.pampasim.resources.filesystem.fileops.*;
import org.pampasim.resources.filesystem.config.FileSystemConfig;
import org.pampasim.resources.filesystem.AllocationScheme;
import javafx.beans.InvalidationListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

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
    private Label validAddressRangeLabel;
    @FXML
    private Button randomizeAccessesButton;

    @FXML
    VBox fileSystemOperationsContainer;
    @FXML
    public VBox fileSystemVBox;
    @FXML
    Button addOperationButton;


    @FXML
    private DialogPane dialogPane;

    private final List<MemoryAccessEntry> accessEntries = new ArrayList<>();

    private static class MemoryAccessEntry {
        TextField addressField;
        CheckBox modifiesCheck;
        HBox container;
    }

    private final List<FileSystemOperation> operationEntries = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //TODO: handle more invalid inputs
        viewModel.processStartProperty().bind(startSpinner.getValueFactory().valueProperty());
        viewModel.processDurationProperty().bind(durationSpinner.getValueFactory().valueProperty());
        viewModel.processPriorityProperty().bind(prioritySpinner.getValueFactory().valueProperty());
        colorPicker.valueProperty().bindBidirectional(viewModel.colorHexProperty());

        // Memory section visibility
        memorySectionVBox.visibleProperty().bind(viewModel.memoryModulePresentProperty());
        memorySectionVBox.managedProperty().bind(viewModel.memoryModulePresentProperty());

        addAccessButton.setOnAction(event -> tryAddMemoryAccess());

        // File section visibility
        fileSystemVBox.visibleProperty().bind(viewModel.fileSystemModulePresentProperty());
        fileSystemVBox.managedProperty().bind(viewModel.fileSystemModulePresentProperty());

        addOperationButton.setOnAction(event -> addFileSystemOperation());

        Button okButtonNode = (Button) dialogPane.lookupButton(okButton);
        okButtonNode.setOnAction(event -> {
            if (viewModel.isMemoryModulePresent()) {
                viewModel.getMemoryInfo().clearAccesses();

                viewModel.getMemoryInfo().parseAndSetFileBackedPages(fileBackedPagesField.getText());
                for (MemoryAccessEntry entry : accessEntries) {
                    try {
                        int address = Integer.parseInt(entry.addressField.getText());
                        boolean modifies = entry.modifiesCheck.isSelected();
                        viewModel.getMemoryInfo().addAccess(address, modifies);
                    } catch (NumberFormatException e) {
                        //FIXME: not working properly
                        // Handle invalid input
                        showAlert("Acesso Inválido", "Acesso " + entry + "é inválido, informe um endereço válido");
                        return; // Prevent dialog from closing
                    }
                }
                viewModel.getMemoryInfo().setLoopAccessList(loopAccessCheckBox.isSelected());
            }
                for (Node node : fileSystemOperationsContainer.getChildren()) {
                    if (!(node instanceof HBox hbox)) continue;
                
                    TextField timeField =
                            (TextField) hbox.lookup("#timeField");
                
                    ChoiceBox<String> operationBox =
                            (ChoiceBox<String>) hbox.lookup("#operationChoiceBox");
                
                    TextField pathField =
                            (TextField) hbox.lookup("#pathField");
                
                    TextField maxSizeBytesField = (TextField) hbox.lookup("#maxSizeBytesField");

                
                    String opName = operationBox.getValue();
                    int time = timeField.getText().isEmpty()
                            ? 0
                            : Integer.parseInt(timeField.getText());
                    String path = pathField.getText();

                    int maxSizeBytes = (maxSizeBytesField == null) ? -1 : Integer.parseInt(maxSizeBytesField.getText());
                
                    FileSystemOperation op =
                            createOperation(opName, time, path, maxSizeBytes);
                
                    viewModel.getFileSystemOperations().add(op);
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

        viewModel.memoryModulePresentProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                viewModel.getMemoryInfo().processSizeProperty().bind(processSizeSpinner.getValueFactory().valueProperty());
                updateValidAddressRange();
            }
        });

        viewModel.getMemoryInfo().processSizeProperty().addListener((obs, oldVal, newVal) -> updateValidAddressRange());
        viewModel.memoryModulePresentProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                viewModel.getMemoryInfo().processSizeProperty().bind(processSizeSpinner.getValueFactory().valueProperty());
                updateValidAddressRange();
            }
        });
        randomizeAccessesButton.setOnAction(e -> generateRandomAccesses());
    }

    private FileSystemOperation createOperation(String op, int time, String path, int maxSizeBytes) {
        return switch (op) {
            case "Criar Arquivo"     -> 
                switch(FileSystemConfig.getAllocationScheme()){
                  case CONTIGUOUS -> new CreateFileContiguousOp(time, path, maxSizeBytes);
                  case FAT -> new CreateFileFATOp(time, path);
                  case INODES -> new CreateFileInodesOp(time, path);
                  default -> throw new Error("unhandled switch case");

                };
            case "Apagar Arquivo"    -> new DeleteFileOp(time, path);
            case "Abrir Arquivo"     -> new OpenFileOp(time, path);
            case "Fechar Arquivo"    -> new CloseFileOp(time, path);
            case "Ler Arquivo"       -> new ReadFileOp(time, path);
            case "Escrever Arquivo"  -> new WriteFileOp(time, path);
            case "Criar Diretório"   ->
                switch(FileSystemConfig.getAllocationScheme()){
                  case CONTIGUOUS -> new CreateDirectoryContiguousOp(time, path, maxSizeBytes);
                  case FAT -> new CreateDirectoryFATOp(time, path);
                  case INODES -> new CreateDirectoryInodesOp(time, path);
                  default -> throw new Error("unhandled switch case");

                };

            case "Apagar Diretório"  -> new DeleteDirectoryOp(time, path);
            default -> throw new IllegalStateException("Unknown operation: " + op);
        };
    }

    private void addFileSystemOperation() {
        FontIcon trashIcon = new FontIcon("bi-trash");
        trashIcon.setIconColor(javafx.scene.paint.Color.RED);
        trashIcon.setIconSize(16);

        Button removeButton = new Button();
        removeButton.setGraphic(trashIcon);
        removeButton.setTooltip(new Tooltip("Remover Operação"));


        Label timeLabel = new Label("Tempo:");

        TextField timeField = new TextField();
        timeField.setPromptText("00");
        timeField.setPrefWidth(32.5);
        timeField.setId("timeField");
        
        UnaryOperator<TextFormatter.Change> filterNonNumbers = change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d*") ? change : null;
        };
        
        timeField.setTextFormatter(new TextFormatter<>(filterNonNumbers));

        ChoiceBox<String> operationChoiceBox = new ChoiceBox<>();
        operationChoiceBox.getItems().addAll(
            "Criar Arquivo",
            "Apagar Arquivo",
            "Abrir Arquivo",
            "Fechar Arquivo",
            "Ler Arquivo",
            "Escrever Arquivo",
            "Criar Diretório",
            "Apagar Diretório"
        );
        operationChoiceBox.setValue("Criar Arquivo");
        operationChoiceBox.setPrefWidth(150.0);
        operationChoiceBox.setId("operationChoiceBox");
        
        Label pathLabel = new Label("Caminho:");
        TextField pathField = new TextField();
        pathField.setId("pathField");
        
        pathField.promptTextProperty().bind(
            Bindings.createStringBinding(
                () -> {
                    String op = operationChoiceBox.getValue();
                    if (op == null) return "";
        
                    return switch (op) {
                        case "Criar Arquivo"        -> "/novo_arquivo";
                        case "Apagar Arquivo"       -> "/arquivo";
                        case "Abrir Arquivo"        -> "/arquivo";
                        case "Fechar Arquivo"       -> "/arquivo";
                        case "Ler Arquivo"          -> "/arquivo";
                        case "Escrever Arquivo"     -> "/arquivo";
                        case "Criar Diretório"      -> "/novo_dir";
                        case "Apagar Diretório"     -> "/dir";
                        default -> "";
                    };
                },
                operationChoiceBox.valueProperty()
            )
        );


        HBox entryContainer = new HBox(10, removeButton, timeLabel, timeField, operationChoiceBox, pathLabel, pathField);

        syncFileCreationInputs(entryContainer, operationChoiceBox);

        operationChoiceBox.valueProperty().addListener((InvalidationListener) obs -> {
            syncFileCreationInputs(entryContainer, operationChoiceBox);
        });

        

        // Create and store the entry
        //FileSystemOperation entry = new FileSystemOperation();
        //entry.container = entryContainer; // missing actual info for now
        //operationEntries.add(entry);

        removeButton.setOnAction(e -> {
            fileSystemOperationsContainer.getChildren().remove(entryContainer);
            //operationEntries.remove(entry);
            //updateAccessIndices();
        });

        fileSystemOperationsContainer.getChildren().add(entryContainer);
        //updateAccessIndices();

    }

    private void syncFileCreationInputs(HBox entryContainer, ChoiceBox<String> operationChoiceBox){

      String fieldId = "maxSizeBytesField";
      String labelId = "maxSizeBytesLabel";

      //delete whatever was going on before
      entryContainer.getChildren().removeIf(e -> e.getId() != null ? e.getId().equals(labelId) : false);
      entryContainer.getChildren().removeIf(e -> e.getId() != null ? e.getId().equals(fieldId) : false);


      UnaryOperator<TextFormatter.Change> filterNonNumbers = change -> {
          String newText = change.getControlNewText();
          return newText.matches("\\d*") ? change : null;
      };

      if(operationChoiceBox.getValue().equals("Criar Arquivo") || operationChoiceBox.getValue().equals("Criar Diretório")){
          if(FileSystemConfig.getAllocationScheme() == AllocationScheme.CONTIGUOUS){
              Label maxSizeBytesLabel = new Label("Tamanho Máximo (bytes):");
              maxSizeBytesLabel.setId(labelId);

              entryContainer.getChildren().add(maxSizeBytesLabel);

              TextField maxSizeBytesField = new TextField();

              maxSizeBytesField.setPromptText("0");
              maxSizeBytesField.setPrefWidth(85.0);
              maxSizeBytesField.visibleProperty();
              maxSizeBytesField.setId(fieldId);
              maxSizeBytesField.setTextFormatter(new TextFormatter<>(filterNonNumbers));

              entryContainer.getChildren().add(maxSizeBytesField);
          }

      }

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
        int pageSize = viewModel.getPageSize().get();
        int processPages = viewModel.getMemoryInfo().processSizeProperty().get();
        int maxAddress = (processPages * pageSize) - 1;
        validAddressRangeLabel.setText("Endereços válidos do processo: 0 - " + maxAddress);
    }

    private void generateRandomAccesses() {
        int pageSize = viewModel.getPageSize().get();
        int processPages = processSizeSpinner.getValue();
        int maxAddress = processPages * pageSize;

        for (MemoryAccessEntry entry : accessEntries) {
            int randomAddress = (int) (Math.random() * maxAddress);
            boolean modifies = Math.random() < 0.5;
            entry.addressField.setText(String.valueOf(randomAddress));
            entry.modifiesCheck.setSelected(modifies);
        }
    }

}
