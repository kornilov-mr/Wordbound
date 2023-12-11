package maingroup.wordbound.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import maingroup.wordbound.Controllers.EndOfDeckSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.utilities.DeckWords;
import maingroup.wordbound.utilities.WordInBound;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;

public class RepeatSceneController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label firstWord;
    @FXML
    private Label secondWord;
    private DeckWords deck;
    public void loadDeck(DeckWords deck){
        this.deck=deck;
        System.out.println("test");
    }
    public void loadCurrWord() throws IOException, ParseException {
        WordInBound currWord= deck.getNextWord();
        if(!Objects.equals(currWord.originalWord, "-1")){
            firstWord.setText(currWord.originalWord);
            secondWord.setText(currWord.wordTranslation);
            secondWord.setVisible(false);
        }else{
            deck.saveInJson();
            SwitchToEndScene();
        }
    }
    private void SwitchToEndScene() throws IOException, ParseException {

        Stage stage;
        stage = (Stage) mainPane.getScene().getWindow();

        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/endOfDeckScene.fxml"));
        Parent root = fxmlLoader.load();
        EndOfDeckSceneController endScene = fxmlLoader.getController();
        endScene.loadText(deck.deckName);
        Scene scene = new Scene(root);
        stage.setTitle("Wordbound");
        stage.setScene(scene);
        stage.show();
    }
    public void showAnswer(){
        secondWord.setVisible(true);
    }
    public void getGood() throws IOException, ParseException {
        deck.getGoodWord();
        loadCurrWord();
    }
    public void setAgain() throws IOException, ParseException {
        deck.setAgainWord();
        loadCurrWord();

    }

}
