package maingroup.wordbound.Controllers.MainScene;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class CardRepeatController {

    @FXML
    private TextField firstWordField;
    @FXML
    private TextField secondWordField;
    @FXML
    public TextFlow contextTextFlow;
    private AccountClass account;
    private String bookName;
    private String deckName;
    private int idWordInbound;
    public void setWords(String firstWord,String secondWord){
        firstWordField.setText(firstWord);
        secondWordField.setText(secondWord);
    }
    public void setContext(String context){
        contextTextFlow.getChildren().clear();
        contextTextFlow.getChildren().add(new Text(context));
    }
    public void setData(String deckName,String bookName,int idWordInbound){
        this.idWordInbound=idWordInbound;
        this.bookName=bookName;
        this.deckName=deckName;
    }
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    public void changeWordInBound() throws IOException, ParseException {
        account.dataHandler.changeWordInbound(firstWordField.getText(),secondWordField.getText(),bookName,deckName,idWordInbound);
    }}
