package maingroup.wordbound.utilities.Jsons;

import maingroup.wordbound.accounts.Config;
import maingroup.wordbound.bookreaders.Bookdata;
import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;

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
    public void addNewBookToWordInBoundJson(String bookName,String realBookName) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        JSONObject deckJson= new JSONObject();
        JSONObject wordinboundJson = new JSONObject();


        wordinboundJson.put("wordCount",0);
        wordinboundJson.put("wordsInbound",new JSONObject());
        deckJson.put("default",wordinboundJson);
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

        JSONObject wordinboundJson = new JSONObject();
        wordinboundJson.put("wordCount",0);
        wordinboundJson.put("wordsInbound",new JSONObject());

        deckJson.put(deckName,wordinboundJson);
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public void changeWordInbound(String originalWord, String wordTranslation,String bookName,String deckName,int idWordinBound) throws IOException, ParseException {

        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");

        JSONObject curr_book = (JSONObject) books.get(bookName);

        JSONObject deck = (JSONObject) curr_book.get(deckName);

        JSONObject wordsInbound = (JSONObject) deck.get("wordsInbound");
        JSONObject card = (JSONObject) wordsInbound.get(String.valueOf(idWordinBound));
        JSONObject word1= (JSONObject) card.get("firstCard");
        word1.remove("originalWord");
        word1.remove("wordTranslation");
        word1.put("originalWord",originalWord);
        word1.put("wordTranslation",wordTranslation);

        JSONObject word2= (JSONObject) card.get("secondCard");
        word2.remove("originalWord");
        word2.remove("wordTranslation");
        word2.put("originalWord",wordTranslation);
        word2.put("wordTranslation",originalWord);

        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public void changeWordInboundDeck(String bookName,String deckNameOld,String deckNameNew,int idWordinBound) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");

        JSONObject curr_book = (JSONObject) books.get(bookName);

        JSONObject deckold = (JSONObject) curr_book.get(deckNameOld);

        JSONObject wordsInbound = (JSONObject) deckold.get("wordsInbound");
        JSONObject wordInbound = (JSONObject) wordsInbound.get(idWordinBound);
        wordsInbound.remove(idWordinBound);

        JSONObject decknew = (JSONObject) curr_book.get(deckNameNew);
        wordsInbound = (JSONObject) decknew.get("wordsInbound");
        wordsInbound.put(idWordinBound,wordInbound);

        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public JSONObject createWordJson(String originalWord, String wordTranslation,String bookName,String deckName,String context){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime data = LocalDateTime.now();

        JSONObject boundData= new JSONObject();
        boundData.put("originalWord",originalWord);
        boundData.put("wordTranslation",wordTranslation);

        boundData.put("time",dtf.format(data));
        boundData.put("realTime",System.currentTimeMillis());
        boundData.put("nextrepeat",System.currentTimeMillis());
        if(!Objects.equals(context,"null")){
            boundData.put("context",context);
        }
        boundData.put("repeatCount",-1);
        return boundData;
    }
    public int updateWordInBountJson(String originalWord, String wordTranslation,String bookName,String deckName,String context) throws IOException, ParseException {


        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");

        JSONObject curr_book = (JSONObject) books.get(bookName);

        JSONObject deck = (JSONObject) curr_book.get(deckName);

        JSONObject wordsInbound = (JSONObject) deck.get("wordsInbound");


        JSONObject cardInBound = new JSONObject();

        cardInBound.put("firstCard",createWordJson(originalWord,wordTranslation,bookName,deckName,context));
        cardInBound.put("secondCard",createWordJson(wordTranslation,originalWord,bookName,deckName,context));
        wordsInbound.put(lastId,cardInBound);
        lastId+=1;


        long wordCount= (long) deck.get("wordCount");
        deck.remove("wordCount");
        deck.put("wordCount", wordCount+1);

        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
        return (int) lastId-1;
    }

    public void updateWordsIncoutered(Vector<String> words) throws IOException, ParseException {

        Object obj = new JSONParser().parse(new FileReader(wordsIncounteredPath));
        JSONObject jo = (JSONObject) obj;

        JSONArray arr= (JSONArray) jo.get("wordsIncountered");
        long bookCount= (long) jo.get("bookCount");
        int addCount=0;
        for(int i=0;i<words.size();i++){
            boolean isIn=false;
            for(int j=0;j<bookCount;j++){
                if(Objects.equals(arr.get(j),words.get(i))){
                    isIn=true;
                }
            }
            if(!isIn) {
                arr.add(words.get(i));
                addCount += 1;
            }
        }
        jo.remove("bookCount");
        jo.put("bookCount", addCount+bookCount);
        PrintWriter pw = new PrintWriter(wordsIncounteredPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public void saveDeckInJson(DeckWords deckWords) throws IOException, ParseException {
        deckWords.saveOrder();

        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");

        JSONObject decks=(JSONObject) books.get(deckWords.bookName);
        JSONObject deckJson=(JSONObject) decks.get(deckWords.deckName);
        deckJson.remove("wordsInbound");

        JSONObject words = new JSONObject();
        for(int i=0;i<deckWords.deck.size();i+=2){
            WordInBound word1 =  deckWords.deck.get(i);
            WordInBound word2 =  deckWords.deck.get(i+1);
            JSONObject card = new JSONObject();
            card.put(word1.key,word1.toJson());
            card.put(word2.key,word2.toJson());
            words.put(word1.id,card);
        }
        deckJson.put("wordsInbound", words);
        deckJson.remove("wordCount");
        deckJson.put("wordCount",words.size());
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public void updateGeneralData(Config config) throws IOException {
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(config.createJson().toJSONString());
        pw.flush();
        pw.close();
    }
}
