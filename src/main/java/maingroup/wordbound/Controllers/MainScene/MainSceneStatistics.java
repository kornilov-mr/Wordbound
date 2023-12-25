package maingroup.wordbound.Controllers.MainScene;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import maingroup.wordbound.accounts.AccountClass;
import maingroup.wordbound.statistics.StatisticsAnalysis;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class MainSceneStatistics {

    @FXML
    private GridPane wordReadStatisticsGrid;
    @FXML
    private GridPane wordEnconteredStatisticsGrid;
    @FXML
    private GridPane wordRepeatStatisticsGrid;
    private AccountClass account;
    public void loadAccount(AccountClass account){
        this.account=account;
    }
    public void loadGrid() throws IOException, ParseException {
        StatisticsAnalysis statisticsAnalysis = new StatisticsAnalysis();
        statisticsAnalysis.loadStatisticsInGrid(wordReadStatisticsGrid,"totalWordRead");
        statisticsAnalysis.loadStatisticsInGrid(wordEnconteredStatisticsGrid,"newWords");
        statisticsAnalysis.loadStatisticsInGrid(wordRepeatStatisticsGrid,"wordRepeated");
    }
}
