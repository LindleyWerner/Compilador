package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Token {

    protected final Tag tag; //constant that represents the token

    public static final Token line_comment = new Token(Tag.LINE_COMMENT);
    public static final Token block_comment = new Token(Tag.BLOCK_COMMENT);

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

    public static final Token INVALID = new Token(Tag.ERROR_CARACTER_INVALIDO);

    public Token(Tag t) {
        tag = t;
    }

    public Tag getTag() {
        return this.tag;
    }

    @Override
    public String toString() {
        if (tag.getName().equals("")) {
            return "<" + tag.name() + ">";
        } else {
            return "<" + tag.name() + " (" + tag.getName() + ")>";
        }
    }

    public Token getSimulatedToken() {
        return Token.INVALID;
    }

    public int getErrorLine() {
        return -1;
    }

    public String getLexeme() {
        return tag.getName();
    }
}
