package maingroup.wordbound.Controllers.MainScene;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

public class BrowserSceneController {
    AccountClass account;
    @FXML
    private AnchorPane cardForDeck;
    @FXML
    private TableView<WordInBound> cardShowTable;
    @FXML
    private ComboBox<String> deckSelector;
    private Map<String, Set<String>> deckTorefer;
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    public void loadDecks() throws IOException, ParseException {
        Map<String, Set<String>> decksToPick= new HashMap<>();
        Iterator<String> bookIterator = account.deckInTree.keySet().iterator();
        Set<String> allDecks= new HashSet<>();
        while(bookIterator.hasNext()){
            String bookName = bookIterator.next();
            Iterator<String> deckIterator= account.deckInTree.get(bookName).keySet().iterator();
            Set<String> decksNeeded= new HashSet<>();
            while(deckIterator.hasNext()){
                String deckName= deckIterator.next();
                decksNeeded.add(bookName+"::"+deckName);
                allDecks.add(bookName+"::"+deckName);
                decksToPick.put(bookName+"::"+deckName,new HashSet<>(Arrays.asList(bookName+"::"+deckName)));
            }
            decksToPick.put(bookName,decksNeeded);
        }
        decksToPick.put("all",allDecks);
        this.deckTorefer= decksToPick;
    }
    public void loadComboBox() throws IOException, ParseException {
        Set<String> decks = deckTorefer.keySet();
        deckSelector.getItems().addAll(decks);
        deckSelector.getSelectionModel().select("all");
    }
    private void loadWordInbound(WordInBound word) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/repeatScene/repeatCard.fxml"));
        fxmlLoader.setController(new CardRepeatController());
        AnchorPane CardNote = fxmlLoader.load();
        CardRepeatController controller = fxmlLoader.getController();
        controller.setWords(word.originalWord,word.wordTranslation);
        controller.setContext(word.context);
        controller.loadAccount(account);



        controller.setData(word.deck,word.bookName, (int) word.id);
        cardForDeck.getChildren().add(CardNote);
    }

    public void setUpTable(){
        TableColumn<WordInBound, String> column1 =
                new TableColumn<>("Id");
        column1.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<WordInBound, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WordInBound, String> p) {
                return p.getValue().idProperty();
            }
        });
        column1.setMaxWidth(30);
        column1.setPrefWidth(30);
        TableColumn<WordInBound, String> column2 =
                new TableColumn<>("First word");
        column2.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<WordInBound, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WordInBound, String> p) {
                return p.getValue().originalWordProperty();
            }
        });
        column2.setMaxWidth(120);
        column2.setPrefWidth(120);
        TableColumn<WordInBound, String> column3 =
                new TableColumn<>("Second word");
        column3.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<WordInBound, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WordInBound, String> p) {
                return p.getValue().wordTranslationProperty();
            }
        });
        column3.setMaxWidth(120);
        column3.setPrefWidth(120);
        TableColumn<WordInBound, String> column4 =
                new TableColumn<>("Context");
        column4.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<WordInBound, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WordInBound, String> p) {
                return p.getValue().contextProperty();
            }
        });
        column4.setMaxWidth(170);
        column4.setPrefWidth(170);
        TableColumn<WordInBound, String> column5 =
                new TableColumn<>("deck");
        column5.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<WordInBound, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WordInBound, String> p) {
                return p.getValue().deckProperty();
            }
        });
        column5.setMaxWidth(80);
        column5.setPrefWidth(80);
        TableColumn<WordInBound, String> column6 =
                new TableColumn<>("Key");
        column6.setCellValueFactory( new Callback<TableColumn.CellDataFeatures<WordInBound, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WordInBound, String> p) {
                return p.getValue().keyProperty();
            }
        });
        column6.setMaxWidth(80);
        column6.setPrefWidth(80);
        cardShowTable.getColumns().add(column1);
        cardShowTable.getColumns().add(column2);
        cardShowTable.getColumns().add(column3);
        cardShowTable.getColumns().add(column4);
        cardShowTable.getColumns().add(column5);
        cardShowTable.getColumns().add(column6);
        cardShowTable.setRowFactory( tv -> {
            TableRow<WordInBound> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                cardForDeck.getChildren().clear();
                WordInBound word = row.getItem();
                try {
                    loadWordInbound(word);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            return row ;
        });
    }
    public void updateTable(String deckName){
        Set<String> deckToShow= deckTorefer.get(deckName);
        Vector<WordInBound> WordBoundToShow= new Vector<>();
        Iterator<String> deckIterator = deckToShow.iterator();
        while(deckIterator.hasNext()){
            String reference = deckIterator.next();
            WordBoundToShow.addAll(getDeckByReferense(reference));
        }
        cardShowTable.getItems().addAll(WordBoundToShow);
    }
    public void onDeckChange(){
        String selectedDeck=deckSelector.getSelectionModel().getSelectedItem();
        cardShowTable.getItems().clear();
        updateTable(selectedDeck);
    }
    private Vector<WordInBound> getDeckByReferense(String reference){
        String[] BookAndDeck = reference.split("::");
        return account.deckInTree.get(BookAndDeck[0]).get(BookAndDeck[1]).deck;
    }

}
