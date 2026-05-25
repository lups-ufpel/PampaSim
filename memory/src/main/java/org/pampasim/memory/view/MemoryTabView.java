package org.pampasim.memory.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.TextFlow;
import org.pampasim.memory.viewmodel.MemoryFrameViewModel;
import org.pampasim.memory.viewmodel.MemoryTabViewModel;
import org.pampasim.memory.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.view.ProcessView;
import org.pampasim.resources.Process;
import org.pampasim.resources.ViewListBinder;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MemoryTabView implements FxmlView<MemoryTabViewModel>, Initializable {

    @FXML public TilePane mainTilepane;
    @FXML public TilePane swapTilepane;
    @FXML public Label label_virtual_addr;
    @FXML public Label label_page_number;
    @FXML public Label label_offset;
    @FXML public Label label_page_table_number;
    @FXML public Label label_valid_bit;
    @FXML public Label label_frame_addr;
    @FXML public Label label_physical_addr;
    @FXML public Label info_title;
    @FXML public Text info_text;
    @FXML public HBox ioWaitingList;
    @FXML public HBox ioRunningList;
    @FXML public ProgressBar ioOperationProgressBar;
    @FXML public Circle info_circle;
    @FXML public ScrollPane ioOperationInfoScrollPane;
    @FXML public TextFlow ioOperationInfoTextFlow;
    @FXML public Circle ioOperationSwapInCircle;
    @FXML public Circle ioOperationSwapOutCircle;
    @FXML public Text ioOperationSwapInText;
    @FXML public Text ioOperationSwapOutText;


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
        label_frame_addr.textProperty().bind(viewModel.getFrameAddress());
        label_physical_addr.textProperty().bind(viewModel.getPhysicalAddress());
        info_title.textProperty().bind(viewModel.getInfoTitle());
        info_text.textProperty().bind(viewModel.getInfoText());

        ioOperationInfoScrollPane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double padding = 30;
            ioOperationInfoTextFlow.setMaxWidth(newVal.getWidth() - padding);
        });

        info_title.textFillProperty().bind(
                javafx.beans.binding.Bindings.createObjectBinding(() -> {
                    String title = viewModel.getInfoTitle().get();
                    if ("Page Hit".equals(title)) {
                        return Color.GREEN;
                    } else if ("Page Fault".equals(title)) {
                        return Color.RED;
                    } else {
                        return Color.BLACK;
                    }
                }, viewModel.getInfoTitle())
        );

        info_circle.fillProperty().bind(viewModel.getInfoColor());
        info_circle.visibleProperty().bind(viewModel.getInfoColor().isNotNull());
        info_circle.managedProperty().bind(viewModel.getInfoColor().isNotNull());

        ioOperationSwapInText.textProperty().bind(viewModel.getIoOperationInfoSwapInText());
        ioOperationSwapInCircle.fillProperty().bind(viewModel.getIoOperationInfoSwapInColor());

        ioOperationSwapOutText.textProperty().bind(viewModel.getIoOperationInfoSwapOutText());
        ioOperationSwapOutCircle.fillProperty().bind(viewModel.getIoOperationInfoSwapOutColor());

        ioOperationSwapInText.visibleProperty().bind(viewModel.getIoOperationInfoSwapInText().isNotEmpty());
        ioOperationSwapInText.managedProperty().bind(ioOperationSwapInText.visibleProperty());
        ioOperationSwapInCircle.visibleProperty().bind(ioOperationSwapInText.visibleProperty());
        ioOperationSwapInCircle.managedProperty().bind(ioOperationSwapInText.visibleProperty());

        ioOperationSwapOutText.visibleProperty().bind(viewModel.getIoOperationInfoSwapOutText().isNotEmpty());
        ioOperationSwapOutText.managedProperty().bind(ioOperationSwapOutText.visibleProperty());
        ioOperationSwapOutCircle.visibleProperty().bind(ioOperationSwapOutText.visibleProperty());
        ioOperationSwapOutCircle.managedProperty().bind(ioOperationSwapOutText.visibleProperty());


        updateFrameDisplays();

        ViewListBinder.bind(
                Map.of(
                        Process.State.IO_WAITING, ioWaitingList,
                        Process.State.IO_RUNNING, ioRunningList),
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
        viewModel.getObservableRamFrameList().addListener((ListChangeListener<MemoryFrameViewModel>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasUpdated() || change.wasReplaced() || change.wasRemoved() || change.wasAdded()) {
                    mainTilepane.getChildren().clear();
                    viewModel.getObservableRamFrameList().forEach(vm -> {
                        Parent view = FluentViewLoader.fxmlView(MemoryFrameView.class)
                                .viewModel(vm)
                                .load()
                                .getView();
                        mainTilepane.getChildren().add(view);
                    });
                }
            }
        });

        viewModel.getObservableSwapFrameList().addListener((ListChangeListener<MemoryFrameViewModel>) change -> {
            while (change.next()) {
                if (change.wasPermutated() || change.wasUpdated() || change.wasReplaced() || change.wasRemoved() || change.wasAdded()) {
                    swapTilepane.getChildren().clear();
                    viewModel.getObservableSwapFrameList().forEach(vm -> {
                        Parent view = FluentViewLoader.fxmlView(MemoryFrameView.class)
                                .viewModel(vm)
                                .load()
                                .getView();
                        swapTilepane.getChildren().add(view);
                    });
                }
            }
        });
    }
}
