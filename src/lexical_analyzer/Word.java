package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Word extends Token{
    private String lexeme = "";
    
    public Word(String s, Tag tag){
        super(tag);
        lexeme = s;
    }
        
    @Override
    public String toString(){
        return "<" + tag.name() + " (\"" + lexeme + "\")>";
    }
    
    @Override
    public String getLexeme(){
        return lexeme;
    }
    
}
