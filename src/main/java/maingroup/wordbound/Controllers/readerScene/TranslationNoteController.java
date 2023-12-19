package maingroup.wordbound.Controllers.readerScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
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
    public ComboBox<String> listViewForDecks;
    @FXML
    private CheckBox pullContext;
    private AccountClass account;
    private String bookName;
    public String lastDeck;
    private String originalWord;
    private String translatedWord;
    private String context;
    private ReaderSceneController controller;
    public void loadAccount(AccountClass account){
        this.account= account;
    }
    public void setParent(ReaderSceneController controller){
        this.controller= controller;
    }
    private Set<String> getDecks() throws IOException, ParseException {
        account.updateWordsInbound();
        Map<String, DeckWords> deckInTree= account.deckInTree.get(bookName).getKey();
        Set<String> decks =  deckInTree.keySet();
        return decks;
    }
    public void setWords(String originalWord, String translatedWord,String bookName,String context){
        originalWordLabel.setText(originalWord);
        translatedWordLabel.setText(translatedWord);
        this.bookName=bookName;
        this.context=context;
        this.originalWord= originalWord;
        this.translatedWord=translatedWord;
    }
    public void addWordInBound() throws IOException, ParseException {
        String selectedDeck=listViewForDecks.getSelectionModel().getSelectedItem();
        int idWordInBound=0;
        if(selectedDeck==null){
            selectedDeck="default";
        }
        if(pullContext.isSelected()){
            idWordInBound=account.jsonWritter.updateWordInBountJson(originalWord,translatedWord,bookName,selectedDeck,context);
        }else{
            idWordInBound=account.jsonWritter.updateWordInBountJson(originalWord,translatedWord,bookName,selectedDeck,"null");
        }
        controller.defaultDeck=selectedDeck;
        controller.createAddNote(originalWord,translatedWord,selectedDeck,idWordInBound);
        controller.closeNote();

    }
    public void loadListView() throws IOException, ParseException {
        Set<String> decks = getDecks();
        listViewForDecks.getItems().addAll(decks);

    }
    public void CreateNewDeck() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/readerScene/decksCreaterScene.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = new Stage();
        String css = Wordbound.class.getResource("styles/mainScene.css").toExternalForm();

        Scene scene = new Scene(root);

        DeckCreaterScene controllernewScene = fxmlLoader.getController();
        controllernewScene.loadAccount(account);
        controllernewScene.passBookName(bookName);
        controllernewScene.setParent(this);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
        controller.defaultDeck=lastDeck;
    }

}
