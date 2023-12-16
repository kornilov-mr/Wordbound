package maingroup.wordbound.Controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReaderSceneController {
    @FXML
    private AnchorPane mainPane;
    public Vector<String> wordsIncountered= new Vector<>();
    private boolean isOnLabel;

    private boolean isOnNote;
    public Popup translationNote = new Popup();
    @FXML
    private Label bookNameLabel;
    @FXML
    private Label bookAuthorLabel;
    @FXML
    private TextFlow readerTextArea;
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
    public Map<String,Font> fonts = new HashMap<>();
    private Vector<Pair<String,String>> currentpage;
    private AccountClass account;

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
        this.wordsIncountered=account.wordsIncountered;
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
        account.jsonWritter.createWordInBoundJson(reader.bookName);
        nextPage();
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
            if (Objects.equals(word,"")){
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
        for(int i=0;i<wordsIncountered.size();i++){
            if (Objects.equals(wordsIncountered.get(i), word)){
                return true;
            }
        }
        return false;
    }


    private StackPane createTranslationNode(String wordToTranslate) throws IOException {
        String wordTranslation=translate("de", "en", wordToTranslate);
        StackPane stickyNotesPane = new StackPane();
        stickyNotesPane.setPrefSize(200, 200);
        stickyNotesPane.setStyle("-fx-background-color: yellow;");
        stickyNotesPane.getChildren().add(new Label(wordTranslation));
        stickyNotesPane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                isOnLabel=false;
                isOnNote=true;
            }
        });
        stickyNotesPane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(isOnLabel==false&& isOnNote==true){
                    translationNote.hide();
                    translationNote=new Popup();
                }
            }
        });
        Button addButton = new Button();
        addButton.setText("add");

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    account.jsonWritter.updateWordInBountJson(wordToTranslate,wordTranslation,reader.bookName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        stickyNotesPane.getChildren().add(addButton);

        return stickyNotesPane;
    }
    public void downFontSize() throws IOException {
        if((Long)account.generalldata.fontSize>=1){
            account.generalldata.fontSize-=1;
            fonts=changeFont((fontSizes.get(account.generalldata.fontSize))-fontSizes.get(account.generalldata.fontSize+1),fonts);
            changeFontinText();
        }
    }
    public void changeFontinText() throws IOException {
        this.currentpage=this.pageSplitter.setFontSizeInText(fonts);
        readerTextArea.getChildren().clear();
        System.out.println(this.currentpage);
        readerTextArea.getChildren().addAll(textToLabel(this.currentpage));
    }
    public void upFontSize() throws IOException {
        if(account.generalldata.fontSize<9){
            account.generalldata.fontSize+=1;
            fonts=changeFont((fontSizes.get(account.generalldata.fontSize))-fontSizes.get((int)account.generalldata.fontSize-1),fonts);
            changeFontinText();
        }
    }
    private Label createWordLabel(String word,String tag) {
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
                StackPane stickyNotesPane= null;
                try {
                    stickyNotesPane = createTranslationNode(word);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Bounds bnds = wordLabel.localToScreen(wordLabel.getLayoutBounds());
                double x = bnds.getMinX() - (stickyNotesPane.getWidth() / 2) + (wordLabel.getWidth() / 2);
                double y = bnds.getMinY() - stickyNotesPane.getHeight();
                translationNote.getContent().add(stickyNotesPane);
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
        for(int i=0;i<text.size();i++){

            String tag=text.get(i).getValue();
            String[] words= text.get(i).getKey().split(" ");
            readerTextArea.getChildren().add(new Text("   "));
            for(int j=0;j<words.length;j++){
                readerTextArea.getChildren().add(createWordLabel(words[j],tag));
                readerTextArea.getChildren().add(new Text(" "));
            }
            readerTextArea.getChildren().add(new Text("\n"));
            labels.add(new Label(""));
        }
        return labels;
    }

    public void nextPage() throws IOException, ParseException {
        account.jsonWritter.updateWordsIncoutered(addwordsIncoutered(this.currentpage));
        readerTextArea.getChildren().clear();
        this.currentpage= pageSplitter.getNextPage();
        System.out.println(this.currentpage);
        readerTextArea.getChildren().addAll(textToLabel(this.currentpage));
    }
    public void prefPage() throws IOException, ParseException {
        account.jsonWritter.updateWordsIncoutered(addwordsIncoutered(this.currentpage));
        readerTextArea.getChildren().clear();
        this.currentpage= pageSplitter.getPrefPage();
        System.out.println(this.currentpage);
        readerTextArea.getChildren().addAll(textToLabel(this.currentpage));
    }
    private static String translate(String langFrom, String langTo, String text) throws IOException {
        // INSERT YOU URL HERE
        String urlStr = "https://script.google.com/macros/s/AKfycbz6cy_Ro5y-R-omGZDjAQJSPVtJsioUxrw_0fSIEO7_NVVIB5K-pcf_ozCK54s6eRJKBA/exec" +
                "?q=" + URLEncoder.encode(text, "UTF-8") +
                "&target=" + langTo +
                "&source=" + langFrom;
        URL url = new URL(urlStr);
        StringBuilder response = new StringBuilder();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
    public void SwitchToMainMenu() throws IOException, ParseException {
        Stage stage;
        stage = (Stage) mainPane.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainScene.fxml"));
        Parent root = fxmlLoader.load();

        MainSceneController controller = fxmlLoader.getController();
        account.updateWordIncountered();
        account.updateWordsInbound();
        controller.init();
        Scene scene = new Scene(root);
        String css = Wordbound.class.getResource("styles/mainScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Wordbound");
        stage.setScene(scene);
        stage.show();
    }
}
