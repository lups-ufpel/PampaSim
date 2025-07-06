package org.pampasim.memory.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import lombok.Setter;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.viewmodel.MemoryFrameViewModel;
import org.pampasim.memory.viewmodel.MemoryTabViewModel;
import org.pampasim.resources.Process;
import org.pampasim.resources.ViewListBinder;
import org.pampasim.resources.view.ProcessView;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MemoryTabView implements FxmlView<MemoryTabViewModel>, Initializable {
    @FXML
    public TilePane mainTilepane;
    @FXML
    public TilePane swapTilepane;
    @FXML
    public Label label_virtual_addr;
    @FXML
    public Label label_page_number;
    @FXML
    public Label label_offset;
    @FXML
    public Label label_page_table_number;
    @FXML
    public Label label_valid_bit;
    @FXML
    public Label label_frame_number;
    @FXML
    public Label label_physical_addr;
    @FXML
    public Label info_title;
    @FXML
    public Text info_text;
    @FXML
    public HBox ioWaitingList;
    @FXML
    public HBox ioRunningList;
    @FXML
    public ProgressBar ioOperationProgressBar;

    @InjectViewModel
    private MemoryTabViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Bind all label properties
        label_virtual_addr.textProperty().bind(viewModel.getVirtualAddress());
        label_page_number.textProperty().bind(viewModel.getPageNumber());
        label_offset.textProperty().bind(viewModel.getOffset());
        label_page_table_number.textProperty().bind(viewModel.getPageTableNumber());
        label_valid_bit.textProperty().bind(viewModel.getValidBit());
        label_frame_number.textProperty().bind(viewModel.getFrameNumber());
        label_physical_addr.textProperty().bind(viewModel.getPhysicalAddress());
        info_title.textProperty().bind(viewModel.getInfoTitle());
        info_text.textProperty().bind(viewModel.getInfoText());

        updateFrameDisplays();

        setupFrameListListeners();

        ViewListBinder.bind(
                Map.of(
                        org.pampasim.resources.Process.State.IO_WAITING, ioWaitingList,
                        org.pampasim.resources.Process.State.IO_RUNNING, ioRunningList),
                ioOperationProgressBar,
                processViewModel -> processViewModel
                        .getModuleInfoViewModel(MemoryInfoViewModel.class)
                        .getIoOperationProgress(),
                viewModel.getObservableProcessList(),
                ViewListBinder.mvvmfxFxmlFactory(ProcessView.class),
                Process.State.IO_RUNNING
        );

    }

    private void updateFrameDisplays() {
        // Clear and populate RAM frames
        mainTilepane.getChildren().clear();
        viewModel.getObservableRamFrameList().forEach(vm -> {
            Parent view = FluentViewLoader.fxmlView(MemoryFrameView.class)
                    .viewModel(vm)
                    .load()
                    .getView();
            mainTilepane.getChildren().add(view);
        });

        // Clear and populate Swap frames
        swapTilepane.getChildren().clear();
        viewModel.getObservableSwapFrameList().forEach(vm -> {
            Parent view = FluentViewLoader.fxmlView(MemoryFrameView.class)
                    .viewModel(vm)
                    .load()
                    .getView();
            swapTilepane.getChildren().add(view);
        });
    }

    private void setupFrameListListeners() {
        // More efficient listener that only updates changed frames
        viewModel.getObservableRamFrameList().addListener((ListChangeListener<MemoryFrameViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(vm -> {
                        Parent view = FluentViewLoader.fxmlView(MemoryFrameView.class)
                                .viewModel(vm)
                                .load()
                                .getView();
                        mainTilepane.getChildren().add(view);
                    });
                }
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(vm ->
                            mainTilepane.getChildren().removeIf(node ->
                                    node.getUserData() != null &&
                                            node.getUserData().equals(vm)
                            )
                    );
                }
            }
        });

        viewModel.getObservableSwapFrameList().addListener((ListChangeListener<MemoryFrameViewModel>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    change.getAddedSubList().forEach(vm -> {
                        Parent view = FluentViewLoader.fxmlView(MemoryFrameView.class)
                                .viewModel(vm)
                                .load()
                                .getView();
                        swapTilepane.getChildren().add(view);
                    });
                }
                if (change.wasRemoved()) {
                    change.getRemoved().forEach(vm ->
                            swapTilepane.getChildren().removeIf(node ->
                                    node.getUserData() != null &&
                                            node.getUserData().equals(vm)
                            )
                    );
                }
            }
        });
    }
}
