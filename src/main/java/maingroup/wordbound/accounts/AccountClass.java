package maingroup.wordbound.accounts;

import javafx.util.Pair;
import maingroup.wordbound.utilities.Jsons.JsonHandler;
import maingroup.wordbound.utilities.Jsons.JsonReader;
import maingroup.wordbound.utilities.Jsons.JsonWritter;
import maingroup.wordbound.utilities.books.BookSet;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AccountClass {
    int lastId = 0;
    public JsonWritter jsonWritter = new JsonWritter();
    public JsonReader jsonReader = new JsonReader();
    public JsonHandler jsonHandler;
    public DataHandler dataHandler;
    public Config generalldata;
    public Vector<String> wordsIncountered = new Vector<>();
    public Map<String, Map<String, DeckWords>> deckInTree = new HashMap<>();
    public BookSet bookset;

    public AccountClass() throws IOException, ParseException {
        jsonHandler = new JsonHandler(jsonReader);
        updategeneralldata();
        jsonWritter.lastId = generalldata.lastId;
        updateWordIncountered();
        updateWordsInbound();
        updateBookset();
        dataHandler = new DataHandler(this);

    }

    public void updateWordIncountered() throws IOException, ParseException {
        jsonHandler.checkAndCreateIncountered();
        this.wordsIncountered = jsonReader.loadWordsIncoutered();
    }

    public void updateWordsInbound() throws IOException, ParseException {
        jsonHandler.checkAndCreateWordsInBound();
        this.deckInTree  = jsonReader.loadWordsInbound();
    }

    public void updateBookset() throws IOException, ParseException {
        jsonHandler.checkAndCreateBookJson();
        this.bookset = jsonReader.loadWordsBooks();
    }

    public void updategeneralldata() throws IOException, ParseException {
        jsonHandler.checkAndCreateUserData();
        this.generalldata = jsonReader.loadGenerallData();
    }
}