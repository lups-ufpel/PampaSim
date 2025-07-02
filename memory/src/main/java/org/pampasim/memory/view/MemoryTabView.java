package org.pampasim.memory.view;

import de.saxsys.mvvmfx.FxmlView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.TilePane;
import lombok.Setter;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.viewmodel.MemoryFrameViewModel;
import org.pampasim.memory.viewmodel.MemoryTabViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class MemoryTabView implements FxmlView<MemoryTabViewModel>, Initializable {
    @FXML
    public TilePane mainTilepane;
    public TilePane swapTilepane;
    private ObservableList<MemoryFrameViewModel> mainFrameList = FXCollections.observableArrayList();
    private ObservableList<MemoryFrameViewModel> swapFrameList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
