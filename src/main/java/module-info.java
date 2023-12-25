module org.simuladorso {
    requires slf4j.api;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires atlantafx.base;
    opens org.simuladorso.GUI to javafx.fxml;
    opens org.simuladorso.GUI.model to javafx.fxml;
    opens org.simuladorso.GUI.controller to javafx.fxml;
    opens org.simuladorso to javafx.fxml;
    exports org.simuladorso;
    exports org.simuladorso.Mediator;
    exports org.simuladorso.Os;
    exports org.simuladorso.GUI;
    opens org.simuladorso.Mediator to javafx.fxml;
}