package maingroup.wordbound.accounts;

import javafx.util.Pair;
import maingroup.wordbound.utilities.Jsons.JsonReader;
import maingroup.wordbound.utilities.Jsons.JsonWritter;
import maingroup.wordbound.utilities.books.BookSet;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;

public class AccountClass {
    public JsonWritter jsonWritter= new JsonWritter();
    public JsonReader jsonReader = new JsonReader();
    public Config generalldata;
    public Vector<String> wordsIncountered= new Vector<>();
    public Map<String,DeckWords> decksInVector = new HashMap<>();
    public Map<String, Pair<Map<String,DeckWords>,DeckIndicator>> deckInTree= new HashMap<>();
    public BookSet bookset;
    private final String wordsInBoundPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";

    public AccountClass() throws IOException, ParseException {
        updateWordIncountered();
        updateWordsInbound();
        updateBookset();
        updategeneralldata();
    }
    public void updateWordIncountered() throws IOException, ParseException {
        this.wordsIncountered= jsonReader.loadWordsIncoutered();
    }
    public void updateWordsInbound() throws IOException, ParseException{
        Pair<Map<String, Pair<Map<String,DeckWords>,DeckIndicator>>,Map<String,DeckWords>> data= jsonReader.loadWordsInbound();
        this.deckInTree= data.getKey();
        this.decksInVector= data.getValue();
    }
    public void updateBookset() throws IOException, ParseException{
        this.bookset= jsonReader.loadWordsBooks();
    }
    public void updategeneralldata() throws IOException, ParseException {
        this.generalldata=jsonReader.loadGenerallData();

    }


}
