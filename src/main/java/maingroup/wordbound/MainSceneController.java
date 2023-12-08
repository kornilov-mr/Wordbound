package maingroup.wordbound;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import maingroup.wordbound.bookreaders.Fb2Reader;
import maingroup.wordbound.utilities.DeckWords;
import maingroup.wordbound.utilities.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainSceneController implements Initializable{
    @FXML
    private TreeView vocabularyTree;
    @FXML
    private FlowPane bookPreShowPane;
    private String[] showedBook;
    private Scene scene;
    private Parent root;
    private Stage stage;

    private String[] supportedExtensions = {"*.fb2"};
    private FileChooser.ExtensionFilter BooksEx = new FileChooser.ExtensionFilter("Books",supportedExtensions);
    private FileChooser.ExtensionFilter AllEx = new FileChooser.ExtensionFilter("All","*.*");
    private Map<String, DeckWords> deckMap= new HashMap<String, DeckWords>();
    public String jsonBookPath=new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    public String wordsInBoundPath=new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";

    public void addBookButtononKlick(ActionEvent event) throws IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(BooksEx,AllEx);
        File selectedFile= fileChooser.showOpenDialog(stage);
        if(selectedFile!=null){

            Fb2Reader fb2Reader = new Fb2Reader(selectedFile.getAbsolutePath(),true);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        LoadFlowPlane();
        try {
            LoadWordInBound();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }
    public void deckSelect(MouseEvent event) throws IOException, ParseException {
        TreeItem<String> item = (TreeItem<String>)vocabularyTree.getSelectionModel().getSelectedItem();
        if(item==null){
            return;
        }
        System.out.println(item);
        if(item.isLeaf()){
            System.out.println(item);
            DeckWords selectedDeck = deckMap.get(item.getValue());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RepeatScene.fxml"));

            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            RepeatSceneController repeatScene = fxmlLoader.getController();
            repeatScene.loadDeck(selectedDeck);
            repeatScene.loadCurrWord();

//            Scene scene = new Scene(root, 900, 600);
//            stage.setTitle("Wordbound");
//            stage.setScene(scene);
//            stage.show();

            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            String css = this.getClass().getResource("styles/wordIncountered.css").toExternalForm();
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
    private void LoadFlowPlane(){
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(jsonBookPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject jo = (JSONObject) obj;
        long bookCount = (long) jo.get("bookCount");
        if (bookCount == 0) {
            return;
        }
        JSONArray booksInfo = (JSONArray) jo.get("books");

        for (int i = 0; i < bookCount; i++) {
            JSONObject data = (JSONObject) booksInfo.get(i);
            bookPreShowPane.getChildren().add(createFlowPane(data));
        }
    }
    private FlowPane createFlowPane(JSONObject data){
        FlowPane flow = new FlowPane(Orientation.HORIZONTAL);
        flow.setColumnHalignment(HPos.LEFT); // align labels on left
        flow.setPrefWrapLength(200);
        Label nameLabel = new Label(data.get("name").toString());
        Label authorLabel = new Label(data.get("author").toString());

        String temp =(String) data.get("coverPath");
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream((String) data.get("coverPath"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Image image = new Image(inputstream);
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setId((String) data.get("name"));
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                String bookName=event.getPickResult().getIntersectedNode().getId();
                Fb2Reader fb2Reader= null;
                try {
                    fb2Reader = new Fb2Reader(bookName,false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("readerScene.fxml"));

                try {
                    root = fxmlLoader.load();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ReaderSceneController readerScene = fxmlLoader.getController();
                try {
                    readerScene.startTextFlow(fb2Reader);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                String css = this.getClass().getResource("styles/wordIncountered.css").toExternalForm();
                scene = new Scene(root);
                scene.getStylesheets().add(css);
                stage.setScene(scene);
                stage.show();

            }
        });


        flow.getChildren().add(imageView);
        flow.getChildren().add(nameLabel);
        flow.getChildren().add(authorLabel);
        return flow;
    }
}
