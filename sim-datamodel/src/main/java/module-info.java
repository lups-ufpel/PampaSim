module org.pampasim.resources {
    requires org.pampasim.core;
    requires static lombok;
    requires org.antlr.antlr4.runtime;
    requires org.apache.logging.log4j;
    requires com.ibm.icu;
    requires de.saxsys.mvvmfx;
    requires javafx.fxml;
    requires org.kordamp.ikonli.javafx;
    requires javafx.controls;
    requires org.pampasim.events;

    opens org.pampasim.resources.view to de.saxsys.mvvmfx, javafx.fxml;
    opens org.pampasim.resources.viewmodel to de.saxsys.mvvmfx, javafx.fxml;

    exports org.pampasim.resources;
    exports org.pampasim.resources.memory;
    exports org.pampasim.resources.viewmodel;
    exports org.pampasim.resources.view;
    exports org.pampasim.resources.dialog;
}
