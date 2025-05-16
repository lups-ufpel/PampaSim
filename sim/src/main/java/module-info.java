module org.pampasim {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.saxsys.mvvmfx;
    requires slf4j.api;
    requires java.desktop;
    requires org.pampasim.core;
    requires guru.nidi.graphviz;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires io.github.classgraph;

    opens org.pampasim to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.viewModel to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.scopes to javafx.controls, javafx.fxml,javafx.graphics,de.saxsys.mvvmfx;
    opens org.pampasim.view to javafx.fxml, de.saxsys.mvvmfx;
    exports org.pampasim.scopes;
    exports org.pampasim.viewModel;
}