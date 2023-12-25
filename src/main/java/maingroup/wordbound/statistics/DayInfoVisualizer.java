package maingroup.wordbound.statistics;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import maingroup.wordbound.Controllers.statisticsScene.OnHoverPaneController;
import maingroup.wordbound.Wordbound;
import maingroup.wordbound.utilities.colors.Color;

import java.io.IOException;

public class DayInfoVisualizer {

    private final Color emptyColor= new Color(221,221,221);
    private final Color fullColor= new Color(71,168,93);
    public Popup translationNote = new Popup();
    public int year;
    public int column;
    public int row;
    public boolean isOnCard;
    private boolean isOnNote;
    public DayInfoVisualizer(int year, int column, int row){
        this.year= year;
        this.column=column;
        this.row=row;
    }
    public AnchorPane getPane(double intensity, int repeats, String dtfTime, String type, GridPane grid) throws IOException {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(8,8);
        Color colorIntensity = emptyColor.mix(fullColor,intensity);
        pane.setStyle("-fx-background-color:"+colorIntensity.toHex()+";");

        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/statisticsScene/onHoverDayInformation.fxml"));
        AnchorPane dayInformationNote= fxmlLoader.load();

        OnHoverPaneController controller= fxmlLoader.getController();
        controller.setDate(dtfTime,repeats,type);


        translationNote.getContent().add(dayInformationNote);
        pane.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Bounds bnds = grid.localToScreen(grid.getLayoutBounds());
                double x = bnds.getMinX();
                double y = bnds.getMinY();
                translationNote.show(grid, x+column*15+5,y+row*15+5);
            }
        });
        pane.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                translationNote.hide();
            }
        });
        return pane;
    }

}
