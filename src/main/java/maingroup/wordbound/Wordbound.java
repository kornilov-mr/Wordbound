package maingroup.wordbound;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Wordbound extends Application {

    public String jsonBookPath=new File("").getAbsolutePath()+"\\src\\main\\resources\\maingroup\\wordbound\\books\\bookinfo.json";

    @Override
    public void start(Stage stage) throws IOException, ParseException {
        initialize();
        FXMLLoader fxmlLoader = new FXMLLoader(Wordbound.class.getResource("FXML/MainScene/mainScene.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Wordbound");
        stage.setScene(scene);
        stage.show();
    }
    private void initialize() throws IOException, ParseException {
        File f = new File(jsonBookPath);
        if(!f.exists()) {
            CreateBookJson();
        }
    }
    private void CreateBookJson() throws IOException, ParseException {
        try {
            File myObj = new File(jsonBookPath);
            if (myObj.createNewFile()) {
                System.out.println("json file for books was created");
            } else {
                System.out.println("json file for books is already present");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
//        Object obj = new JSONParser().parse(new FileReader(jsonBookPath));
//        JSONObject jo = (JSONObject) obj;

        JSONObject jo = new JSONObject();
        jo.put("bookCount",0);
        jo.put("wordCount",0);
        jo.put("books",new JSONArray());


        PrintWriter pw = new PrintWriter(jsonBookPath);
        pw.write(jo.toJSONString());
        pw.flush();
        pw.close();
    }
    public static void main(String[] args) {
        launch();
    }
}