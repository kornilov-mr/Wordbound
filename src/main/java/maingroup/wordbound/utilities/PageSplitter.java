package maingroup.wordbound.utilities;

import javafx.scene.control.TreeItem;
import javafx.util.Pair;
import maingroup.wordbound.bookreaders.Fb2Reader;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PageSplitter {

    private AffineTransform affinetransform = new AffineTransform();
    private FontRenderContext frc = new FontRenderContext(affinetransform, true, true);
    private SymbolFinder openFinder;
    private SymbolFinder closeFinder;
    private int openIndex; //Indexes between < and > for targeting tag
    private int closeIndex; //Indexes between < and > for targeting tag
    private int paneWidth;
    private Vector<String> tagspattern = new Vector<String>();
    private double hidthOfOneLine;
    private int paneHigth;
    private String text;
    private Map<String,Font> fonts;
    private int pageCount = 0;
    private int lineCount=0;
    private Vector<Pair<String, String>> curr_page;
    private int prefLineCount=0;
    private int nextLineCount=0;
    private long charCount=0;
    private long charCountPoint=0;
    private Vector<Pair<String,String>> lines;

    public Map<String, String> tagsExist = Stream.of(new String[][]{
            {"p", "p"},
            {"body", "b"},
            {"title", "t"},
            {"empty-line /", "e"},
            {"subtitle", "s"},
            {"section", "h"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public PageSplitter(Fb2Reader bookReader,Map<String,Font> FontsTotag) {
        this.paneHigth = 450;
        this.paneWidth = 550;
        this.text = bookReader.getText();
        this.openFinder = new SymbolFinder('<', this.text);
        this.closeFinder = new SymbolFinder('>', this.text);

        //set the current closeIndex and openIndex to the first tag in the line
        this.closeIndex = closeFinder.nextSymbol();
        this.openIndex = openFinder.nextSymbol();

        this.fonts = FontsTotag;
        hidthOfOneLine = (double) (FontsTotag.get("p").getStringBounds(" ", frc).getHeight() * 1.5);
        this.lines= getLines();
    }

    private String getSubString(int l, int r) {
        l += 1;
        if (r - l == -1) {
            return new String("");
        }
        char[] dst = new char[r - l];
        text.getChars(l, r, dst, 0);
        return new String(dst);
    }

    private Vector<Pair<String,String>> splitTextOnLine(String textInTag,Vector<String> tags) {

        String curr_text = "";
        int CurrTextWidth = 0;

        String[] words = textInTag.split(" ");
        Vector<Pair<String,String>> lines = new Vector<Pair<String,String>>();
        String currTag=null;
        int wordscount = words.length;
        if(tags.size()<=1){
            currTag="s";
        }else{
            currTag=tags.get(1);
        }
        Font currFont=fonts.get(currTag);

        for (int i = 0; i < wordscount; i++) {
            int currWordWidth = (int) (currFont.getStringBounds(words[i], frc).getWidth());
            //if width of the text too lange
            if (CurrTextWidth + currWordWidth > this.paneWidth) {

                lines.add(new Pair<>(curr_text,currTag));

                curr_text = words[i];
                CurrTextWidth = currWordWidth;
            } else {

                curr_text += " " + words[i];
                CurrTextWidth += currWordWidth;

            }
        }
        lines.add(new Pair<>(curr_text,currTag));
        return lines;
    }


    public Vector<Pair<String,String>> getPrefPage() {
        Vector<Pair<String,String>> current= new Vector<Pair<String,String>>();
        double highCount=0;
        lineCount=prefLineCount;
        nextLineCount=lineCount+1;

        while(highCount<=paneHigth&&lineCount<lines.size()&&lineCount>=0){
            String curr_tag=lines.get(lineCount).getValue();
            double currLineHigth=0;
            if(Objects.equals(lines.get(lineCount).getKey(),new String())){
                 currLineHigth=(double) (fonts.get(curr_tag).getStringBounds("", frc).getHeight() );
            }else{
                 currLineHigth=(double) (fonts.get(curr_tag).getStringBounds("", frc).getHeight() * 1.5);

            }
            if(highCount+currLineHigth>paneHigth){
                break;
            }
            highCount+=currLineHigth;
            current.add(lines.get(lineCount));
            charCount-=lines.get(lineCount).getKey().length();
            lineCount-=1;
        }
        Vector<Pair<String,String>> page= new Vector<>();
        for(int i=current.size()-1;i>=0;i--){
            page.add(current.get(i));
        }
        pageCount-=1;
        charCountPoint=charCount;

        curr_page=page;
        prefLineCount=lineCount;
        return page;
    }
    public Vector<Pair<String,String>> getNextPage() {
        Vector<Pair<String,String>> current= new Vector<Pair<String,String>>();
        double highCount=0;
        lineCount=nextLineCount;
        charCountPoint=charCount;
        prefLineCount=lineCount-1;
        while(highCount<=paneHigth&&lineCount<lines.size()){
            String curr_tag=lines.get(lineCount).getValue();
            double currLineHigth=(double) (fonts.get(curr_tag).getStringBounds(" ", frc).getHeight() *1.5);
            if(highCount+currLineHigth>paneHigth){
                break;
            }
            highCount+=currLineHigth;
            current.add(lines.get(lineCount));
            charCount+=lines.get(lineCount).getKey().length();
            lineCount+=1;
        }
        charCount+=1;
        pageCount+=1;
        curr_page=lines;
        nextLineCount=lineCount;

        return current;

    }

    public Vector<Pair<String, String>> setFontSizeInText(Map<String,Font> newFonts){
        this.fonts=newFonts;
        long currCharCount=0;
        int lineCounts=0;
        this.lineCount=0;
        this.openFinder = new SymbolFinder('<', this.text);
        this.closeFinder = new SymbolFinder('>', this.text);

        //set the current closeIndex and openIndex to the first tag in the line
        this.closeIndex = closeFinder.nextSymbol();
        this.openIndex = openFinder.nextSymbol();
        this.lines=getLines();
        System.out.println(charCountPoint);
        while(currCharCount<charCountPoint){
            currCharCount+=lines.get(lineCounts).getKey().length();
            lineCounts+=1;
        }
        if(lineCounts!=0){
            currCharCount-=lines.get(lineCounts-1).getKey().length()-1;
        }
        nextLineCount=lineCounts;
        charCount=currCharCount;
        return getNextPage();

    }
    public Vector<Pair<String, String>> getPageByN(int pageN){
        int steps=Math.abs(pageN-pageCount)-1;
        Vector<Pair<String,String>> page = new Vector<>();
        if(pageCount<pageN){
            for(int i=0;i<steps;i++){
                pageCount+=1;

                page=getNextPage();
            }
        }else if(pageCount>pageN){
            for(int i=0;i<steps;i++){
                page=getPrefPage();
                pageCount-=1;
            }
        }
        return page;
    }

    private String createPageFromLines(Vector<String> lines, boolean reverse) {
        String page = "";
        if (reverse) {
            for (int i = 0; i < (int) (this.paneHigth / hidthOfOneLine); i++) {
                page += lines.removeLast() + "\n";
            }
        } else {
            for (int i = 0; i < (int) (this.paneHigth / hidthOfOneLine); i++) {
                page += lines.removeFirst() + "\n";
            }
        }
        return page;
    }

    public Vector<Pair<String,String>> getLines() {
        Vector<Pair<String,String>> lines= new Vector<>();
        while (openIndex != -1) {

            if (text.charAt(openIndex + 1) != '/') {
                String tagShort = tagsExist.get(getSubString(openIndex, closeIndex));
                if (tagShort == "e") {
                    lines.add(new Pair<>(new String(),"p"));
                } else {
                    tagspattern.add(tagShort);

                }
            } else {
                tagspattern.remove(tagspattern.size() - 1);
                lines.add(new Pair<>(new String(),"p"));
            }

            openIndex = this.openFinder.nextSymbol();
            if(openIndex==-1){
                break;
            }
            String textInTag = getSubString(closeIndex, openIndex);
            if (!Objects.equals(textInTag,"")){
                Vector<Pair<String,String>> lineInTag = splitTextOnLine(textInTag,tagspattern);

                int pagesAddition = lineInTag.size();
                lines.addAll(lineInTag);

            }
            closeIndex = this.closeFinder.nextSymbol();
        }

        return lines;
    }
}