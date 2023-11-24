module org.simuladorso {
    requires slf4j.api;
    requires javafx.controls;
    requires javafx.fxml;
    exports org.simuladorso;
    exports org.simuladorso.GUI to javafx.graphics;
    opens org.simuladorso.GUI.controller to javafx.fxml;
}