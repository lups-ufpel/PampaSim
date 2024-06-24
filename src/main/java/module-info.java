module org.simuladorso {
    requires slf4j.api;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires atlantafx.base;
    requires de.saxsys.mvvmfx;

    opens org.pampasim to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.gui to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.gui.viewmodel to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.gui.view to de.saxsys.mvvmfx, javafx.fxml;
    opens org.pampasim.gui.model to javafx.fxml, de.saxsys.mvvmfx;

    exports org.pampasim;
    exports org.pampasim.gui;
    exports org.pampasim.Mediator;
    exports org.pampasim.Os;
    exports org.pampasim.gui.view;
    exports org.pampasim.gui.model;
    exports org.pampasim.gui.viewmodel;
}
