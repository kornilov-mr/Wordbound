package maingroup.wordbound.utilities.Jsons;

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
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;

public class JsonWritter {
    private final String userDataPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\userGenerallData.json";
    private final String wordsInBoundPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";
    private final String wordsIncounteredPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsIncountered.json";
    private final String bookJsonPath = new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    public JsonWritter(){

    }
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
    public void addNewBookToWordInBoundJson(String bookName,String realBookName) throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        JSONObject deckJson= new JSONObject();
        JSONObject wordinboundJson = new JSONObject();


        wordinboundJson.put("wordCount",0);
        wordinboundJson.put("wordsInbound",new JSONArray());
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
        wordinboundJson.put("wordsInbound",new JSONArray());

        deckJson.put(deckName,wordinboundJson);
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public void updateWordInBountJson(String originalWord, String wordTranslation,String bookName,String deckName) throws IOException, ParseException {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime data = LocalDateTime.now();

        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");

        JSONObject curr_book = (JSONObject) books.get(bookName);

        JSONObject deck = (JSONObject) curr_book.get(deckName);

        JSONArray wordsInbound = (JSONArray) deck.get("wordsInbound");

        JSONObject boundData= new JSONObject();
        boundData.put("originalWord",originalWord);
        boundData.put("wordTranslation",wordTranslation);

        boundData.put("time",dtf.format(data));
        boundData.put("realTime",System.currentTimeMillis());
        boundData.put("nextrepeat",System.currentTimeMillis());

        boundData.put("deck","default");

        boundData.put("repeatCount",-1);


        wordsInbound.add(boundData);

        long wordCount= (long) deck.get("wordCount");
        deck.remove("wordCount");
        deck.put("wordCount", wordCount+1);

        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
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
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        Iterator<String> booksIterator = books.keySet().iterator();

        JSONObject decks=(JSONObject) books.get(deckWords.bookName);
        JSONObject deckJson=(JSONObject) decks.get(deckWords.deckName);
        deckJson.remove("wordsInbound");

        JSONArray words = new JSONArray();
        for(int i=0;i<deckWords.deck.size();i++){
            WordInBound word =  deckWords.deck.get(i);
            words.add(word.toJson());
        }
        deckJson.put("wordsInbound", words);
        deckJson.remove("wordCount");
        deckJson.put("wordCount",words.size());
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
}
