package maingroup.wordbound.Controllers.MainScene;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.accounts.AccountClass;
import org.json.simple.parser.ParseException;

import java.io.*;

public class MainSceneController {
    @FXML
    private VBox navSlideBar;
    @FXML
    private BorderPane mainBorderPane;
    private static AccountClass account;
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    @FXML
    public void loadBookScene() throws IOException, ParseException {
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainSceneBooks.fxml"));
        Parent root=null;
        try {
            root=fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainSceneBooks controller = fxmlLoader.getController();
        controller.loadAccount(account);
        controller.loadBooks();
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
        MainSceneRepeat controller = fxmlLoader.getController();
        controller.loadAccount(account);
        controller.loadWordInBound();
        mainBorderPane.setCenter(root);
    }
    public void loadStatisticsScene() throws IOException, ParseException {
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainSceneStatistic.fxml"));
        Parent root=null;
        try {
            root=fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MainSceneStatistics controller = fxmlLoader.getController();
        controller.loadAccount(account);
        controller.loadGrid();
        mainBorderPane.setCenter(root);
    }
    public void loadBrowserScene() throws IOException, ParseException {
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/browserScene/mainSceneBrowser.fxml"));
        Parent root=null;
        try {
            root=fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BrowserSceneController controller = fxmlLoader.getController();
        controller.loadAccount(account);
        controller.loadDecks();
        controller.loadComboBox();
        controller.setUpTable();
        controller.updateTable("all");
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
    public void init() throws IOException, ParseException {
        loadBookScene();
        navSlideBar.setTranslateX(-120);
    }
}
