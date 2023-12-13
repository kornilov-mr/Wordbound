package maingroup.wordbound.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RepeatSceneController  {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Label firstWordLabel;
    @FXML
    private Label secondWordLabel;
    @FXML
    private AnchorPane badPane;
    @FXML
    private AnchorPane goodPane;
    private DeckWords deck;
    public void loadDeck(DeckWords deck){
        this.deck=deck;
        System.out.println("test");
    }
    public void loadCurrWord() throws IOException, ParseException {
        badPane.setVisible(false);
        goodPane.setVisible(false);
        WordInBound currWord= deck.getNextWord();
        if(!Objects.equals(currWord.originalWord, "-1")){
            firstWordLabel.setText(currWord.originalWord);
            secondWordLabel.setText(currWord.wordTranslation);
            secondWordLabel.setVisible(false);
        }else{
//            deck.saveInJson();
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
        String css = Wordbound.class.getResource("styles/repeatScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
    public void showAnswer(){
        secondWordLabel.setVisible(true);
        badPane.setVisible(true);
        goodPane.setVisible(true);
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
