package lexical_analyzer;

/**
 *
 * @author Lindley and Nícolas
 */
public class Tag {
    public final static int
            //Reserved words
            PROGRAM = 256,
            END = 257,
            INT = 258,
            STRING = 259,
            IF = 260,
            THEN = 261,            
            ELSE = 262,
            DO = 263,
            WHILE = 264,
            SCAN = 265,
            PRINT = 266,
            
            //Operators
            EQ = 267, // ==            
            GE = 268, // >=            
            LE = 269, // <=
            NE = 230, // !=
            OR = 231, // ||            
            AND = 232, // &&
            
            //Another tokens
            NUM = 233,
            ID = 234;
}
