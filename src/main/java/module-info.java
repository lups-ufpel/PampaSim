module org.simuladorso {
    requires slf4j.api;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires atlantafx.base;
    opens org.pampasim.GUI.model to javafx.fxml;
    opens org.pampasim.GUI.controllers to javafx.fxml;
    opens org.pampasim to javafx.fxml;
    exports org.pampasim;
    exports org.pampasim.Mediator;
    exports org.pampasim.Os;
    opens org.pampasim.Mediator to javafx.fxml;
    exports org.pampasim.GUI.controllers;
    exports org.pampasim.GUI.model;
}