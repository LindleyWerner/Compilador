package lexical_analyzer;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import semantic_parser.SymbolTableObject;

/**
 *
 * @author Lindley and Nícolas
 */
public class Lexer {

    public static int line; //line counter
    public static int error; //error counter
    public static boolean eof;
    private char ch; //read file character
    private FileReader file;
    private List text;
    private int controladorText;

    private Hashtable words = new Hashtable();

    //Method to insert reserved words in hashtable
    private void reserve(Word w) {
        // TODO Ver se está correto
        SymbolTableObject obj = new SymbolTableObject(w, Tag.VOID);
        words.put(w.getLexeme(), obj); //lexeme is the key to entry into the hashtable

    }

    //Constructor
    public Lexer(String fileName) throws FileNotFoundException {
        line = 1;
        error = 0;
        eof = false;
        ch = ' ';
        controladorText = 0;

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
        reserve(new Word("program", Tag.PROGRAM));
        reserve(new Word("end", Tag.END));
        reserve(new Word("int", Tag.INT));
        reserve(new Word("string", Tag.STRING));
        reserve(new Word("if", Tag.IF));
        reserve(new Word("then", Tag.THEN));
        reserve(new Word("else", Tag.ELSE));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("scan", Tag.SCAN));
        reserve(new Word("print", Tag.PRINT));
    }

    private void readAllFile() throws IOException {
        int c;
        while ((c = file.read()) != -1) {
            text.add(c);
        }
    }

    //Reads next file character
    private void readch() throws IOException {
        int c;
        if (controladorText < text.size()) {
            ch = Character.toChars((int) text.get(controladorText))[0];
            controladorText++;
        } else {
            eof = true;
        }
    }

    //Reads next file character and check if it is equal to c
    private boolean readch(char c) throws IOException {
        readch();
        if (ch != c) {
            controladorText--;
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
                return new Token(Tag.EOF);
            } else if (ch == ' ' || ch == '\t'
                    || ch == Character.toChars(65279)[0] || ch == '\r'
                    || ch == '\b') {
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
                    while (!readch('\n')) {
                        if (eof) {
                            break;
                        }
                        readch();
                    }
                    line++;
                    ch = ' ';
                    return Token.line_comment;
                } else if (readch('*')) {
                    int startLine = line;
                    while (true) {
                        readch();
                        if (ch == '\n') {
                            line++;
                        }
                        if (ch == '*') {
                            if (readch('/')) {
                                ch = ' ';
                                return Token.block_comment;
                            }
                        } else {
                            if (eof) {
                                error++;
                                return new Error(startLine, "*/", Tag.ERROR_CARACTER_INESPERADO, Token.block_comment);
                            }
                        }
                    }
                } else {
                    return Token.div;
                }
            case '*':
                ch = ' ';
                return Token.mult;
            case '+':
                ch = ' ';
                return Token.plus;
            case '-':
                ch = ' ';
                return Token.minus;
        }

        //simbols
        switch (ch) {
            case '(':
                ch = ' ';
                return Token.open_paren;
            case ')':
                ch = ' ';
                return Token.close_paren;
            case ';':
                ch = ' ';
                return Token.dot_comma;
            case ',':
                ch = ' ';
                return Token.comma;
            case '"':
                StringBuilder lb = new StringBuilder();
                while (!readch('"')) {
                    lb.append(ch);
                    if (eof || ch == '\n') {
                        error++;
                        return new Error(line, "\"", Tag.ERROR_CARACTER_INESPERADO, new Word(lb.toString(), Tag.TEXT));
                    }
                    readch();
                }
                Word s = new Word(lb.toString(), Tag.TEXT);
                ch = ' ';
                return s;
        }

        //Operators
        switch (ch) {
            case '=':
                if (readch('=')) {
                    ch = ' ';
                    return Token.eq;
                } else {
                    ch = ' ';
                    return Token.assing;
                }
            case '>':
                if (readch('=')) {
                    ch = ' ';
                    return Token.ge;
                } else {
                    ch = ' ';
                    return Token.gt;
                }
            case '<':
                if (readch('=')) {
                    ch = ' ';
                    return Token.le;
                } else {
                    ch = ' ';
                    return Token.lt;
                }
            case '!':
                if (readch('=')) {
                    ch = ' ';
                    return Token.ne;
                } else {
                    ch = ' ';
                    return Token.not;
                }
            case '|':
                if (readch('|')) {
                    ch = ' ';
                    return Token.or;
                } else {
                    ch = ' ';
                    error++;
                    return new Error(line, "|", Tag.ERROR_CARACTER_INESPERADO, Token.or);
                }
            case '&':
                if (readch('&')) {
                    ch = ' ';
                    return Token.and;
                } else {
                    ch = ' ';
                    error++;
                    return new Error(line, "&", Tag.ERROR_CARACTER_INESPERADO, Token.and);
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

        //Identifiers
        if (Character.isLetter(ch)) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append(ch);
                readch();
            } while (Character.isLetterOrDigit(ch) && !eof);

            String s = sb.toString();
            SymbolTableObject obj1 = (SymbolTableObject) words.get(s);
            Word w;
            if (obj1 != null) {
                w = obj1.getWord();
                return w; //word already exists in hashtable
            }
            w = new Word(s, Tag.ID);
            SymbolTableObject obj = new SymbolTableObject(w, Tag.VOID);
            words.put(s, obj);
            return w;
        }

        char aux = ch;
        ch = ' ';
        error++;
        return new Error(line, "" + aux, Tag.ERROR_CARACTER_INVALIDO, Token.INVALID);

    }

    public void printTabelaSimbolos() {
        System.out.println("\nTabela de Simbolos\n");
        Object[] a = words.values().toArray();
        for (Object a1 : a) {
            System.out.println(a1);
        }
    }

    public int howManyErrors() {
        return error;
    }

    public int getLine() {
        return line;
    }

    public Hashtable getSymbolTable() {
        return words;
    }

    public void setSymbolTable(Hashtable table) {
        words = table;
    }
}
