module org.pampasim.filesystem {
    //requires slf4j.api;
    //requires java.desktop;
    requires org.pampasim.core;
    requires org.pampasim.resources;
    requires org.pampasim.events;
    //requires guru.nidi.graphviz;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires org.apache.logging.log4j;
    requires de.saxsys.mvvmfx;
    requires javafx.fxml;
    requires javafx.controls;

    exports org.pampasim.filesystem;
    exports org.pampasim.filesystem.view;
    exports org.pampasim.filesystem.viewmodel;
    exports org.pampasim.filesystem.dialog;

    opens org.pampasim.filesystem.view to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.filesystem.viewmodel to de.saxsys.mvvmfx, javafx.fxml;
}
