package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public enum Tag {
    //Reserved words
    PROGRAM("program"),
    END("end"),
    INT("int"),
    STRING("string"),
    IF("if"),
    THEN("then"),
    ELSE("else"),
    DO("do"),
    WHILE("while"),
    SCAN("scan"),
    PRINT("print"),
    
    //comments
    LINE_COMMENT("//"),
    BLOCK_COMMENT("/**/"),
    //OPEN_BLOCK_COMMENT("/*"), 
    //CLOSE_BLOCK_COMMENT("*/"),

    //simbols
    OPEN_PAREN("("), 
    CLOSE_PAREN(")"), 
    DOT_COMMA(";"),
    COMMA(","),
    ASSIGN("="),
    //OPEN_ASPAS("“"),
    //CLOSE_ASPAS("”"), // " 

    // Arithmetic  Operators
    PLUS("+"),
    MINUS("-"), 
    DIV("/"), 
    MULT("*"),

    // Logitacal Operators
    EQ("=="),             
    GT(">"), 
    GE(">="),             
    LT("<"),            
    LE("<="), 
    NE("!="),
    OR("||"),            
    AND("&&"),           
    NOT("!"), 

    //Another tokens
    NUM,
    ID,
    TEXT,
    
    // errors tokens
    ERROR_CARACTER_INVALIDO,
    ERROR_CARACTER_INESPERADO;

    private String name="";
    
    public String getName(){
        return name;
    }
    
    private Tag(String name) {
        this.name = name;
    }

    private Tag() {
    }
}
