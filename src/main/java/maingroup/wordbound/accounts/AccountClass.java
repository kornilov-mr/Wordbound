package maingroup.wordbound.accounts;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import maingroup.wordbound.Controllers.ReaderSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.bookreaders.Fb2Reader;
import maingroup.wordbound.utilities.animations.ResizeAnimation;
import maingroup.wordbound.utilities.books.Book;
import maingroup.wordbound.utilities.books.BookSet;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class AccountClass {
    private Scene scene;
    private Parent root;
    private Stage stage;
    public static long fontSize;
    public Vector<String> wordsIncountered= new Vector<>();
    public Map<String,DeckWords> decksInVector = new HashMap<>();
    public Map<String, Pair<Map<String,DeckWords>,DeckIndicator>> deckInTree= new HashMap<>();
    public BookSet bookset = new BookSet();
    private final String userDataPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\userGenerallData.json";
    private final String wordsInBoundPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";
    private final String wordsInCounteredPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsIncountered.json";
    private final String bookPath = new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";

    public AccountClass() throws IOException, ParseException {
        loadGenerallData();
        loadWordsInbound();
        loadWordsIncoutered();
        loadWordsBooks();

    }
    public void loadWordsIncoutered() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(wordsInCounteredPath));
        JSONObject jo = (JSONObject) obj;
        long bookCount = (long) jo.get("bookCount");
        if (bookCount == 0) {
            return;
        }
        JSONArray books = (JSONArray) jo.get("wordsIncountered");
        for (int i = 0; i < bookCount; i++) {
            wordsIncountered.add((String) books.get(i));
        }
    }
    public void loadWordsBooks() {
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(bookPath));
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
        JSONObject booksInfo = (JSONObject) jo.get("books");
        Iterator<String> booksIterator = booksInfo.keySet().iterator();


        while (booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            JSONObject data = (JSONObject) booksInfo.get(bookname);
            Book currBook = new Book(createFlowPane(data), (String) data.get("name"), (String) data.get("realBookName"), (String) data.get("author"), (Long) data.get("timeLastSeen"));
            bookset.addBook((String) data.get("name"), currBook);
        }
    }
    private FlowPane createFlowPane(JSONObject data){
            FlowPane flow = new FlowPane(Orientation.HORIZONTAL);
            flow.setColumnHalignment(HPos.LEFT); // align labels on left
            flow.setPrefHeight(170);
            flow.setPrefWidth(100);
            flow.setMaxHeight(170);
            flow.setMaxWidth(100);
            flow.setVgap(10);
            FlowPane flowText = new FlowPane(Orientation.HORIZONTAL);
            flowText.setVgap(5);
            flowText.setMaxWidth(40);
            Label nameLabel = new Label(data.get("realBookName").toString());
            nameLabel.setId("bookNameLabel");
            Label authorLabel = new Label(data.get("author").toString());
            authorLabel.setId("authorLabel");
            flowText.getChildren().add(nameLabel);
            flowText.getChildren().add(authorLabel);

            flow.getChildren().add(createImageView(data,flow));
            flow.getChildren().add(flowText);
            return flow;

    }
    private FlowPane createImageView(JSONObject data, FlowPane flow) {
        FlowPane container = new FlowPane();
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream((String) data.get("coverPath"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Image image = new Image(inputstream);

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(160);
        imageView.setFitWidth(160);
        imageView.setId((String) data.get("name"));

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(0);
        imageView.setEffect(colorAdjust);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(15.0);
        dropShadow.setOffsetY(15.0);
        dropShadow.setColor(new javafx.scene.paint.Color(0, 0, 0, 1));
        imageView.setEffect(dropShadow);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String bookName = event.getPickResult().getIntersectedNode().getId();
                try {
                    switchToReaderScene(event, bookName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ResizeAnimation rht = new ResizeAnimation(Duration.millis(200), imageView, new Pair<Double, Double>(170.0, 170.0), new Pair<Double, Double>(190.0, 190.0));

        imageView.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rht.ChangeToBig();
                FadeTransition ft = new FadeTransition(Duration.millis(200), container);
                ft.setFromValue(0);
                ft.setToValue(1);
                SequentialTransition pt = new SequentialTransition(rht, ft);

                pt.play();
            }
        });
        imageView.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                rht.ChangeToSmall();
                FadeTransition ft = new FadeTransition(Duration.millis(100), flow);
                ft.setFromValue(0);
                ft.setToValue(1);
                SequentialTransition pt = new SequentialTransition(rht, ft);
                pt.play();
            }
        });
        container.getChildren().add(imageView);
        return container;
    }
    private void switchToReaderScene(MouseEvent event,String bookName) throws IOException, ParseException {
        bookset.UpdateLastTime(bookName);
        Fb2Reader fb2Reader= null;
        try {
            fb2Reader = new Fb2Reader(bookName,false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/readerScene.fxml"));

        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ReaderSceneController readerScene = fxmlLoader.getController();
        readerScene.loadWordsIncountered(wordsIncountered);
        try {
            readerScene.startTextFlow(fb2Reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String css = Wordbound.class.getResource("styles/wordIncountered.css").toExternalForm();
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }

    public void loadGenerallData() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(userDataPath));
        JSONObject jo = (JSONObject) obj;
        this.fontSize= (long) jo.get("fontSize");
    }

    public void loadWordsInbound() throws IOException, ParseException {

        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        Iterator<String> booksIterator = books.keySet().iterator();

        while(booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            JSONObject decks= (JSONObject) books.get(bookname);
            Iterator<String> decksIterator = decks.keySet().iterator();
            Vector<DeckIndicator> indicators= new Vector<>();
            Map<String, DeckWords> decksInBook = new HashMap<>();
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
                decksInVector.put(bookname+"::"+deckName,currDeck);
                decksInBook.put(deckName,currDeck);
                indicators.add(currDeck.getIndicator());
            }
            deckInTree.put(bookname,new Pair<>(decksInBook,sumIndecators(indicators)));
        }
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
    public void updateJson() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(userDataPath));
        JSONObject jo = (JSONObject) obj;
        jo.remove("fontSize");
        jo.put("fontSize",this.fontSize);
        PrintWriter pw = new PrintWriter(userDataPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
}
