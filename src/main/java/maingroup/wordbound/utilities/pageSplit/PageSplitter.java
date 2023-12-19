package maingroup.wordbound.utilities.pageSplit;

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
    public int maxPage=0;
    public Vector<Pair<Vector<Pair<String,String>>,Integer>> pages = new Vector<>();
    private Vector<String> tagspattern = new Vector<String>();
    private int paneHigth;
    private String text;
    private Map<String,Font> fonts;
    public int pageCount =0;
    private Vector<Pair<String, String>> curr_page;
    private long charCountPoint=0;
    public Map<String, String> tagsExist = Stream.of(new String[][]{
            {"p", "p"},
            {"body", "b"},
            {"title", "t"},
            {"empty-line /", "e"},
            {"subtitle", "s"},
            {"section", "h"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));

    public PageSplitter(Fb2Reader bookReader,Map<String,Font> FontsTotag) {
        this.paneHigth = 400;
        this.paneWidth = 600;
        this.text = bookReader.getText();
        this.openFinder = new SymbolFinder('<', this.text);
        this.closeFinder = new SymbolFinder('>', this.text);

        this.closeIndex = closeFinder.nextSymbol();
        this.openIndex = openFinder.nextSymbol();

        this.fonts = FontsTotag;
        this.pages = getPages();
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
        int CurrTextWidth = 20;

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
                CurrTextWidth = currWordWidth+20;
            } else {

                curr_text += " " + words[i];
                CurrTextWidth += currWordWidth;

            }
        }
        lines.add(new Pair<>(curr_text,currTag));
        return lines;
    }


    public Vector<Pair<String,String>> getPrefPage() {
        pageCount-=1;
        charCountPoint=pages.get(pageCount).getValue();
        return pages.get(pageCount).getKey();
    }
    public Vector<Pair<String,String>> getNextPage() {
        pageCount+=1;
        charCountPoint=pages.get(pageCount).getValue();
        return pages.get(pageCount).getKey();
    }
    public Vector<Pair<String, String>> getPageByN(long pageN){
        pageCount=(int)pageN;
        charCountPoint=pages.get(pageCount).getValue();
        return pages.get(pageCount).getKey();
    }

    public Vector<Pair<String, String>> setFontSizeInText(Map<String,Font> newFonts){
        this.fonts=newFonts;
        this.openFinder = new SymbolFinder('<', this.text);
        this.closeFinder = new SymbolFinder('>', this.text);
        this.closeIndex = closeFinder.nextSymbol();
        this.openIndex = openFinder.nextSymbol();
        this.pages= getPages();
        int neeedPage=0;
        while(pages.get(neeedPage).getValue()<charCountPoint) neeedPage++;
        pageCount=neeedPage-1;
        return pages.get(neeedPage-1).getKey();
    }
    public Vector<Pair<Vector<Pair<String,String>>,Integer>> getPages(){
        Vector<Pair<String,String>> lines = getLines();
        Vector<Pair<Vector<Pair<String,String>>,Integer>> pages= new Vector<>();
        double highCount=0;
        int charCount=0;
        Iterator<Pair<String,String>> linesInterator= lines.iterator();
        while(linesInterator.hasNext()){
            Vector<Pair<String,String>> currPage = new Vector<>();
            highCount=0;
            while(highCount<=paneHigth&&linesInterator.hasNext()){
                Pair<String,String> currLine= linesInterator.next();
                String curr_tag=currLine.getValue();
                double currLineHigth=(double) (fonts.get(curr_tag).getStringBounds(" ", frc).getHeight() *1.5);

                highCount+=currLineHigth;
                charCount+=currLine.getKey().length();
                currPage.add(currLine);
                if(highCount+currLineHigth>paneHigth){
                    break;
                }
            }
            pages.add(new Pair<>(currPage,charCount));
        }
        maxPage=pages.size();

        return pages;
    }
    public Vector<Pair<String,String>> getLines() {
        double hightCount=0;
        Vector<Pair<String,String>> lines= new Vector<>();
        while (openIndex != -1) {

            if (text.charAt(openIndex + 1) != '/') {
                String tagShort = tagsExist.get(getSubString(openIndex, closeIndex));
                if (tagShort == "e") {
                    lines.add(new Pair<>(new String(),"p"));
                    double currLineHigth=(double) (fonts.get("p").getStringBounds(" ", frc).getHeight() *1.5);
                    hightCount+=currLineHigth;
                } else {
                    tagspattern.add(tagShort);

                }
            } else {
                tagspattern.remove(tagspattern.size() - 1);
                lines.add(new Pair<>(new String("/n"),"p"));
                double currLineHigth=(double) (fonts.get("p").getStringBounds(" ", frc).getHeight() *1.5);
                hightCount+=currLineHigth;
            }

            openIndex = this.openFinder.nextSymbol();
            if(openIndex==-1){
                break;
            }
            String textInTag = getSubString(closeIndex, openIndex);
            if (!Objects.equals(textInTag,"")){
                Vector<Pair<String,String>> lineInTag = splitTextOnLine(textInTag,tagspattern);

                int pagesAddition = lineInTag.size();
                double currLineHigth=(double) (fonts.get(lineInTag.get(0).getValue()).getStringBounds(" ", frc).getHeight() *1.5);
                hightCount+=currLineHigth*pagesAddition;
                lines.addAll(lineInTag);

            }
            closeIndex = this.closeFinder.nextSymbol();
        }
        maxPage=(int)hightCount/paneHigth+1;
        return lines;
    }
}