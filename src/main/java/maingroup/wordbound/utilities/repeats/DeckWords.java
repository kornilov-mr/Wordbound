package maingroup.wordbound.utilities.repeats;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class DeckWords {
    public String deckName;
    public String bookName;
    private Vector<WordInBound> deck;
    public DeckIndicator indicator;
    public DeckWords(Vector<WordInBound> deck,String deckName,String bookName){
        this.deck=deck;
        this.deckName=deckName;
        this.bookName=bookName;
        ShowOrder();

    }
    public void ShowOrder(){
        Collections.sort(deck);
    }
    public WordInBound getNextWord(){

        long time= System.currentTimeMillis();
        WordInBound firstWord=deck.getFirst();
        if(time<firstWord.nextrepeat){
            return new WordInBound(-1,-1,firstWord.deck,"-1","-1","-1",0);
        }else{
            return deck.getFirst();
        }

    }
    public void getGoodWord(){
        deck.getFirst().getGood();
        ShowOrder();
    }
    public void setAgainWord(){
        deck.getFirst().setAgain();
        ShowOrder();
    }

    public DeckIndicator getIndicator(){
        int red=0;
        int blue=0;
        int green=0;
        for(int i=0;i<deck.size();i++){
            WordInBound currWord= deck.get(i);
            if(currWord.repeatCount==-1){
                red++;
            }else if(currWord.nextrepeat<System.currentTimeMillis()){
                blue++;
            }else{
                green++;
            }
        }
        DeckIndicator indicator= new DeckIndicator(red,blue,green);
        return indicator;
    }
    public FlowPane createFlowPaneForDeck(DeckIndicator indicator){
        FlowPane flow = new FlowPane(Orientation.VERTICAL);
        this.indicator=indicator;
        flow.setHgap(200);
        flow.setMaxHeight(45);
        flow.setMaxWidth(800);
        Label deckNameLabel= new Label(this.deckName);
        deckNameLabel.setId("deckNameLabel");
        flow.getChildren().add(deckNameLabel);
        flow.getChildren().add(indicator.createIndicator());
        flow.setId("deckFlow");
        return flow;
    }
}
