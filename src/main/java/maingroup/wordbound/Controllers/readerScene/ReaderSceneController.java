package maingroup.wordbound.Controllers.readerScene;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.net.URL;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Pair;
import maingroup.wordbound.Controllers.MainScene.MainSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.bookreaders.Fb2Reader;
import maingroup.wordbound.utilities.pageSplit.PageSplitter;
import maingroup.wordbound.utilities.repeats.DeckWords;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReaderSceneController {
    @FXML
    private AnchorPane mainPane;
    private boolean isOnLabel;

    private boolean isOnNote;
    private boolean isOnCombo=false;
    public Popup translationNote = new Popup();
    @FXML
    private Label bookNameLabel;
    @FXML
    private Label bookAuthorLabel;
    @FXML
    private TextFlow readerTextArea;
    @FXML
    private Label pageIndicator;
    @FXML
    private TextField pageSelector;
    @FXML
    private AnchorPane addNotePane;
    public String defaultDeck;
    public Fb2Reader reader;
    private PageSplitter pageSplitter;
    private final Map<Integer,Integer> fontSizes= Stream.of(new int[][]{
            {0, 8},
            {1, 9},
            {2, 10},
            {3, 11},
            {4, 12},
            {5, 14},
            {6, 18},
            {7, 24},
            {8, 30},
            {9, 36},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    private Map<String,Integer> defaultFontSizes = new HashMap<>();
    private final Set<Character> charsToIgnore=new HashSet<>(List.of(new Character[]{'.', ',', ';', ':'}));
    public Map<String,Font> fonts = new HashMap<>();
    private Vector<Pair<String,String>> currentpage;
    private AccountClass account;
    private Vector<AnchorPane> addNotes= new Vector<>();
    public void showAddNotes(){

        addNotePane.getChildren().clear();
        int Vgap =20;
        int y=20;
        int paneSz=100;
        for(int i=addNotes.size()-1;i>=addNotes.size()-5&&i>=0;i--){
            AnchorPane addNote= addNotes.get(i);
            addNote.setLayoutX(20);
            addNote.setLayoutY(y);
            addNotePane.getChildren().add(addNote);
            y+=paneSz;
        }
    }
    public void createAddNote(String firstWord,String secondWord,String deckName,int idWordInBound) throws IOException {
        AnchorPane addNote = loadAddNote(firstWord,secondWord,deckName,idWordInBound);
        addNotes.add(addNote);
        showAddNotes();
    }
    public AnchorPane loadAddNote(String firstWord,String secondWord,String deckName,int idWordInBound) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/readerScene/addNote.fxml"));
        AnchorPane addNote = fxmlLoader.load();
        AddNoteController controller = fxmlLoader.getController();
        controller.loadAccount(account);
        controller.loadWords(firstWord,secondWord);
        controller.loadCardData(reader.bookName,deckName,idWordInBound);
        return addNote;
    }
    public Map<String,Font> changeFont(int n,Map<String,Font> fonts) {
        Map<String, Font> newFonts= new HashMap<>();

        for (String tag : fonts.keySet()) {
            Font curr_font = fonts.get(tag);
            int size = curr_font.getSize();
            newFonts.put(tag, new Font("Tahoma", Font.BOLD, size + n));
        }
        return newFonts;
    }
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    public void startTextFlow(Fb2Reader reader) throws IOException, ParseException {
        this.reader=reader;
        bookNameLabel.setText(reader.realBookName);
        bookAuthorLabel.setText(reader.autor);

        Font fontS = new Font("Tahoma", Font.BOLD, 4);
        Font fontT = new Font("Tahoma", Font.BOLD, 4);
        Font fonth = new Font("Tahoma", Font.BOLD, 0);
        Font fontp = new Font("Tahoma", Font.BOLD, 0);
        fonts.put("s",fontS);
        defaultFontSizes.put("s",4);
        fonts.put("t",fontT);
        defaultFontSizes.put("t",4);
        fonts.put("h",fonth);
        defaultFontSizes.put("h",0);
        fonts.put("p",fontp);
        defaultFontSizes.put("p",0);

        fonts=changeFont(fontSizes.get((int)account.generalldata.fontSize),fonts);
        this.pageSplitter= new PageSplitter(reader,fonts);
        this.defaultDeck= reader.lastDeck;
//        nextPage();
        pageIndicator.setText(String.valueOf(pageSplitter.maxPage));
    }




    private Vector<String> addwordsIncoutered(Vector<Pair<String,String>> text){
        if(text== null){
            return new Vector<>();
        }
        Vector<String> clearWords= new Vector<>();
        Vector<String> words = new Vector<>();
        for(int i=0;i<text.size();i++){
            words.addAll(List.of(text.get(i).getKey().split(" ")));
        }
        for(int i=0;i<words.size();i++){
            String word =words.get(i);
            word=word.replaceAll(" ","");
            word=word.replaceAll("\n","");
            word=word.toLowerCase();
            word = word.replaceAll("[^\\sa-zA-Z0-9]", "");
            if (!Objects.equals(word,"")){
                clearWords.add(word);
            }
        }
        return clearWords;
    }
    private boolean ifIncountered(String word){
        word=word.replaceAll(" ","");
        word=word.replaceAll("\n","");
        word=word.toLowerCase();
        word = word.replaceAll("[^\\sa-zA-Z0-9]", "");
        if(Objects.equals(word,"")){
            return true;
        }
        for(int i=0;i<account.wordsIncountered.size();i++){
            if (Objects.equals(account.wordsIncountered.get(i), word)){
                return true;
            }
        }
        return false;
    }
    private Set<String> getDecks(){
        Map<String, DeckWords> deckInTree= account.deckInTree.get(reader.bookName);
        Set<String> decks =  deckInTree.keySet();
        return decks;
    }

    private AnchorPane createTranslationNode(String wordToTranslate,String context) throws IOException, InterruptedException, ParseException {
//        String wordTranslation=translate("de", "en", wordToTranslate);
        String wordTranslation="test";
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/readerScene/translationNode.fxml"));
        AnchorPane tranlationPane = fxmlLoader.load();

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(15.0);
        dropShadow.setOffsetY(15.0);
        dropShadow.setColor(new javafx.scene.paint.Color(0.2, 0.2, 0.2, 1));
        tranlationPane.setEffect(dropShadow);
        tranlationPane.setId("translationNote");
        String css = Wordbound.class.getResource("styles/translationNote.css").toExternalForm();
        tranlationPane.getStylesheets().add(css);
        TranslationNoteController controller= fxmlLoader.getController();
        controller.loadAccount(account);
        controller.setParent(this);
        controller.setWords(wordToTranslate,wordTranslation, reader.bookName,context);
        controller.listViewForDecks.getItems().addAll(getDecks());
        controller.listViewForDecks.getSelectionModel().select(defaultDeck);
        controller.listViewForDecks.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isOnCombo=true;
                isOnLabel=false;
                isOnNote=true;
            }
        });
        controller.listViewForDecks.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isOnCombo=false;
            }
        });
        controller.listViewForDecks.setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                }
            };
            cell.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    isOnCombo=true;
                    isOnLabel=false;
                    isOnNote=true;
                }
            });
            cell.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(isOnLabel==false&& isOnNote==false){
                        isOnCombo=false;
                        translationNote.hide();
                        translationNote=new Popup();
                    }
                }
            });
            return cell ;
        });

        tranlationPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isOnLabel=false;
                isOnNote=true;
            }
        });
        tranlationPane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(isOnLabel==false&& isOnNote==true&& isOnCombo==false){
                    translationNote.hide();
                    translationNote=new Popup();
                }
            }
        });


        return tranlationPane;
    }
    public void downFontSize() throws IOException {
        if((Long)account.generalldata.fontSize>=1){
            account.generalldata.fontSize-=1;
            fonts=changeFont((fontSizes.get((int)account.generalldata.fontSize))-fontSizes.get((int)account.generalldata.fontSize+1),fonts);
            changeFontinText();
        }
    }
    public void changeFontinText() throws IOException {
        this.currentpage=this.pageSplitter.setFontSizeInText(fonts);
        readerTextArea.getChildren().clear();
        System.out.println(this.currentpage);
        readerTextArea.getChildren().addAll(textToLabel(this.currentpage));
        pageIndicator.setText(String.valueOf(pageSplitter.maxPage));

    }
    public void upFontSize() throws IOException {
        if(account.generalldata.fontSize<9){
            account.generalldata.fontSize+=1;
            fonts=changeFont((fontSizes.get((int)account.generalldata.fontSize))-fontSizes.get((int)account.generalldata.fontSize-1),fonts);
            changeFontinText();
        }
    }
    private Label createWordLabel(String word,String tag,String context) {
        Label wordLabel = new Label(word);
        Font currFont= fonts.get(tag);
        wordLabel.setStyle("-fx-font-size:"+ currFont.getSize() +"px;");
        wordLabel.setAccessibleText(word);
        if(!ifIncountered(word)){
            wordLabel.setId("incountered");
        }

        wordLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                isOnNote=false;
                isOnLabel=true;
                AnchorPane translationPane= null;
                try {
                    translationPane = createTranslationNode(word,context);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Bounds bnds = wordLabel.localToScreen(wordLabel.getLayoutBounds());
                double x = bnds.getMinX() - (300) + (wordLabel.getWidth() / 2);
                double y = bnds.getMinY() - 120;
                translationNote.getContent().add(translationPane);
                translationNote.show(wordLabel, x, y);
            }
        });
        wordLabel.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(isOnLabel==true&& isOnNote==false){
                    translationNote.hide();
                    translationNote=new Popup();
                }
            }
        });
        return wordLabel;
    }
    private Vector<Label> textToLabel(Vector<Pair<String,String>> text){
        Vector<Label> labels= new Vector<>();
        readerTextArea.getChildren().add(new Text("\n"));
        int wordsCount=0;
        Vector<Pair<String, Integer>> context= new Vector<>();
        String content="";
        for(int i=0;i<text.size();i++){
            String[] words= text.get(i).getKey().split(" ");
            for(int j=0;j<words.length;j++){
                wordsCount+=1;
                String currWord= words[j];
                content+=currWord+" ";
                if(!Objects.equals(currWord,"")){
                    if(currWord.charAt(currWord.length()-1)=='.'||currWord.charAt(currWord.length()-1)==':'||Objects.equals(currWord,"/n")) {
                        context.add(new Pair<>(content,wordsCount));
                        content="";
                    }
                }
            }
        }
        context.add(new Pair<>(content,wordsCount));
        int contextCount=0;
        content=context.get(0).getKey();
        wordsCount=0;
        for(int i=0;i<text.size();i++){

            String tag=text.get(i).getValue();
            String[] words= text.get(i).getKey().split(" ");


            readerTextArea.getChildren().add(new Text("   "));
            for(int j=0;j<words.length;j++){
                wordsCount++;
                if(wordsCount>context.get(contextCount).getValue()){
                    contextCount+=1;
                    content=context.get(contextCount).getKey();
                }
                if(!Objects.equals(words[j],"/n")&&!Objects.equals(words[j],"")) {
                    if(charsToIgnore.contains(words[j].charAt(words[j].length()-1))){
                        readerTextArea.getChildren().add(createWordLabel(words[j].substring(0,words[j].length()-1), tag, content));
                        readerTextArea.getChildren().add(new Text(String.valueOf(words[j].charAt(words[j].length()-1))));
                        readerTextArea.getChildren().add(new Text(" "));
                    }else{
                        readerTextArea.getChildren().add(createWordLabel(words[j], tag, content));
                        readerTextArea.getChildren().add(new Text(" "));
                    }
                }
            }
            readerTextArea.getChildren().add(new Text("\n"));
            labels.add(new Label(""));
        }
        return labels;
    }

    public void nextPage() throws IOException, ParseException {
        account.dataHandler.updateWordsIncoutered(addwordsIncoutered(this.currentpage));
        readerTextArea.getChildren().clear();
        this.currentpage= pageSplitter.getNextPage();
        System.out.println(this.currentpage);
        readerTextArea.getChildren().addAll(textToLabel(this.currentpage));
        pageSelector.setText(String.valueOf(pageSplitter.pageCount));

    }
    public void prefPage() throws IOException, ParseException {
        account.dataHandler.updateWordsIncoutered(addwordsIncoutered(this.currentpage));
        readerTextArea.getChildren().clear();
        this.currentpage= pageSplitter.getPrefPage();
        System.out.println(this.currentpage);
        readerTextArea.getChildren().addAll(textToLabel(this.currentpage));
        pageSelector.setText(String.valueOf(pageSplitter.pageCount));

    }
    private static String translate(String langFrom, String langTo, String text) throws IOException, InterruptedException, ParseException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                .header("content-type", "application/x-www-form-urlencoded")
                .header("Accept-Encoding", "application/gzip")
                .header("X-RapidAPI-Key", "3a502c1024mshb4230da13f860b8p1f3b0ejsn6efb01fc592c")
                .header("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString("q="+text+"&target="+langTo+"&source="+langFrom))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JSONParser parser = new JSONParser();
        JSONObject responseJson = (JSONObject) parser.parse(response.body());
        JSONObject data = (JSONObject) responseJson.get("data");
        JSONArray translations= (JSONArray) data.get("translations");
        JSONObject firstTranslation= (JSONObject) translations.get(0);
        return (String) firstTranslation.get("translatedText");
    }
    public void SwitchToMainMenu() throws IOException, ParseException {
        Stage stage;
        stage = (Stage) mainPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainScene.fxml"));
        Parent root = fxmlLoader.load();

        MainSceneController controller = fxmlLoader.getController();

        account.jsonWritter.updateBookData(reader.bookName,pageSplitter.pageCount,defaultDeck);

        controller.init();
        Scene scene = new   Scene(root);
        String css = Wordbound.class.getResource("styles/mainScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Wordbound");
        stage.setScene(scene);
        stage.show();
    }
    public void startFromPageN(long n){
        readerTextArea.getChildren().clear();
        this.currentpage= pageSplitter.getPageByN(n);
        System.out.println(this.currentpage);
        readerTextArea.getChildren().addAll(textToLabel(this.currentpage));
        pageSelector.setText(String.valueOf(pageSplitter.pageCount));
        pageSelector.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                if (!newValue.matches("\\d*")) {
                    pageSelector.setText(newValue.replaceAll("[^\\d]", ""));
                }
            }
        });
    }
    public void closeNote(){
        translationNote.hide();
        translationNote=new Popup();
        isOnNote=false;
        isOnLabel=false;
    }
    public void selectPage(){
        int neededPage = (int) Long.parseLong(String.valueOf(pageSelector.getText()));
        startFromPageN(neededPage);

    }
}
