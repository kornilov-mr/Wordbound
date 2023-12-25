package maingroup.wordbound.Controllers.statisticsScene;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;

public class OnHoverPaneController {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @FXML
    private Label indicatorLabel;
    @FXML
    private Label dateLabel;
    public void setDate(String date, int repeats,String type){
        LocalDate day = LocalDate.parse(date, dtf);
        dateLabel.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(day));
        indicatorLabel.setText(type+": "+ String.valueOf(repeats));
    }

}
