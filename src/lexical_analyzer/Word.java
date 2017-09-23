package lexical_analyzer;

import lexical_analyzer.Token;

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
        
    public String toString(){
        return "<" + tag.name() + " (\"" + lexeme + "\")>";
    }
    
}
