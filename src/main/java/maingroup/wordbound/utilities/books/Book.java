package maingroup.wordbound.utilities.books;

import javafx.scene.layout.FlowPane;
import maingroup.wordbound.utilities.repeats.WordInBound;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Book {
    private final String jsonPath = new File("").getAbsolutePath() + "\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";

    public FlowPane content;
    protected String realBookName;
    protected long timeLastSeen;
    protected String StorageBookName;
    public Book(FlowPane content,String StorageBookName,String realBookName,long timeLastSeen){
        this.content=content;
        this.StorageBookName=StorageBookName;
        this.realBookName=realBookName;
        this.timeLastSeen=timeLastSeen;
    }
    protected void upDateTime() throws IOException, ParseException {
        this.timeLastSeen=System.currentTimeMillis();
        Object obj = new JSONParser().parse(new FileReader(this.jsonPath));
        JSONObject jo = (JSONObject) obj;
        JSONObject booksInfo = (JSONObject) jo.get("books");
        JSONObject bookdate= (JSONObject) booksInfo.get(this.StorageBookName);
        bookdate.remove("timeLastSeen");
        bookdate.put("timeLastSeen",System.currentTimeMillis());
        PrintWriter pw = new PrintWriter(this.jsonPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
}
