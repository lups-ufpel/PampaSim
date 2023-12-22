module org.simuladorso {
    requires slf4j.api;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    opens org.simuladorso.GUI to javafx.fxml;
    opens org.simuladorso.GUI.model to javafx.fxml;
    opens org.simuladorso.GUI.controller to javafx.fxml;
    exports org.simuladorso;
    exports org.simuladorso.Mediator;
    opens org.simuladorso.Mediator to javafx.fxml;
}