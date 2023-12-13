package maingroup.wordbound.Controllers.MainScene;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import maingroup.wordbound.Controllers.RepeatSceneController;
import maingroup.wordbound.Wordbound;
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
        if(item.isLeaf()){
            DeckWords selectedDeck = deckMap.get(item.getValue());
            if(selectedDeck.indicator.blue==0&&selectedDeck.indicator.red==0){
                return;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/repeatScene.fxml"));
            root = fxmlLoader.load();




            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            String css = Wordbound.class.getResource("styles/repeatScene.css").toExternalForm();
            System.out.println(css);
            scene = new Scene(root);
            RepeatSceneController repeatScene = fxmlLoader.getController();
            repeatScene.loadDeck(selectedDeck);
            repeatScene.loadCurrWord();
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
        vocabularyTree.setShowRoot(false);
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        Iterator<String> booksIterator = books.keySet().iterator();

        TreeItem<String> mainRoot= new TreeItem<>("Decks");
        while(booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            TreeItem<String> bookRoot = new TreeItem<>(bookname);
            bookRoot.setGraphic(new Text("test"));
            JSONObject decks= (JSONObject) books.get(bookname);
            Iterator<String> decksIterator = decks.keySet().iterator();
            Vector<DeckIndicator> indicators= new Vector<>();
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
                DeckWords currDeck = new DeckWords(wordInBound,deckName,bookname);
                deckMap.put(bookname+"::"+deckName,currDeck);
                TreeItem<String> deckLeaf = new TreeItem<>(bookname+"::"+deckName);

                deckLeaf.setGraphic(currDeck.createFlowPaneForDeck(currDeck.getIndicator()));
                bookRoot.getChildren().add(deckLeaf);
                indicators.add(currDeck.getIndicator());
            }
            DeckWords tempdeck = new DeckWords(new Vector<WordInBound>(),bookname,bookname);

            bookRoot.setGraphic(tempdeck.createFlowPaneForDeck(sumIndecators(indicators)));
            mainRoot.getChildren().add(bookRoot);
        }
        vocabularyTree.setRoot(mainRoot);
    }
    private DeckIndicator sumIndecators(Vector<DeckIndicator> indicators){
        int blue=0;
        int red=0;
        int green=0;
        for(int i=0;i<indicators.size();i++){
            DeckIndicator currIndicator= indicators.get(i);
            blue+=currIndicator.blue;
            red+=currIndicator.red;
            green+=currIndicator.green;
        }
        return new DeckIndicator(red,blue,green);
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
