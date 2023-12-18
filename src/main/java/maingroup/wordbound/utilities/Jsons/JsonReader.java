package maingroup.wordbound.utilities.Jsons;

import javafx.util.Pair;
import maingroup.wordbound.accounts.Config;
import maingroup.wordbound.utilities.books.Book;
import maingroup.wordbound.utilities.books.BookSet;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class JsonReader {
    private final String userDataPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\userGenerallData.json";
    private final String wordsInBoundPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";
    private final String wordsInCounteredPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsIncountered.json";
    private final String bookPath = new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";

    public JsonReader(){

    }
    public Vector<String> loadWordsIncoutered() throws IOException, ParseException {
        Vector<String>  wordsIncountered= new Vector<>();
        Object obj = new JSONParser().parse(new FileReader(wordsInCounteredPath));
        JSONObject jo = (JSONObject) obj;
        long bookCount = (long) jo.get("bookCount");
        if (bookCount == 0) {
            return wordsIncountered;
        }
        JSONArray books = (JSONArray) jo.get("wordsIncountered");
        for (int i = 0; i < bookCount; i++) {
            wordsIncountered.add((String) books.get(i));
        }
        return wordsIncountered;
    }
    public Config loadGenerallData() throws IOException, ParseException {
        Map<String,Object> generalldata= new HashMap<>();

        Object obj = new JSONParser().parse(new FileReader(userDataPath));
        JSONObject jo = (JSONObject) obj;
        return new Config((long) jo.get("fontSize"));
    }
    public BookSet loadWordsBooks() {
        BookSet bookSet= new BookSet();
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(bookPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject jo = (JSONObject) obj;
        long bookCount = (long) jo.get("bookCount");
        if (bookCount == 0) {
            return bookSet;
        }
        JSONObject booksInfo = (JSONObject) jo.get("books");
        Iterator<String> booksIterator = booksInfo.keySet().iterator();


        while (booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            JSONObject data = (JSONObject) booksInfo.get(bookname);
            Book currBook = new Book((String)data.get("coverPath"),(String) data.get("name"), (String) data.get("realBookName"), (String) data.get("author"), (Long) data.get("timeLastSeen"));
            bookSet.addBook((String) data.get("name"), currBook);
        }
        return bookSet;
    }
    public Pair<Map<String, Pair<Map<String,DeckWords>,DeckIndicator>>,Map<String,DeckWords>> loadWordsInbound() throws IOException, ParseException {
        Map<String, Pair<Map<String,DeckWords>,DeckIndicator>> deckInTree = new HashMap<>();
        Map<String,DeckWords> decksInVector= new HashMap<>();
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        Iterator<String> booksIterator = books.keySet().iterator();

        while(booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            JSONObject decks= (JSONObject) books.get(bookname);
            String realBookName= (String) decks.get("realBookName");
            Iterator<String> decksIterator = decks.keySet().iterator();
            Vector<DeckIndicator> indicators= new Vector<>();
            Map<String, DeckWords> decksInBook = new HashMap<>();
            while(decksIterator.hasNext()){
                String deckName = decksIterator.next();
                if(Objects.equals(deckName,"realBookName")){
                    continue;
                }
                Vector<WordInBound> wordInBound= new Vector<>();

                JSONObject deck= (JSONObject) decks.get(deckName);
                long wordCount= (long) deck.get("wordCount");
                JSONArray words =(JSONArray) deck.get("wordsInbound");
                for(int i=0;i<wordCount;i++){
                    JSONObject wordInfo= (JSONObject) words.get(i);
                    wordInBound.add(CreateWordInBoundFromJson(wordInfo));
                }
                DeckWords currDeck = new DeckWords(wordInBound,deckName,bookname,realBookName);
                decksInVector.put(bookname+"::"+deckName,currDeck);
                decksInBook.put(deckName,currDeck);
                indicators.add(currDeck.getIndicator());
            }
            deckInTree.put(bookname,new Pair<>(decksInBook,sumIndecators(indicators)));
        }
        return new Pair<>(deckInTree,decksInVector);
    }
    private DeckIndicator sumIndecators(Vector<DeckIndicator> indicators){
        int blue=0;
        int red=0;
        int green=0;
        for(int i=0;i<indicators.size();i++){
            DeckIndicator currIndicator= indicators.get(i);
            blue+=currIndicator.blue;
            red+=currIndicator.red;
            green+=currIndicator.green;
        }
        return new DeckIndicator(red,blue,green);
    }
    private WordInBound CreateWordInBoundFromJson(JSONObject wordInfo){
        WordInBound word = new WordInBound((Long) wordInfo.get("realTime"),
                (Long) wordInfo.get("nextrepeat"),
                (String) wordInfo.get("deck"),
                (String) wordInfo.get("originalWord"),
                (String) wordInfo.get("time"),
                (String) wordInfo.get("wordTranslation"),
                (Long) wordInfo.get("repeatCount"));
        return word;

    }
}
