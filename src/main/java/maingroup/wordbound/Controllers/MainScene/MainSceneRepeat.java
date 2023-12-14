package maingroup.wordbound.Controllers.MainScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Pair;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import maingroup.wordbound.Controllers.RepeatSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainSceneRepeat{
    @FXML
    private TreeView vocabularyTree;
    private AccountClass account;
    private Scene scene;
    private Parent root;
    private Stage stage;
    private Map<String, DeckWords> deckMap= new HashMap<String, DeckWords>();

    public String wordsInBoundPath=new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    public void deckSelect(MouseEvent event) throws IOException, ParseException {
        TreeItem<DeckWords> item = (TreeItem<DeckWords>)vocabularyTree.getSelectionModel().getSelectedItem();
        if(item==null){
            return;
        }
        if(item.isLeaf()){
            DeckWords selectedDeck= item.getValue();
            if(selectedDeck.indicator.blue==0&&selectedDeck.indicator.red==0){
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/repeatScene.fxml"));
            root = fxmlLoader.load();

            stage = new Stage();
            String css = Wordbound.class.getResource("styles/repeatScene.css").toExternalForm();

            scene = new Scene(root);

            RepeatSceneController repeatScene = fxmlLoader.getController();
            repeatScene.loadDeck(selectedDeck);
            repeatScene.loadCurrWord();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
        }
    }

    public void loadWordInBound(){
        vocabularyTree.setShowRoot(false);
        TreeItem<DeckWords> mainRoot= new TreeItem<>(new DeckWords(new Vector<>(),"null","null"));
        Iterator<String> booksIterator = account.deckInTree.keySet().iterator();
        while(booksIterator.hasNext()){
            String bookName= booksIterator.next();

            TreeItem<DeckWords> bookNode= new TreeItem<>(new DeckWords(new Vector<>(),bookName,bookName));

            Pair<Map<String,DeckWords>,DeckIndicator> decksData = account.deckInTree.get(bookName);
            Map<String, DeckWords> decks= decksData.getKey();

            Iterator<String> decksIterator = decks.keySet().iterator();

            while(decksIterator.hasNext()){
                String deckName=decksIterator.next();
                DeckWords curr_deck= decks.get(deckName);
                TreeItem<DeckWords> deckNode = new TreeItem<>(curr_deck);
                deckNode.setGraphic(curr_deck.createFlowPaneForDeck(curr_deck.getIndicator()));
                bookNode.getChildren().add(deckNode);
            }
            bookNode.setGraphic(bookNode.getValue().createFlowPaneForDeck(decksData.getValue()));
            mainRoot.getChildren().add(bookNode);
        }
        vocabularyTree.setRoot(mainRoot);
    }
}
