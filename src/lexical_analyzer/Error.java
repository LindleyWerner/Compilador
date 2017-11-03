package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Error extends Token{
    private int line = -1;
    private final String lexeme;
    private final Token tok;
    
    public Error(int line, String lexeme, Tag erro, Token t){
        super(erro);
        this.line = line;
        this.lexeme = lexeme;
        this.tok = t;
    }
        
    @Override
    public String toString(){
        if(tag==Tag.ERROR_CARACTER_INESPERADO){
            return "<" + tag.name() + " (linha: " + line + ", esperado: \"" + lexeme + "\")>";
        }else if (tag==Tag.ERROR_CARACTER_INVALIDO){
            return "<" + tag.name() + " (linha: " + line + ", caracter inválido: \"" + lexeme + "\")>";
        }
        return "";
    }
    
    @Override
    public Token getSimulatedToken(){
        return tok;
    }
    
    @Override
    public int getErrorLine(){
        return line;
    }
    
    @Override
    public String getLexeme(){
        return lexeme;
    }
}
