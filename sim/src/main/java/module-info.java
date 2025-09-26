module org.pampasim {
    requires javafx.fxml;
    requires de.saxsys.mvvmfx;
    requires slf4j.api;
    requires java.desktop;
    requires org.pampasim.core;
    requires org.pampasim.resources;
    requires guru.nidi.graphviz;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires io.github.classgraph;
    requires org.apache.logging.log4j;
    requires antlr4;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires javafx.controls;
    requires org.kordamp.ikonli.bootstrapicons;
    requires org.kordamp.ikonli.feather;
    requires jakarta.xml.bind;

    requires org.pampasim.events;
    requires org.pampasim.memory;
    requires eu.dariolucia.jfx.timeline;

    opens org.pampasim to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.viewModel to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.view to javafx.fxml, de.saxsys.mvvmfx;
    exports org.pampasim.viewModel to de.saxsys.mvvmfx;
    opens org.pampasim.dialog to de.saxsys.mvvmfx, javafx.controls, javafx.fxml, javafx.graphics;
}