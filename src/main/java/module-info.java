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
    requires java.net.http;
    requires javafx.web;
    opens maingroup.wordbound to javafx.fxml;
    exports maingroup.wordbound;
    exports maingroup.wordbound.Controllers;
    opens maingroup.wordbound.Controllers to javafx.fxml;
    exports maingroup.wordbound.Controllers.MainScene;
    opens maingroup.wordbound.Controllers.MainScene to javafx.fxml;
    exports maingroup.wordbound.Controllers.readerScene;
    opens maingroup.wordbound.Controllers.readerScene to javafx.fxml;
    opens maingroup.wordbound.Controllers.statisticsScene to javafx.fxml;
}