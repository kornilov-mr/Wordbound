package maingroup.wordbound.utilities.Jsons;

import javafx.util.Pair;
import maingroup.wordbound.accounts.Config;
import maingroup.wordbound.bookreaders.Bookdata;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JsonWritter {
    private final String userDataPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\userGenerallData.json";
    private final String wordsInBoundPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";
    private final String wordsIncounteredPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsIncountered.json";
    private final String bookJsonPath = new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    public long lastId;
    public boolean alreadyadded(String bookName) throws IOException, ParseException {

        Object obj = new JSONParser().parse(new FileReader(bookJsonPath));
        JSONObject jo = (JSONObject) obj;
        long bookCount = (long) jo.get("bookCount");
        if (bookCount == 0) {
            return false;
        }
        JSONObject booksInfo = (JSONObject) jo.get("books");
        Iterator<String> booksiterator= booksInfo.keySet().iterator();
        boolean thereIsBook = false;
        while(booksiterator.hasNext()){
            JSONObject data = (JSONObject) booksInfo.get(booksiterator.next());
            if (Objects.equals((String) data.get("name"), (String) bookName)) {
                thereIsBook = true;
            }
        }
        return thereIsBook;
    }

    public void updateJson(Bookdata bookdata) throws IOException, ParseException {

        if (alreadyadded(bookdata.bookName)) {
            return;
        } else {
            Object obj = new JSONParser().parse(new FileReader(bookJsonPath));
            JSONObject jo = (JSONObject) obj;
            JSONObject booksInfo = (JSONObject) jo.get("books");
            long bookCount = (long) jo.get("bookCount");
            booksInfo.put(bookdata.bookName,bookdata.createJsondata());
            jo.remove("bookCount");
            jo.put("bookCount", bookCount + 1);
            PrintWriter pw = new PrintWriter(bookJsonPath);
            pw.write(jo.toJSONString());
            pw.flush();
            pw.close();
        }
    }
    public void updateBookData(String BookName, int lastPage, String lastDeck) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(bookJsonPath));
        JSONObject jo = (JSONObject) obj;
        JSONObject booksInfo = (JSONObject) jo.get("books");
        JSONObject bookInfo= (JSONObject) booksInfo.get(BookName);
        bookInfo.remove("lastPage");
        bookInfo.remove("lastDeck");
        bookInfo.put("lastPage",lastPage);
        bookInfo.put("lastDeck",lastDeck);
        PrintWriter pw = new PrintWriter(bookJsonPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();

    }
    //rewrite
    public void addNewBookToWordInBoundJson(String bookName,String realBookName) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        JSONObject deckJson= new JSONObject();

        deckJson.put("default",new JSONObject());
        deckJson.put("realBookName",realBookName);
        books.put(bookName,deckJson);
        jo.remove("books");
        jo.put("books",books);
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();

    }
    public void addNewDeckToWordInBoundJson(String deckName,String bookName) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        JSONObject deckJson= (JSONObject) books.get(bookName);

        deckJson.put(deckName,new JSONObject());
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }


    public void saveGeneralData(Config config) throws IOException {
        File json = new File(userDataPath);
        json.delete();
        json.createNewFile();
        PrintWriter pw = new PrintWriter(userDataPath);
        pw.write(config.createJson().toJSONString());
        pw.flush();
        pw.close();
    }
    public void saveWordsIncountered(Vector<String> wordsIncountered) throws IOException, ParseException {
        File json = new File(wordsIncounteredPath);
        json.delete();
        json.createNewFile();
        JSONObject jo = new JSONObject();
        jo.put("bookCount",wordsIncountered.size());
        jo.put("wordsIncountered",wordsIncountered);
        PrintWriter pw = new PrintWriter(wordsIncounteredPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public void saveWordsInBound(Map<String, Map<String,DeckWords>> deckInTree) throws IOException, ParseException {
        File json = new File(wordsInBoundPath);
        json.delete();
        json.createNewFile();
        JSONObject jo = new JSONObject();
        JSONObject books= new JSONObject();
        Iterator<String> booksIterator = deckInTree.keySet().iterator();
        while(booksIterator.hasNext()){

            String bookName= booksIterator.next();
            JSONObject decksJson= new JSONObject();
            Map<String,DeckWords> decks = deckInTree.get(bookName);
            Iterator<String> deckIterator = decks.keySet().iterator();
            String deckName="";
            DeckWords currDeck = null;
            while(deckIterator.hasNext()){
                deckName = deckIterator.next();
                currDeck = decks.get(deckName);
                decksJson.put(deckName,currDeck.toJson());
            }
            decksJson.put("realBookName",currDeck.realBookName);
            books.put(bookName,decksJson);

        }
        jo.put("books",books);
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
}
