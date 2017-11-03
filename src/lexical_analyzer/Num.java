package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Num extends Token {

    public final int value;

    public Num(int value) {
        super(Tag.NUM);
        this.value = value;
    }

    public String toString() {
        return "<" + tag.name() + " (\"" + value + "\")>";
    }

}
