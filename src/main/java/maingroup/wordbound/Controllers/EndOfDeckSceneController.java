package maingroup.wordbound.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import maingroup.wordbound.Wordbound;

import java.awt.*;
import java.io.IOException;

public class EndOfDeckSceneController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    Label labelDeck;
    public void loadText(String deckName){
        labelDeck.setText(deckName);
    }
    public void goToMain() throws IOException {
        Stage stage;
        stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}
