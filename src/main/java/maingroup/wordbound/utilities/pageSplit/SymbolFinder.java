package maingroup.wordbound.utilities.pageSplit;

public class SymbolFinder {
    private char symbol;
    private String text;
    public int curr_index=-1;
    private int textLenght;
    public SymbolFinder(char symbol,String text){
        this.symbol=symbol;
        this.text = text;
        this.textLenght=text.length();
    }
    public int nextSymbol() {
        this.curr_index+=1;
        while (curr_index < textLenght && text.charAt(curr_index) != symbol) {
            this.curr_index+=1;
        }
        if (curr_index >= textLenght){
            return -1;
        }

        return curr_index;
    }
    public int prefSymbol() {
        this.curr_index-=1;

        while (curr_index >= 0 && text.charAt(curr_index) != symbol) {
            this.curr_index-=1;
        }
        if (curr_index <0){
            return -1;
        }

        return curr_index;
    }
}
