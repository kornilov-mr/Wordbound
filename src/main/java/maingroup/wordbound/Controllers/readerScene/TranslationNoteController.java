package maingroup.wordbound.Controllers.readerScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Pair;
import maingroup.wordbound.Controllers.RepeatSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class TranslationNoteController {
    @FXML
    private javafx.scene.control.Label originalWordLabel;
    @FXML
    private javafx.scene.control.Label translatedWordLabel;
    @FXML
    private ListView<String> listViewForDecks;
    private AccountClass account;
    private String bookName;
    private String originalWord;
    private String translatedWord;
    public void loadAccount(AccountClass account){
        this.account= account;
    }
    private Set<String> getDecks(){
        Map<String, DeckWords> deckInTree= account.deckInTree.get(bookName).getKey();
        Set<String> decks =  deckInTree.keySet();
        return decks;
    }
    public void setWords(String originalWord, String translatedWord,String bookName){
        originalWordLabel.setText(originalWord);
        translatedWordLabel.setText(translatedWord);
        this.bookName=bookName;
        this.originalWord= originalWord;
        this.translatedWord=translatedWord;
    }
    public void addWordInBound() throws IOException, ParseException {
        account.jsonWritter.updateWordInBountJson(originalWord,translatedWord,bookName,listViewForDecks.getSelectionModel().getSelectedItem());
    }
    public void loadListView(){
        Set<String> decks = getDecks();
        listViewForDecks.getItems().addAll(decks);
    }
    public void CreateNewDeck() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/readerScene/decksCreaterScene.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        String css = Wordbound.class.getResource("styles/mainScene.css").toExternalForm();

        Scene scene = new Scene(root);

        DeckCreaterScene controller = fxmlLoader.getController();
        controller.loadAccount(account);
        controller.passBookName(bookName);
        controller.setParent(this);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

}
