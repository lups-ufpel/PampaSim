module org.pampasim {
    requires slf4j.api;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires atlantafx.base;
    requires de.saxsys.mvvmfx;
    requires lombok;

    opens org.pampasim to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.gui to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.gui.viewmodel to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.gui.view to de.saxsys.mvvmfx, javafx.fxml;

    exports org.pampasim;
    exports org.pampasim.gui;
    exports org.pampasim.SimCore;
    exports org.pampasim.SimResources;
    exports org.pampasim.SimEntity;
    exports org.pampasim.gui.view;
    exports org.pampasim.gui.viewmodel;
    exports org.pampasim.gui.scopes;
    opens org.pampasim.gui.scopes to de.saxsys.mvvmfx, javafx.fxml;
}
