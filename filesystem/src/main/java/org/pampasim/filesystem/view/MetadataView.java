package org.pampasim.filesystem.view;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.control.ButtonType;
import javafx.scene.Cursor;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.beans.property.ReadOnlyObjectWrapper;

import org.pampasim.filesystem.core.Partition;
import org.pampasim.filesystem.viewmodel.MetadataViewModel;

public class MetadataView implements FxmlView<MetadataViewModel> {

    @FXML private Label currentSizeLabel;
    @FXML private Label creationTimeLabel;
    @FXML private Label lastAccessLabel;
    @FXML private Label lastModifiedLabel;
    @FXML private Label isDirectoryLabel;

    @InjectViewModel
    private MetadataViewModel viewModel;

    @FXML
    public void initialize() {

        currentSizeLabel.textProperty().bind(
            viewModel.currentSizeProperty().asString()
        );

        creationTimeLabel.textProperty().bind(
            Bindings.createStringBinding(
                () -> viewModel.getCreationTime().toString(),
                viewModel.creationTimeProperty()
            )
        );

        lastAccessLabel.textProperty().bind(
            Bindings.createStringBinding(
                () -> viewModel.getLastAccessTime().toString(),
                viewModel.lastAccessTimeProperty()
            )
        );

        lastModifiedLabel.textProperty().bind(
            Bindings.createStringBinding(
                () -> viewModel.getLastModifiedTime().toString(),
                viewModel.lastModifiedTimeProperty()
            )
        );

        isDirectoryLabel.textProperty().bind(
            Bindings.when(viewModel.directoryProperty())
                    .then("Sim")
                    .otherwise("Não")
        );
    }
}
