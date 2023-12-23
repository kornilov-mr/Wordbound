package maingroup.wordbound.Controllers.readerScene;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import maingroup.wordbound.accounts.AccountClass;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class AddNoteController {
    @FXML
    private AnchorPane mainPane;
    @FXML
    private TextField firstWordField;
    @FXML
    private TextField secondWordField;
    private int idWordInbound;
    private String bookName;
    private String deckName;
    private AccountClass account;
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    public void loadWords(String firstWord,String secondWord){
        firstWordField.setText(firstWord);
        secondWordField.setText(secondWord);
    }
    public void loadCardData(String bookName,String deckName,int idWordInBound){
        this.idWordInbound= idWordInBound;
        this.bookName= bookName;
        this.deckName=deckName;
    }
    public void changeWordInBound() throws IOException, ParseException {
        account.dataHandler.changeWordInbound(firstWordField.getText(),secondWordField.getText(),bookName,deckName,idWordInbound);
    }

}
