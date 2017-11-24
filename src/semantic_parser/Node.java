package semantic_parser;

import lexical_analyzer.Tag;

/**
 *
 * @author Lindley and Nícolas
 */
public class Node {
    public Tag type = Tag.VOID;
    //Put class, trueList, falseList and etc here
    
    public Node(){}    
   
    public void setType(Tag type){
        this.type = type;
    }
    
    public Tag getType(){
        return this.type;
    }
}
