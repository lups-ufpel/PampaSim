module org.pampasim {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires javafx.fxml;
    requires static lombok;
    requires de.saxsys.mvvmfx;
    requires slf4j.api;
    requires guru.nidi.graphviz;
    requires java.desktop;
    requires java.xml;
    requires java.management;
    requires org.antlr.antlr4.runtime;

    opens org.pampasim to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.viewModel to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.scopes to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.view to javafx.fxml, de.saxsys.mvvmfx;
    exports org.pampasim.scopes;
    exports org.pampasim.SimCore;
    exports org.pampasim.SimEntity;
    exports org.pampasim.SimResources;
    exports org.pampasim.viewModel;
    exports org.pampasim.Utils;
}