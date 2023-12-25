package maingroup.wordbound.statistics;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class StatisticController {

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private final String pathToStatisticJson = new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\statisticsInfo\\dailyReport.json";
    public long totalWordRead;
    public long newWords;
    public long wordRepeated;
    private String dtfTime;
    public StatisticController() throws IOException, ParseException {
        LocalDateTime data = LocalDateTime.now();
        dtfTime=dtf.format(data);


        Object obj = new JSONParser().parse(new FileReader(pathToStatisticJson));
        JSONObject jo = (JSONObject) obj;
        if(jo.size()==0|| jo.get(dtfTime)==null){
            fillBlanks(jo);
            PrintWriter pw = new PrintWriter(pathToStatisticJson);
            pw.write(jo.toJSONString());
            pw.flush();
            pw.close();
        }else{
            readData((JSONObject) jo.get(String.valueOf(dtfTime)));
        }
    }
    private void initializeNewData(){
        totalWordRead=0;
        wordRepeated=0;
        newWords=0;
    }
    private void fillBlanks(JSONObject jo){
        LocalDateTime data = LocalDateTime.now();
        String Time=dtf.format(data);
        while (jo.get(Time)==null){
            jo.put(Time,createEmptyJson(Time));
            data=data.minusDays(1);
            Time=dtf.format(data);
        }
    }
    private void readData(JSONObject jo){
        totalWordRead= (Long) jo.get("totalWordRead");
        wordRepeated= (Long) jo.get("wordRepeated");
        newWords= (Long) jo.get("newWords");
        dtfTime= (String) jo.get("dtfTime");
    }
    public void saveStatistic() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(pathToStatisticJson));
        JSONObject jo = (JSONObject) obj;
        jo.remove(String.valueOf(dtfTime));
        jo.put(String.valueOf(dtfTime), createJsonFromClass());
        PrintWriter pw = new PrintWriter(pathToStatisticJson);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    private JSONObject createJsonFromClass(){
        JSONObject jo = new JSONObject();
        jo.put("totalWordRead",totalWordRead);
        jo.put("wordRepeated",wordRepeated);
        jo.put("newWords",newWords);
        jo.put("dtfTime",dtfTime);
        return jo;
    }
    private JSONObject createEmptyJson(String time){
        JSONObject jo = new JSONObject();
        jo.put("totalWordRead",0);
        jo.put("wordRepeated",0);
        jo.put("newWords",0);
        jo.put("dtfTime",time);
        return jo;
    }

}
