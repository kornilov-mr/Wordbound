package maingroup.wordbound.Controllers.readerScene;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import maingroup.wordbound.Controllers.MainScene.MainSceneRepeat;
import maingroup.wordbound.accounts.AccountClass;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Objects;

public class DeckCreaterScene {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField newDeckField;
    private AccountClass account;
    private String bookName;
    private String realBookName;
    private TranslationNoteController controller;
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    public void passBookName(String bookName,String realBookName){
        this.bookName=bookName;
        this.realBookName=realBookName;
    }
    public void setParent(TranslationNoteController controller){
        this.controller= controller;
    }
    public void createNewDeck() throws IOException, ParseException {
        String newDeckName = newDeckField.getText();
        if(Objects.equals(newDeckName,"")){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("inappropriate deck name");
        }else{
            account.jsonWritter.addNewDeckToWordInBoundJson(newDeckName,bookName);
            account.dataHandler.addNewDeckToWordInBoundJson(newDeckName,bookName,realBookName);
            controller.loadListView();
            controller.lastDeck=newDeckName;
            Stage stage;
            stage = (Stage) mainPane.getScene().getWindow();
            stage.close();
        }
    }
}
