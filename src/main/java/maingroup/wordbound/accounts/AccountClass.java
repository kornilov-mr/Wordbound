package maingroup.wordbound.accounts;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Objects;

public class AccountClass {
    public static long fontSize;

    private final String userDataPath= new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\userInfo\\userGenerallData.json";
    public AccountClass() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(userDataPath));
        JSONObject jo = (JSONObject) obj;
        this.fontSize= (long) jo.get("fontSize");

    }
    public void updateJson() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(userDataPath));
        JSONObject jo = (JSONObject) obj;
        jo.remove("fontSize");
        jo.put("fontSize",this.fontSize);
        PrintWriter pw = new PrintWriter(userDataPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
}
