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

    public String jsonBookPath=new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    AccountClass account;
    @Override
    public void start(Stage stage) throws IOException, ParseException {
        initialize();
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
            stage.close();
        }
    }
    private void initialize() throws IOException, ParseException {
        File f = new File(jsonBookPath);
        if(!f.exists()) {
            CreateBookJson();
        }
    }
    private void CreateBookJson() throws IOException {
        try {
            File myObj = new File(jsonBookPath);
            if (myObj.createNewFile()) {
                System.out.println("json file for books was created");
            } else {
                System.out.println("json file for books is already present");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }


        JSONObject jo = new JSONObject();
        jo.put("bookCount",0);
        jo.put("wordCount",0);
        jo.put("books",new JSONObject());


        PrintWriter pw = new PrintWriter(jsonBookPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public static void main(String[] args) {
        launch();
    }
}