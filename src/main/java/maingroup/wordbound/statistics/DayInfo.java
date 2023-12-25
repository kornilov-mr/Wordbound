package maingroup.wordbound.statistics;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DayInfo {
    private final DateTimeFormatter dtfYear = DateTimeFormatter.ofPattern("yyyy");
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final DateTimeFormatter dtfDay = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String dtfTime;
    public Map<String, Integer> info = new HashMap<>();
    public DayInfo(String dtfTime, int totalWordRead, int newWords, int wordRepeated){
        this.dtfTime=dtfTime;
        info.put("totalWordRead",totalWordRead);
        info.put("newWords",newWords);
        info.put("wordRepeated",wordRepeated);
    }
    public DayInfoVisualizer getVisualizer() {
        System.out.println(dtfTime);
        LocalDateTime dateTime = LocalDateTime.parse(dtfTime.replaceAll("/","-")+" 00:00",dtf);

        int year = Integer.parseInt(dtfYear.format(dateTime));
        int column = dateTime.get(ChronoField.ALIGNED_WEEK_OF_YEAR);

        Date date = new Date(dtfTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int row =calendar.get(Calendar.DAY_OF_WEEK);

        return new DayInfoVisualizer(year,column-1,row-1);
    }
}
