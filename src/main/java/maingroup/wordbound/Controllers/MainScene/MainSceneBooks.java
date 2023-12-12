package maingroup.wordbound.Controllers.MainScene;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import maingroup.wordbound.Controllers.ReaderSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.bookreaders.Fb2Reader;
import maingroup.wordbound.utilities.animations.ResizeAnimation;
import maingroup.wordbound.utilities.books.Book;
import maingroup.wordbound.utilities.books.BookSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.text.FlowView;
import java.awt.image.BufferedImage;
import javafx.scene.control.ScrollPane;
import java.io.*;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

public class MainSceneBooks implements Initializable {
    private Scene scene;
    private Parent root;
    private Stage stage;
    private final String[] supportedExtensions = {"*.fb2"};
    private BookSet bookset = new BookSet();
    private FileChooser.ExtensionFilter BooksEx = new FileChooser.ExtensionFilter("Books",supportedExtensions);
    private final FileChooser.ExtensionFilter AllEx = new FileChooser.ExtensionFilter("All","*.*");
    public String jsonBookPath=new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    @FXML
    private GridPane bookPreShowPane;
    @FXML
    private ScrollPane scrollPaneBooks;

    public void addBookButtononKlick() throws IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(BooksEx,AllEx);
        File selectedFile= fileChooser.showOpenDialog(stage);
        if(selectedFile!=null){
            Fb2Reader fb2Reader = new Fb2Reader(selectedFile.getAbsolutePath(),true);
            try {
                bookPreShowPane.getChildren().clear();
                LoadFlowPlane();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scrollPaneBooks.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        try {
            LoadFlowPlane();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void LoadFlowPlane() throws IOException {
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
        JSONObject booksInfo = (JSONObject) jo.get("books");
        Iterator<String> booksIterator = booksInfo.keySet().iterator();


        while(booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            JSONObject data = (JSONObject) booksInfo.get(bookname);
            Book currBook= new Book(createFlowPane(data),(String)data.get("name"),(String)data.get("realBookName"),(Long)data.get("timeLastSeen"));
            bookset.addBook((String)data.get("name"),currBook);
        }
        Vector<Pair<Long,Book>> bookOrder= bookset.GetSortedBytime();
        displayBookSet(bookOrder);

    }
    private void displayBookSet(Vector<Pair<Long,Book>> bookOrder){
        boolean scrollNeeded = false;
        for(int i=0;i<bookOrder.size();i++){
            int j=i/3;
            bookPreShowPane.add(bookOrder.get(i).getValue().content,i%3,j);
            if(i>=3*bookPreShowPane.getRowCount()){
                scrollNeeded=true;
                bookPreShowPane.setMaxHeight(bookPreShowPane.getHeight()+255+20);
                bookPreShowPane.addRow(2);
            }
        }
        if(!scrollNeeded){
            scrollPaneBooks.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        }
    }
    private FlowPane createImageView(JSONObject data, FlowPane flow){
        FlowPane container= new FlowPane();
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
        dropShadow.setColor(new javafx.scene.paint.Color(0, 0, 0,1));
        imageView.setEffect(dropShadow);
        imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String bookName=event.getPickResult().getIntersectedNode().getId();
                try {
                    switchToReaderScene(event,bookName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        ResizeAnimation rht = new ResizeAnimation(Duration.millis(200), imageView, new Pair<Double,Double>(170.0,170.0),new Pair<Double,Double>(190.0,190.0));

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
        try {
            readerScene.startTextFlow(fb2Reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        String css = Wordbound.class.getResource("styles/wordIncountered.css").toExternalForm();
        scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
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
}
