package maingroup.wordbound.accounts;

import maingroup.wordbound.utilities.repeats.DeckWords;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;

public class DataHandler {
    AccountClass account;
    public DataHandler(AccountClass account){
        this.account=account;
    }
    public void updateWordsIncoutered(Vector<String> words) throws IOException, ParseException {
        int addCount=0;
        for(int i=0;i<words.size();i++){
            boolean isIn=false;
            for(int j=0;j<account.wordsIncountered.size();j++){
                if(Objects.equals(account.wordsIncountered.get(j),words.get(i))){
                    isIn=true;
                }
            }
            if(!isIn) {
                account.wordsIncountered.add(words.get(i));
                addCount += 1;
            }
        }
    }
    public WordInBound createWord(String originalWord, String wordTranslation,String bookName,String deckName,String context,String key) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime data = LocalDateTime.now();
        return new WordInBound(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                deckName,
                originalWord,
                dtf.format(data),
                wordTranslation,
                -1,
                account.generalldata.lastId,
                key,
                context,
                bookName);
    }
    public int addNewWordToWordInBount(String originalWord, String wordTranslation,String bookName,String deckName,String context){
        System.out.println(account);

        WordInBound wordInBound1 = createWord(originalWord,wordTranslation,bookName,deckName,context,"firstCard");
        WordInBound wordInBound2 = createWord(wordTranslation,originalWord,bookName,deckName,context,"secondCard");

        account.deckInTree.get(bookName).get(deckName).deck.add(wordInBound1);
        account.deckInTree.get(bookName).get(deckName).deck.add(wordInBound2);

        account.generalldata.lastId+=1;
        return (int) account.generalldata.lastId-1;
    }
    public void changeWordInboundDeck(String bookName,String deckNameOld,String deckNameNew,int idWordinBound) throws IOException, ParseException {
        WordInBound word1 = null;
        int iToDelete1=-1;
        WordInBound word2 = null;
        int iToDelete2=-1;
        for(int i=0;i<account.deckInTree.get(bookName).get(deckNameOld).deck.size();i++){
            if(Objects.equals(account.deckInTree.get(bookName).get(deckNameOld).deck.get(i).key,"firstCard") &&account.deckInTree.get(bookName).get(deckNameOld).deck.get(i).id==idWordinBound){
                word1=account.deckInTree.get(bookName).get(deckNameOld).deck.get(i);
                iToDelete1=i;
            }
            if(Objects.equals(account.deckInTree.get(bookName).get(deckNameOld).deck.get(i).key,"secondCard") &&account.deckInTree.get(bookName).get(deckNameOld).deck.get(i).id==idWordinBound){
                word2=account.deckInTree.get(bookName).get(deckNameOld).deck.get(i);
                iToDelete2=i;
            }
        }
        if(iToDelete1>iToDelete2){
            account.deckInTree.get(bookName).get(deckNameOld).deck.remove(iToDelete1);
            account.deckInTree.get(bookName).get(deckNameOld).deck.remove(iToDelete2);
        }else{
            account.deckInTree.get(bookName).get(deckNameOld).deck.remove(iToDelete2);
            account.deckInTree.get(bookName).get(deckNameOld).deck.remove(iToDelete1);
        }
        account.deckInTree.get(bookName).get(deckNameNew).deck.add(word1);
        account.deckInTree.get(bookName).get(deckNameNew).deck.add(word2);
    }
    public void addNewDeckToWordInBoundJson(String deckName,String bookName,String realBookName) throws IOException, ParseException {
        account.deckInTree.get(bookName).put(deckName, new DeckWords(new Vector<>(),deckName,bookName,realBookName));
    }
    public void addNewBookToWordInBoundJson(String bookName,String realBookName) throws IOException, ParseException {
        account.deckInTree.put(bookName, new HashMap<>());

    }
    public void changeWordInbound(String originalWord, String wordTranslation,String bookName,String deckName,int idWordinBound) throws IOException, ParseException {
        int iToChange1=-1;
        int iToChange2=-1;
        for(int i=0;i<account.deckInTree.get(bookName).get(deckName).deck.size();i++){
            if(Objects.equals(account.deckInTree.get(bookName).get(deckName).deck.get(i).key,"firstCard") &&account.deckInTree.get(bookName).get(deckName).deck.get(i).id==idWordinBound){
                iToChange1=i;
            }
            if(Objects.equals(account.deckInTree.get(bookName).get(deckName).deck.get(i).key,"secondCard") &&account.deckInTree.get(bookName).get(deckName).deck.get(i).id==idWordinBound){
                iToChange2=i;
            }
        }
        account.deckInTree.get(bookName).get(deckName).deck.get(iToChange1).originalWord=originalWord;
        account.deckInTree.get(bookName).get(deckName).deck.get(iToChange1).wordTranslation=wordTranslation;
        account.deckInTree.get(bookName).get(deckName).deck.get(iToChange2).originalWord=wordTranslation;
        account.deckInTree.get(bookName).get(deckName).deck.get(iToChange2).wordTranslation=originalWord;
    }
}
