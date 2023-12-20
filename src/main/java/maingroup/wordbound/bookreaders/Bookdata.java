package maingroup.wordbound.bookreaders;

import org.json.simple.JSONObject;

public class Bookdata {
    private String bookPath;
    private String charset;
    public String bookName;
    private String realBookName;
    private String autor;
    private String bookdirPath;
    private String coverPath;
    public Bookdata(String bookPath, String charset,String realBookName,String autor,String bookdirPath,String coverPath,String bookName ){
        this.bookPath=bookPath;
        this.charset=charset;
        this.realBookName=realBookName;
        this.autor=autor;
        this.bookdirPath=bookdirPath;
        this.coverPath=coverPath;
        this.bookName=bookName;
    }
    public JSONObject createJsondata() {
        JSONObject info = new JSONObject();
        info.put("name", this.bookName);
        info.put("author", this.autor);
        info.put("realBookName", this.realBookName);
        info.put("bookPath", this.bookPath);
        info.put("dirPath", this.bookdirPath);
        info.put("charset", this.charset);
        info.put("timeLastSeen", System.currentTimeMillis());
        info.put("lastPage", 0);
        info.put("lastDeck","default");
        info.put("pullContext",0);
        info.put("coverPath",this.coverPath);
        return info;
    }
}
