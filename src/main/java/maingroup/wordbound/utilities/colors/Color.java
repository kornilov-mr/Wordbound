package maingroup.wordbound.utilities.colors;

public class Color {
    private int R;
    private int G;
    private int B;

    public Color(int R,int G,int B){
        this.R=R;
        this.B=B;
        this.G=G;
    }

    public String toHex(){
        String resultR = Integer.toHexString(R);
        String resultG = Integer.toHexString(G);
        String resultB = Integer.toHexString(B);
        return  "#"+resultR+resultG+resultB;
    }
    public Color mix(Color other,double intensity){
        R= (int) (R*(1-intensity)+other.R*intensity);
        G= (int) (G*(1-intensity)+other.G*intensity);
        B= (int) (B*(1-intensity)+other.B*intensity);
        return new Color(R,G,B);
    }
}
