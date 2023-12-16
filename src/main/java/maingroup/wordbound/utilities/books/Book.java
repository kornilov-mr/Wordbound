package maingroup.wordbound.utilities.books;

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
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
import maingroup.wordbound.Controllers.ReaderSceneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.bookreaders.Fb2Reader;
import maingroup.wordbound.utilities.animations.ResizeAnimation;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.concurrent.Flow;

public class Book {
    private final String jsonPath = new File("").getAbsolutePath() + "\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    public FlowPane content;
    public String coverPath;
    protected String realBookName;
    protected long timeLastSeen;
    protected String StorageBookName;
    protected String author;
    public Book(String coverPath,String StorageBookName,String realBookName,String author,long timeLastSeen){
        this.coverPath=coverPath;
        this.author=author;
        this.StorageBookName=StorageBookName;
        this.realBookName=realBookName;
        this.timeLastSeen=timeLastSeen;
    }
    protected void upDateTime() throws IOException, ParseException {
        this.timeLastSeen=System.currentTimeMillis();
        Object obj = new JSONParser().parse(new FileReader(this.jsonPath));
        JSONObject jo = (JSONObject) obj;
        JSONObject booksInfo = (JSONObject) jo.get("books");
        JSONObject bookdate= (JSONObject) booksInfo.get(this.StorageBookName);
        bookdate.remove("timeLastSeen");
        bookdate.put("timeLastSeen",System.currentTimeMillis());
        PrintWriter pw = new PrintWriter(this.jsonPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public FlowPane createFlowPane(AccountClass account){
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
        Label nameLabel = new Label(realBookName);
        nameLabel.setId("bookNameLabel");
        Label authorLabel = new Label(author);
        authorLabel.setId("authorLabel");
        flowText.getChildren().add(nameLabel);
        flowText.getChildren().add(authorLabel);

        flow.getChildren().add(createImageView(flow,account));
        flow.getChildren().add(flowText);
        return flow;

    }
    private FlowPane createImageView(FlowPane flow,AccountClass account) {
        FlowPane container = new FlowPane();
        FileInputStream inputstream = null;
        try {
            inputstream = new FileInputStream(coverPath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Image image = new Image(inputstream);

        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setFitHeight(160);
        imageView.setFitWidth(160);
        imageView.setId(StorageBookName);

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
                    switchToReaderScene(event, bookName,account);
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
    private void switchToReaderScene(MouseEvent event,String bookName, AccountClass account) throws IOException, ParseException {
        upDateTime();
        Fb2Reader fb2Reader= null;
        try {
            fb2Reader = new Fb2Reader(bookName,account,false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/readerScene.fxml"));
        Parent root= null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ReaderSceneController readerScene = fxmlLoader.getController();
        readerScene.loadAccount(account);
        try {
            readerScene.startTextFlow(fb2Reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        String css = Wordbound.class.getResource("styles/wordIncountered.css").toExternalForm();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(css);
        stage.setScene(scene);
        stage.show();
    }
}
