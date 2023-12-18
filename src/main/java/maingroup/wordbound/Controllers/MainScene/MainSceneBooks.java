package maingroup.wordbound.Controllers.MainScene;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.bookreaders.Fb2Reader;
import maingroup.wordbound.utilities.books.Book;
import org.json.simple.parser.ParseException;

import javafx.scene.control.ScrollPane;
import java.io.*;
import java.util.Vector;

public class MainSceneBooks {
    private Stage stage;
    private final String[] supportedExtensions = {"*.fb2"};
    private FileChooser.ExtensionFilter BooksEx = new FileChooser.ExtensionFilter("Books",supportedExtensions);
    private final FileChooser.ExtensionFilter AllEx = new FileChooser.ExtensionFilter("All","*.*");
    public String jsonBookPath=new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    @FXML
    private GridPane bookPreShowPane;
    @FXML
    private ScrollPane scrollPaneBooks;
    @FXML
    private TextField bookSearch;
    private AccountClass account;
    public void loadAccount(AccountClass account){
        this.account= account;
    }
    public void addBookButtononKlick() throws IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(BooksEx,AllEx);
        File selectedFile= fileChooser.showOpenDialog(stage);
        if(selectedFile!=null){
            Fb2Reader fb2Reader = new Fb2Reader(selectedFile.getAbsolutePath(),account,true);
            bookPreShowPane.getChildren().clear();
            LoadFlowPlane();

        }
    }

    public void loadBooks() throws IOException, ParseException {
        scrollPaneBooks.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        LoadFlowPlane();
    }
    private void LoadFlowPlane() throws IOException, ParseException {
        account.updateBookset();
        Vector<Book> bookOrder= account.bookset.getSortedBytime();
        displayBookSet(bookOrder);
    }
    private void displayBookSet(Vector<Book> bookOrder){
        boolean scrollNeeded = false;
        for(int i=0;i<bookOrder.size();i++){
            int j=i/3;
            bookPreShowPane.add(bookOrder.get(i).createFlowPane(account),i%3,j);
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
    public void startSearch(){
        bookPreShowPane.getChildren().clear();
        Vector<Book> bookOrder= account.bookset.sortBooksByString(bookSearch.getText());
        displayBookSet(bookOrder);
    }
}
