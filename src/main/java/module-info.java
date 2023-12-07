module maingroup.wordbound {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires jdk.jpackage;
    requires juniversalchardet;
    requires org.apache.commons.io;
    requires java.desktop;
    requires json;
    requires json.simple;
    requires javafx.web;
    opens maingroup.wordbound to javafx.fxml;
    exports maingroup.wordbound;
}