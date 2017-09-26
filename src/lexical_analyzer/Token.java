package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Token {

    public final Tag tag; //constant that represents the token

    public static final Token program = new Token(Tag.PROGRAM);
    public static final Token end = new Token(Tag.END);
    public static final Token iF = new Token(Tag.IF);
    public static final Token then = new Token(Tag.THEN);
    public static final Token elsE = new Token(Tag.ELSE);
    public static final Token dO = new Token(Tag.DO);
    public static final Token whilE = new Token(Tag.WHILE);
    public static final Token scan = new Token(Tag.SCAN);
    public static final Token print = new Token(Tag.PRINT);
    public static final Token string = new Token(Tag.STRING);
    public static final Token inT = new Token(Tag.INT);
    
    

    public static final Token line_comment = new Token(Tag.LINE_COMMENT);
    public static final Token block_comment = new Token(Tag.BLOCK_COMMENT);
    //public static final Token open_block_comment = new Token(Tag.OPEN_BLOCK_COMMENT);
    //public static final Token close_block_comment = new Token(Tag.CLOSE_BLOCK_COMMENT);

    public static final Token open_paren = new Token(Tag.OPEN_PAREN);
    public static final Token close_paren = new Token(Tag.CLOSE_PAREN);
    public static final Token dot_comma = new Token(Tag.DOT_COMMA);
    public static final Token comma = new Token(Tag.COMMA);
    public static final Token assing = new Token(Tag.ASSIGN);
    //public static final Token open_aspas = new Token(Tag.OPEN_ASPAS);
    //public static final Token close_aspas = new Token(Tag.CLOSE_ASPAS);

    public static final Token plus = new Token(Tag.PLUS);
    public static final Token minus = new Token(Tag.MINUS);
    public static final Token div = new Token(Tag.DIV);
    public static final Token mult = new Token(Tag.MULT);

    public static final Token eq = new Token(Tag.EQ);
    public static final Token gt = new Token(Tag.GT);
    public static final Token ge = new Token(Tag.GE);
    public static final Token lt = new Token(Tag.LT);
    public static final Token le = new Token(Tag.LE);
    public static final Token ne = new Token(Tag.NE);
    public static final Token or = new Token(Tag.OR);
    public static final Token and = new Token(Tag.AND);
    public static final Token not = new Token(Tag.NOT);

    public static final Token error = new Token(Tag.ERROR);

    public Token(Tag t) {
        tag = t;
    }

    @Override
    public String toString() {
        if (tag.getName() != "") {
            return "<" + tag.name() + " (" + tag.getName() + ")>";
        } else {
            return "<" + tag.name() + ">";
        }
    }

}
