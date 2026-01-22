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
import javafx.scene.layout.GridPane;

import org.pampasim.filesystem.core.Partition;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.viewmodel.InodeViewModel;

public class InodeView implements FxmlView<InodeViewModel> {

    @InjectViewModel
    private InodeViewModel viewModel;

    @FXML private GridPane inodeLabelGrid;

    @FXML
    public void initialize() {

      buildGrid();
        
    }
    private void buildGrid() {
      inodeLabelGrid.getChildren().clear();

      addDirectAddresses(inodeLabelGrid);

    }

    private void addDirectAddresses(GridPane grid){
      for (int r = 0; r < Inode.ADDRESSES_NUMBER; r++) {
          for (int c = 0; c < 2; c++) {
              Label label = new Label();
              //label.setMinSize(40, 40);
              //label.setAlignment(Pos.CENTER);

              if(c == 0){
                label.setText("Endereço direto " + r + ":");
              }  else{

                label.textProperty().bind(
                    viewModel.directAddressProperty(r).asString()
                );

              }
              grid.add(label, c, r);
          }
      }

    }
}
