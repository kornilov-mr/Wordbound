package maingroup.wordbound.utilities.Jsons;

import javafx.util.Pair;
import maingroup.wordbound.utilities.books.Book;
import maingroup.wordbound.utilities.books.BookSet;
import maingroup.wordbound.utilities.repeats.DeckIndicator;
import maingroup.wordbound.utilities.repeats.DeckWords;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class JsonHandler {
    private final String userDataPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\userGenerallData.json";
    private final String wordsInBoundPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";
    private final String wordsIncounteredPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsIncountered.json";

    private final String bookJsonPath = new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";
    private JsonReader jsonReader;
    public JsonHandler(JsonReader jsonReader){
        this.jsonReader= jsonReader;
    }
    public void checkAndCreateIncountered() throws IOException, ParseException {
        if(!new File(wordsIncounteredPath).isFile()){
            File json = new File(wordsIncounteredPath);
            json.createNewFile();
            JSONObject jo = new JSONObject();
            jo.put("bookCount",0);
            jo.put("wordsIncountered",new JSONArray());
            PrintWriter pw = new PrintWriter(wordsIncounteredPath);
            pw.write(jo.toJSONString());
            pw.flush();
            pw.close();
        }
    }
    public void checkAndCreateUserData() throws IOException, ParseException {
        if(!new File(userDataPath).isFile()){
            File json = new File(userDataPath);
            json.createNewFile();
            JSONObject jo = new JSONObject();

            jo.put("fontSize",6);
            jo.put("lastId",0);
            PrintWriter pw = new PrintWriter(userDataPath);
            pw.write(jo.toJSONString());
            pw.flush();
            pw.close();
        }
    }
    public void checkAndCreateBookJson() throws IOException, ParseException {
        if(!new File(bookJsonPath).isFile()){
            File json = new File(bookJsonPath);
            json.createNewFile();

            JSONObject jo = new JSONObject();

            jo.put("bookCount",0);
            jo.put("books",new JSONObject());
            PrintWriter pw = new PrintWriter(bookJsonPath);
            pw.write(jo.toJSONString());
            pw.flush();
            pw.close();
        }
    }
    public void checkAndCreateWordsInBound() throws IOException, ParseException {
        if(!new File(wordsInBoundPath).isFile()){
            File json = new File(wordsInBoundPath);
            json.createNewFile();

            JSONObject jo = new JSONObject();

            jo.put("bookCount",0);
            Map<String,Book> bookset = jsonReader.loadWordsBooks().allbooks;
            Iterator<String> bookIterator = bookset.keySet().iterator();
            JSONObject bookJson= new JSONObject();
            while(bookIterator.hasNext()){
                String bookName= bookIterator.next();
                Book currBook = bookset.get(bookName);
                JSONObject deckJson= new JSONObject();
                deckJson.put("default",new JSONObject());
                deckJson.put("realBookName",currBook.realBookName);
                bookJson.put(bookName,deckJson);

            }
            jo.put("books",bookJson);

            PrintWriter pw = new PrintWriter(wordsInBoundPath);
            pw.write(jo.toJSONString());
            pw.flush();
            pw.close();
        }
    }

}
