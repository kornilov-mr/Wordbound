package maingroup.wordbound;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import maingroup.wordbound.utilities.DeckWords;
import maingroup.wordbound.utilities.WordInBound;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;

public class RepeatSceneController {
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
            SwitchToEndScene();
        }
    }
    private void SwitchToEndScene() throws IOException, ParseException {
//        deck.saveInJson();
//        FXMLLoader loader = new FXMLLoader(getClass()
//                .getResource("endOfDeckScene.fxml"));
//        Parent root;
//        try
//        {
//            root = (Parent)loader.load();
//            Scene newScene = new Scene(root, 300, 400);
//
//            Stage currentStage = (Stage)root.getScene().getWindow();
//            currentStage.setScene(newScene);
//            currentStage.show();
//
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }
        return;
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
