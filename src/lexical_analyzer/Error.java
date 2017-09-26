package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Error extends Token{
    private int line = -1;
    private String expected;
    
    public Error(int line, String expected){
        super(Tag.ERROR);
        this.line = line;
        this.expected = expected;
    }
        
    public String toString(){
        if(expected == " "){
            return "<" + tag.name() + " (linha: " + line + ", caracter não esperado)>";
        }else{
            return "<" + tag.name() + " (linha: " + line + ", esperado: \"" + expected + "\")>";
        }
    }
    
}
