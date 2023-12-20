package maingroup.wordbound.utilities.repeats;

import javafx.util.Pair;
import maingroup.wordbound.utilities.books.Book;
import org.json.simple.JSONObject;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordInBound{
    private final Map<Long, Long> repeats= Stream.of(new long[][]{
            {0, 20*60*1000},
            {1, 24*60*60*1000},
            {2, 3*24*60*60*1000},
            {3, 7*24*60*60*1000},
            {4, 3*7*24*60*60*1000},
            {5, 2*4*7*24*60*60*1000},
            {6, 8*4*7*24*60*60*1000},
            {7, 12*4*7*24*60*60*1000},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    public long realTime;
    public long nextrepeat;
    public String deck;
    public String originalWord;
    public String timeInyyyymmdd;
    public String wordTranslation;
    public long id;
    public String key;
    protected long repeatCount;
    public WordInBound(long realTime, long nextrepeat, String deck, String originalWord,String timeInyyyymmdd,String wordTranslation,long repeatCount,long id,String key){
        this.realTime=realTime;
        this.nextrepeat=nextrepeat;
        this.deck=deck;
        this.originalWord=originalWord;
        this.timeInyyyymmdd=timeInyyyymmdd;
        this.wordTranslation=wordTranslation;
        this.repeatCount=repeatCount;
        this.id=id;
        this.key=key;
    }
    public void getGood(){
        repeatCount+=1;
        nextrepeat=System.currentTimeMillis()+repeats.get(repeatCount);
    }
    public void setAgain(){
        if(repeatCount!=-1){
            repeatCount=0;
        }
        nextrepeat=System.currentTimeMillis()+repeats.get(repeatCount);
    }
    public JSONObject toJson(){
        org.json.simple.JSONObject wordInfo= new org.json.simple.JSONObject();
        wordInfo.put("originalWord",originalWord);
        wordInfo.put("wordTranslation",wordTranslation);

        wordInfo.put("time",timeInyyyymmdd);
        wordInfo.put("realTime",realTime);
        wordInfo.put("nextrepeat",nextrepeat);
        wordInfo.put("id",id);
        wordInfo.put("deck",deck);

        wordInfo.put("repeatCount",repeatCount);
        return wordInfo;
    }
}
