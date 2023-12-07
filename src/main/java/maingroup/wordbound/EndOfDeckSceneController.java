package maingroup.wordbound;

import javafx.fxml.FXML;

import java.awt.*;

public class EndOfDeckSceneController {
    @FXML
    private Label labelDeck;
    public void loadText(String deckName){
        labelDeck.setText("the end of Deck: "+deckName);
    }
    public void goToMain(){

    }
}
