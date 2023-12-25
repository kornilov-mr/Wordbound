package maingroup.wordbound;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import maingroup.wordbound.Controllers.MainScene.MainSceneController;
import maingroup.wordbound.accounts.AccountClass;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Wordbound extends Application {

    AccountClass account;
    @Override
    public void start(Stage stage) throws IOException, ParseException {
        AccountClass acount = new AccountClass();
        this.account=acount;
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainScene.fxml"));
        Parent root = fxmlLoader.load();
        MainSceneController controller= fxmlLoader.getController();
        controller.loadAccount(acount);
        controller.init();
        Scene scene = new Scene(root);
        String css = Wordbound.class.getResource("styles/mainScene.css").toExternalForm();
        scene.getStylesheets().add(css);
        stage.setTitle("Wordbound");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            try {
                logout(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void logout(Stage stage) throws IOException, ParseException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("You are about to logout");
        alert.setContentText("Do you want to save before exit");
        if(alert.showAndWait().get() == ButtonType.OK) {

            account.jsonWritter.saveGeneralData(account.generalldata);
            account.jsonWritter.saveWordsInBound(account.deckInTree);
            account.jsonWritter.saveWordsIncountered(account.wordsIncountered);
            account.statisticController.saveStatistic();
            stage.close();
        }else{
            stage.close();

        }
    }


    public static void main(String[] args) {
        launch();
    }
}