package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Token {
    public final int tag; //constant that represents the token
    
    public Token(int t){
        tag = t;
    }
    
    public String toString(){
        return "" + tag;
    }
}
