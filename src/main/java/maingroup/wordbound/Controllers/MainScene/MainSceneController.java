package maingroup.wordbound.Controllers.MainScene;

import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import maingroup.wordbound.Controllers.RepeatSceneController;
import maingroup.wordbound.Wordbound;
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

public class MainSceneController implements Initializable {
    @FXML
    private VBox navSlideBar;
    @FXML
    private BorderPane mainBorderPane;
    private Scene scene;
    private Parent root;
    private Stage stage;

    @FXML
    public void loadBookScene(){
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainSceneBooks.fxml"));
        Parent root=null;
        try {
            root=fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mainBorderPane.setCenter(root);
    }
    public void loadReoeatScene(){
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainSceneRepeat.fxml"));
        Parent root=null;
        try {
            root=fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainBorderPane.setCenter(root);
    }

    public void openNavBar(){
        TranslateTransition slide= new TranslateTransition();
        slide.setDuration(Duration.seconds(0.3));
        slide.setNode(navSlideBar);
        slide.setToX(0);
        slide.play();
    }
    public void closeNavBar(){
        TranslateTransition slide= new TranslateTransition();
        slide.setDuration(Duration.seconds(0.3));
        slide.setNode(navSlideBar);
        slide.setToX(-120);
        slide.play();
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadBookScene();
        navSlideBar.setTranslateX(-120);
    }
}
