package maingroup.wordbound.Controllers.MainScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import maingroup.wordbound.Controllers.RepeatSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.utilities.DeckWords;
import maingroup.wordbound.utilities.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainSceneRepeat implements Initializable {
    @FXML
    private TreeView vocabularyTree;
    private Scene scene;
    private Parent root;
    private Stage stage;
    private Map<String, DeckWords> deckMap= new HashMap<String, DeckWords>();

    public String wordsInBoundPath=new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";

    public void deckSelect(MouseEvent event) throws IOException, ParseException {
        TreeItem<String> item = (TreeItem<String>)vocabularyTree.getSelectionModel().getSelectedItem();
        if(item==null){
            return;
        }
        System.out.println(item);
        if(item.isLeaf()){
            System.out.println(item);
            DeckWords selectedDeck = deckMap.get(item.getValue());
            FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/RepeatScene.fxml"));

            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            RepeatSceneController repeatScene = fxmlLoader.getController();
            repeatScene.loadDeck(selectedDeck);
            repeatScene.loadCurrWord();

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            String css = Wordbound.class.getResource("styles/wordIncountered.css").toExternalForm();
            scene = new Scene(root);
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
        }
    }
    private WordInBound CreateWordInBoundFromJson(JSONObject wordInfo){
        WordInBound word = new WordInBound((Long) wordInfo.get("realTime"),
                (Long) wordInfo.get("nextrepeat"),
                (String) wordInfo.get("deck"),
                (String) wordInfo.get("originalWord"),
                (String) wordInfo.get("time"),
                (String) wordInfo.get("wordTranslation"),
                (Long) wordInfo.get("repeatCount"));
        return word;

    }
    private void LoadWordInBound() throws IOException, ParseException {

        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        Iterator<String> booksIterator = books.keySet().iterator();

        TreeItem<String> mainRoot= new TreeItem<>("Decks");
        while(booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            TreeItem<String> bookRoot = new TreeItem<>(bookname);

            JSONObject decks= (JSONObject) books.get(bookname);
            Iterator<String> decksIterator = decks.keySet().iterator();

            while(decksIterator.hasNext()){

                String deckName = decksIterator.next();
                Vector<WordInBound> wordInBound= new Vector<>();

                JSONObject deck= (JSONObject) decks.get(deckName);
                long wordCount= (long) deck.get("wordCount");
                JSONArray words =(JSONArray) deck.get("wordsInbound");
                for(int i=0;i<wordCount;i++){
                    JSONObject wordInfo= (JSONObject) words.get(i);
                    wordInBound.add(CreateWordInBoundFromJson(wordInfo));
                }
                deckMap.put(bookname+"::"+deckName,new DeckWords(wordInBound,deckName,bookname));
                TreeItem<String> deckLeaf = new TreeItem<>(bookname+"::"+deckName);
                bookRoot.getChildren().add(deckLeaf);
            }
            mainRoot.getChildren().add(bookRoot);
        }
        vocabularyTree.setRoot(mainRoot);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            LoadWordInBound();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
