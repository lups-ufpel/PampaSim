package org.pampasim.view;

// Lifted from https://stackoverflow.com/a/69210913
import javafx.application.Platform;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LockedTableCell<T, S> extends TableCell<T, S> {
    private static final Logger LOGGER = LogManager.getLogger(LockedTableCell.class);
    public LockedTableCell() {
        super();
        Platform.runLater(() -> {
            try {
                ScrollBar scrollBar = (ScrollBar) getTableView()
                        .queryAccessibleAttribute(AccessibleAttribute.HORIZONTAL_SCROLLBAR);
                // set fx:id of TableColumn and get region of column header by #id
                Region headerNode = (Region) getTableView().lookup("#" + getTableColumn().getId());
                scrollBar.valueProperty().addListener((ob, o, n) -> {
                    double doubleValue = n.doubleValue();

                    // move header and cell with translateX & bring it front
                    headerNode.setTranslateX(doubleValue);
                    headerNode.toFront();

                    this.setTranslateX(doubleValue);
                    this.toFront();

                });
            } catch (Exception e) {
                LOGGER.error(e);
            }
        });
    }
}