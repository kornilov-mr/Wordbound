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
        Config config= new Config();
        config.fontSize=(long) jo.get("fontSize");
        config.lastId=(long) jo.get("lastId");
        return config;
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
    public Map<String, Map<String,DeckWords>> loadWordsInbound() throws IOException, ParseException {
        Map<String, Map<String,DeckWords>> deckInTree = new HashMap<>();
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        Iterator<String> booksIterator = books.keySet().iterator();

        while(booksIterator.hasNext()) {
            String bookname = booksIterator.next();
            JSONObject decks= (JSONObject) books.get(bookname);
            String realBookName= (String) decks.get("realBookName");
            Iterator<String> decksIterator = decks.keySet().iterator();
            Map<String,DeckWords> decksInBook= new HashMap<>();

            while(decksIterator.hasNext()){
                String deckName = decksIterator.next();
                if(Objects.equals(deckName,"realBookName")){
                    continue;
                }
                Vector<WordInBound> wordInBound= new Vector<>();

                JSONObject deck= (JSONObject) decks.get(deckName);

                JSONObject words =(JSONObject) deck.get("wordsInbound");
                Iterator<String> idIterator= words.keySet().iterator();
                while(idIterator.hasNext()){
                    String currId= idIterator.next();
                    JSONObject wordInfo= (JSONObject) words.get(currId);
                    wordInBound.add(CreateWordInBoundFromJson(wordInfo,currId,"firstCard"));
                    wordInBound.add(CreateWordInBoundFromJson(wordInfo,currId,"secondCard"));
                }

                DeckWords currDeck = new DeckWords(wordInBound,deckName,bookname,realBookName);
                decksInBook.put(deckName,currDeck);
            }
            deckInTree.put(bookname,decksInBook);
        }
        return deckInTree;
    }

    private WordInBound CreateWordInBoundFromJson(JSONObject wordInfos,String id,String key){
        JSONObject wordInfo= (JSONObject) wordInfos.get(key);
        WordInBound word = new WordInBound((Long) wordInfo.get("realTime"),
                (Long) wordInfo.get("nextrepeat"),
                (String) wordInfo.get("deck"),
                (String) wordInfo.get("originalWord"),
                (String) wordInfo.get("time"),
                (String) wordInfo.get("wordTranslation"),
                (Long) wordInfo.get("repeatCount"),
                Long.parseLong(id),
                key,
                (String) wordInfo.get("context"),
                (String) wordInfo.get("bookName"));
        return word;

    }
}
