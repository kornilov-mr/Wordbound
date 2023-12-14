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
import javafx.scene.control.TextField;
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
import maingroup.wordbound.accounts.AccountClass;
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

public class MainSceneBooks {
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
            Fb2Reader fb2Reader = new Fb2Reader(selectedFile.getAbsolutePath(),true);
            bookPreShowPane.getChildren().clear();
            LoadFlowPlane();

        }
    }

    public void loadBooks() {
        scrollPaneBooks.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        LoadFlowPlane();
    }
    private void LoadFlowPlane() {
        Vector<Book> bookOrder= account.bookset.getSortedBytime();
        displayBookSet(bookOrder);
    }
    private void displayBookSet(Vector<Book> bookOrder){
        boolean scrollNeeded = false;
        for(int i=0;i<bookOrder.size();i++){
            int j=i/3;
            bookPreShowPane.add(bookOrder.get(i).content,i%3,j);
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
        Vector<Book> bookOrder= bookset.sortBooksByString(bookSearch.getText());
        displayBookSet(bookOrder);
    }
}
