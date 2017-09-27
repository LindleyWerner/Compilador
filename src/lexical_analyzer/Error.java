package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Error extends Token{
    private int line = -1;
    private String lexeme;
    
    public Error(int line, String lexeme, Tag erro){
        super(erro);
        this.line = line;
        this.lexeme = lexeme;
    }
        
    public String toString(){
        if(tag==Tag.ERROR_CARACTER_INESPERADO){
            return "<" + tag.name() + " (linha: " + line + ", esperado: \"" + lexeme + "\")>";
        }else if (tag==Tag.ERROR_CARACTER_INVALIDO){
            return "<" + tag.name() + " (linha: " + line + ", caracter inválido: \"" + lexeme + "\")>";
        }
        return "";
    }
    
}
