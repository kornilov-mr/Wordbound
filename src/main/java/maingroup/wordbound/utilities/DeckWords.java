package maingroup.wordbound.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class DeckWords {
    public String deckName;
    public String bookName;
    private Vector<WordInBound> deck;
    public String wordsInBoundPath=new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\wordsInBound.json";
    public DeckWords(Vector<WordInBound> deck,String deckName,String bookName){
        this.deck=deck;
        this.deckName=deckName;
        this.bookName=bookName;

    }
    public void ShowOrder(){
        Collections.sort(deck);
    }
    public WordInBound getNextWord(){
        long time= System.currentTimeMillis();
        WordInBound firstWord=deck.getFirst();
        if(time<firstWord.nextrepeat){
            return new WordInBound(-1,-1,firstWord.deck,"-1","-1","-1",0);
        }else{
            return deck.getFirst();
        }

    }
    public void getGoodWord(){
        deck.getFirst().getGood();
        ShowOrder();
    }
    public void setAgainWord(){
        deck.getFirst().setAgain();
        ShowOrder();
    }
    public void saveInJson() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(wordsInBoundPath));
        JSONObject jo = (JSONObject) obj;

        JSONObject books= (JSONObject) jo.get("books");
        Iterator<String> booksIterator = books.keySet().iterator();

        JSONObject decks=(JSONObject) books.get(bookName);
        JSONObject deckJson=(JSONObject) decks.get(deckName);
        deckJson.remove("wordsInbound");
        JSONArray words = new JSONArray();
        for(int i=0;i<deck.size();i++){
            WordInBound word = (WordInBound) deck.get(i);
            words.add(word.toJson());
        }
        deckJson.put("wordsInbound", words);
        PrintWriter pw = new PrintWriter(wordsInBoundPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
}
