package maingroup.wordbound.utilities.books;

import javafx.scene.layout.FlowPane;
import javafx.util.Pair;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
public class BookSet {
    private Map<String,Book> allbooks = new HashMap<>();
    public BookSet(){
    }
    public void addBook(String storageBooknName,Book book){
        allbooks.put(storageBooknName,book);
    }
    public void UpdateLastTime(String storageBooknName) throws IOException, ParseException {
        allbooks.get(storageBooknName).upDateTime();
    }
    public Book getBook(String storageBooknName){
        return allbooks.get(storageBooknName);
    }
    public Vector<Pair<Long,Book>> GetSortedBytime(){
        Vector<Pair<Long,Book>> booksOrder=new Vector<>();
        Iterator<String> booknamesIterator= allbooks.keySet().iterator();
        while(booknamesIterator.hasNext()){
            String key= booknamesIterator.next();
            Book currBook= allbooks.get(key);
            booksOrder.add(new Pair<>(currBook.timeLastSeen,currBook));
        }
        Collections.sort(booksOrder,new Sortbytime());
        return booksOrder;
    }
}
class Sortbytime implements Comparator<Pair<Long,Book>> {
    public int compare(Pair<Long,Book> p1, Pair<Long,Book> p2) {
        if(p1.getKey()<p2.getKey()){
            return 1;
        }else if(p1.getKey()>p2.getKey()){
            return -1;
        }else{
            return 0;
        }
    }
}