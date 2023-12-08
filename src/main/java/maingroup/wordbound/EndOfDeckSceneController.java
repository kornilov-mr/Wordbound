package maingroup.wordbound;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;

public class EndOfDeckSceneController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    Label labelDeck;
    public void loadText(String deckName){
        labelDeck.setText("the end of Deck: "+deckName);
    }
    public void goToMain() throws IOException {
        Stage stage;
        stage = (Stage) mainPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("mainScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Wordbound");
        stage.setScene(scene);
        stage.show();
    }
}
