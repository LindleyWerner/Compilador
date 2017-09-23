package lexical_analyzer;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lindley and Nícolas
 */
public class Lexer {

    public static int line = 1; //line counter
    public static boolean eof = false;
    private char ch = ' '; //read file character
    private FileReader file;
    private List text;
    private int contraladorText = 0;

    private Hashtable words = new Hashtable();

    //Method to insert reserved words in hashtable
    private void reserve(Word w) {
        // TODO Ver se está correto
        words.put(w.toString(), w); //lexeme is the key to entry into the hashtable

    }

    //Constructor
    public Lexer(String fileName) throws FileNotFoundException {
        try {
            file = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("File '" + fileName + "' not found");
            throw e;
        }

        this.text = new ArrayList<>();
        try {
            readAllFile();
        } catch (IOException ex) {
            Logger.getLogger(Lexer.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Insert reserved words in hashtable
      /*  reserve(new Word("program", Tag.PROGRAM));
        reserve(new Word("end", Tag.END));
        reserve(new Word("int", Tag.INT));
        reserve(new Word("string", Tag.STRING));
        reserve(new Word("if", Tag.IF));
        reserve(new Word("then", Tag.THEN));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("scan", Tag.SCAN));
        reserve(new Word("print", Tag.PRINT));*/
    }

    void readAllFile() throws IOException {
        int c;
        while ((c = file.read()) != -1) {
            text.add(c);
        }
    }

    //Reads next file character
    private void readch() throws IOException {
        int c;
        if (contraladorText < text.size()) {
            ch = Character.toChars((int) text.get(contraladorText))[0];
            contraladorText++;
        } else {
            eof = true;
        }
    }

    //Reads next file character and check if it is equal to c
    private boolean readch(char c) throws IOException {
        readch();
        if (ch != c) {
            contraladorText--;
            return false;
        }
        ch = ' ';
        return true;
    }

    //Recognizes tokens
    public Token scan() throws IOException {
        //Ignore delimiters
        for (;; readch()) {
            if (eof) {
                return null;
            } else if (ch == ' ' || ch == '\t'
                    || ch == Character.toChars(65279)[0] || ch == '\r'
                    || ch == '\b') {
                continue;
            } else if (ch == '\n') {
                line++; //line counter
            } else {
                break;
            }
        }

        //comments and Arithmetic  Operators
        switch (ch) {
            case '/':
                if (readch('/')) {
                    return Token.line_comment;
                } else if (readch('*')) {
                    return Token.open_block_comment;
                } else {
                    return Token.div;
                }
            case '*':
                if (readch('/')) {
                    return Token.close_block_comment;
                } else {
                    return Token.mult;
                }
            case '+':
                ch=' ';
                return Token.plus;
            case '-':
                ch=' ';
                return Token.minus;
        }

        //simbols
        switch (ch) {
            case '(':
                ch=' ';
                return Token.open_paren;
            case ')':
                ch=' ';
                return Token.close_paren;
            case ';':
                ch=' ';
                return Token.dot_comma;
            case ',':
                ch=' ';
                return Token.comma;
            case '“':
                ch=' ';
                return Token.open_aspas;
            case '”':
                ch=' ';
                return Token.close_aspas;
        }

        //Operators
        switch (ch) {
            case '=':
                if (readch('=')) {
                    return Token.eq;
                } else {
                    return Token.assing;
                }
            case '>':
                if (readch('=')) {
                    return Token.ge;
                } else {
                    return Token.gt;
                }
            case '<':
                if (readch('=')) {
                    return Token.le;
                } else {
                    return Token.lt;
                }
            case '!':
                if (readch('=')) {
                    return Token.ne;
                } else {
                    return Token.not;
                }
            case '|':
                if (readch('|')) {
                    return Token.or;
                }
            case '&':
                if (readch('&')) {
                    return Token.and;
                }
        }

        //Numbers
        if (Character.isDigit(ch)) {
            int value = 0;
            do {
                value = 10 * value + Character.digit(ch, 10);
                readch();
            } while (Character.isDigit(ch));
            return new Num(value);
        }

        //Reserved words
        switch (ch) {
            case 'p':
                if (readch('r')) {
                    if (readch('o') && readch('g') && readch('r') && readch('a') && readch('m')) {
                        return Token.program;
                    } else if (readch('i') && readch('n') && readch('t')) {
                        return Token.print;
                    }
                }
            case 'e':
                if (readch('n') && readch('d')) {
                    return Token.end;
                }
                if (readch('l') && readch('s') && readch('e')) {
                    return Token.elsE;
                }
            case 'i':
                if (readch('f')) {
                    return Token.iF;
                } else if (readch('n') && readch('t')) {
                    return Token.inT;
                }
            case 't':
                if (readch('h') && readch('e') && readch('n')) {
                    return Token.then;
                }
            case 'd':
                if (readch('o')) {
                    return Token.dO;
                }
            case 'w':
                if (readch('h') && readch('i') && readch('l') && readch('e')) {
                    return Token.whilE;
                }
            case 's':
                if (readch('c') && readch('a' ) && readch('n')) {
                    return Token.scan;
                } else if (readch('t') && readch('r') && readch('i') && readch('n') && readch('g')) {
                    return Token.string;
                }
        }

        //Identifiers
        if (Character.isLetter(ch)) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append(ch);
                readch();
            } while (Character.isLetterOrDigit(ch));

            String s = sb.toString();
            Word w = (Word) words.get(s);
            if (w != null) {
                return w; //word already exists in hashtable
            }
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }

        // error
        Word word=new Word(""+ch,Tag.DIFERENTS_TAGS);
        ch = ' ';
        return word;
    }
}
