module org.pampasim.memory {
    requires slf4j.api;
    requires java.desktop;
    requires org.pampasim.core;
    requires org.pampasim.resources;
    requires org.pampasim.events;
    requires guru.nidi.graphviz;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires org.apache.logging.log4j;
    requires de.saxsys.mvvmfx;
    requires javafx.fxml;
    requires javafx.controls;

    exports org.pampasim.memory;
    exports org.pampasim.memory.entity;
    exports org.pampasim.memory.entity.algorithms;

    opens org.pampasim.memory.view to javafx.fxml, de.saxsys.mvvmfx;
    opens org.pampasim.memory.viewmodel to de.saxsys.mvvmfx, javafx.fxml;
    exports org.pampasim.memory.view;
    exports org.pampasim.memory.viewmodel;
    exports org.pampasim.memory.dialog;
}
