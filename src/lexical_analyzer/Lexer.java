package lexical_analyzer;

import java.io.*;
import java.util.*;

/**
 *
 * @author Lindley and Nícolas
 */
public class Lexer {
    public static int line = 1; //line counter
    public static boolean eof = false;
    private char ch = ' '; //read file character
    private FileReader file;
        
    private Hashtable words = new Hashtable();
    
    //Method to insert reserved words in hashtable
    private void reserve(Word w){
        // TODO Ver se está correto
        words.put(w.toString(), w); //lexeme is the key to entry into the hashtable
        
    }
    
    //Constructor
    public Lexer(String fileName) throws FileNotFoundException{       
        try{
            file = new FileReader(fileName);
        }catch(FileNotFoundException e){
            System.out.println("File '" + fileName + "' not found");
            throw e;
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

    //Reads next file character
    private void readch() throws IOException{
        int c;
        if((c = file.read()) != -1) ch = (char) c;
        else eof = true;            
    }
    
    //Reads next file character and check if it is equal to c
    private boolean readch(char c) throws IOException{
        readch();
        if(ch != c) return false;
        ch = ' ';
        return true;
    }
    
    //Recognizes tokens
    public Token scan() throws IOException{
        //Ignore delimiters
        for(;; readch()){
            if (eof) return null;
            else if(ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b') continue;
            else if(ch == '\n') line++; //line counter
            else break;
        }
        
        //Operators
        switch(ch){
            case '=':
                if(readch('=')) return Word.eq;
                return new Token('=');
            case '>':
                if(readch('=')) return Word.ge;
                return new Token('>');
            case '<':
                if(readch('=')) return Word.le;
                return new Token('<');
            case '!':
                if(readch('=')) return Word.ne;
                return new Token('!');
            case '|':
                if(readch( '|')) return Word.or;
                // TODO Tem o operador '!' na linguagem?
                return new Token('|');
            case '&':
                if(readch('&')) return Word.and;
                // TODO Tem o operador '&' na linguagem?
                return new Token('&');
        }
        
        //Numbers
        if(Character.isDigit(ch)){
            int value=0;
            do{
                value = 10*value + Character.digit(ch, 10);
                readch();
            }while(Character.isDigit(ch));
            return new Num(value);            
        }
        
        //Identifiers
        if(Character.isLetter(ch)){
            StringBuilder sb = new StringBuilder();
            do{
                sb.append(ch);
                readch();
            }while(Character.isLetterOrDigit(ch));
            
            String s = sb.toString();
            Word w = (Word)words.get(s);
            if(w != null) return w; //word already exists in hashtable
            w = new Word(s,Tag.ID);
            words.put(s, w);
            return w;
        }
        
        //Another characters
        Token t = new Token(ch);
        ch = ' ';
        return t;
    }   
}
