package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Error extends Token{
    private int line = -1;
    private String lexeme;
    private boolean expected;
    
    public Error(int line, String lexeme, boolean expected){
        super(Tag.ERROR);
        this.line = line;
        this.lexeme = lexeme;
        this.expected = expected;
    }
        
    public String toString(){
        if(expected){
            return "<" + tag.name() + " (linha: " + line + ", esperado: \"" + lexeme + "\")>";
        }else{
            return "<" + tag.name() + " (linha: " + line + ", caracter inesperado: \"" + lexeme + "\")>";
        }
    }
    
}
