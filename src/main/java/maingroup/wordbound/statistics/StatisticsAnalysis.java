package maingroup.wordbound.statistics;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsAnalysis {

    private final String pathToStatisticJson = new File("").getAbsolutePath()+"\\src\\main\\java\\maingroup\\wordbound\\statisticsInfo\\dailyReport.json";
    private Map<String,Long> maxForKeys= new HashMap<>();
    private Vector<DayInfo> dayInfos = new Vector<>();
    private final Map<String, String> ShowForKeys= Stream.of(new String[][]{
            {"newWords", "New words encountered"},
            {"totalWordRead", "Total words you red"},
            {"wordRepeated", "Words repeated"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
    public StatisticsAnalysis() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader(pathToStatisticJson));
        JSONObject jo = (JSONObject) obj;

        Iterator<String> dateiterator = jo.keySet().iterator();
        long totalWordReadMax=1;
        long newWordsMax=1;
        long wordRepeatedMax=1;
        while(dateiterator.hasNext()) {
            String date = dateiterator.next();
            JSONObject info = (JSONObject) jo.get(date);
            long totalWordRead=(Long) info.get("totalWordRead");
            long newWords=(Long) info.get("newWords");
            long wordRepeated=(Long) info.get("wordRepeated");

            dayInfos.add(new DayInfo(date, (int) totalWordRead, (int) newWords, (int) wordRepeated));

            totalWordReadMax=Math.max(totalWordRead,totalWordReadMax);
            newWordsMax=Math.max(newWords,newWordsMax);
            wordRepeatedMax=Math.max(wordRepeated,wordRepeatedMax);
        }

        maxForKeys.put("totalWordRead",totalWordReadMax);
        maxForKeys.put("newWords",newWordsMax);
        maxForKeys.put("wordRepeated",wordRepeatedMax);
    }

    public void loadStatisticsInGrid(GridPane grid,String key) throws IOException {
        String keyToShow= ShowForKeys.get(key);
        for(int i=0;i<dayInfos.size();i++){
            DayInfo currInfo = dayInfos.get(i);
            DayInfoVisualizer visualizer= currInfo.getVisualizer();
            AnchorPane dayPane= visualizer.getPane(
                    currInfo.info.get(key)/maxForKeys.get(key),
                    currInfo.info.get(key),
                    currInfo.dtfTime,
                    keyToShow,
                    grid);
            grid.add(dayPane,visualizer.column,visualizer.row);
        }

    }
}
