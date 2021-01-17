module packbrew.main {
    requires javafx.fxml;
    requires javafx.controls;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;

    opens ru.falseresync.packbrew.controller to javafx.fxml;
    opens ru.falseresync.packbrew.model to com.fasterxml.jackson.databind;
    exports ru.falseresync.packbrew;
}