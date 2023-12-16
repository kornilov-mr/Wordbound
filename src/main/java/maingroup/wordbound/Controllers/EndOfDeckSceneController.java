package maingroup.wordbound.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import maingroup.wordbound.Controllers.MainScene.MainSceneRepeat;
import maingroup.wordbound.Wordbound;

import java.awt.*;
import java.io.IOException;

public class EndOfDeckSceneController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    Label labelDeck;
    private MainSceneRepeat contoller;
    public void setParent(MainSceneRepeat contoller){
        this.contoller=contoller;

    }
    public void loadText(String deckName){
        labelDeck.setText(deckName);
    }
    public void goToMain() throws IOException {
        contoller.loadWordInBound();
        Stage stage;
        stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }
}
