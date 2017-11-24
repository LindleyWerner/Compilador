package semantic_parser;

import lexical_analyzer.Word;
import lexical_analyzer.Tag;

/**
 *
 * @author Lindley e Nícolas
 */

public class SymbolTableObject {
    private final Word w;
    private Tag type = Tag.VOID;
    
    public SymbolTableObject(Word w, Tag type){
        this.w = w;
        this.type = type;
    }
    
    public Tag getType(){
        return type;
    }
    
    public void setType(Tag type){
        this.type = type;
    }
    
    public Word getWord(){
        return w;
    }
}
