/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lexical_analyzer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author nicol
 */
public class Semantic {

    private Lexer lexer;
    private Token tok;
    private final List<Tag> listTagEsperadas;

    public Semantic(String fileName) throws FileNotFoundException {
        lexer = new Lexer(fileName);      
        listTagEsperadas = new ArrayList<Tag>();
     }
    
    public void start(){
           advance();
        program();
    };

    private void advance() {
        try {
            tok = lexer.scan();
        } catch (IOException ex) {
            Logger.getLogger(Semantic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void eat(Tag tag) {
        if (tok.tag == tag) {
            advance();
        } else {
            listTagEsperadas.add(tag);
            error();
        }
    }

    private void error() {
        listTagEsperadas.forEach((tag) -> {
            System.out.println("Token esperado: " + tag.getName());
        });
        System.out.println("Token recebido: " + tok + '\n');
        listTagEsperadas.clear();
    }

    //program ::= program [decl-list] stmt-list end
    void program() {
        eat(Tag.PROGRAM);
        if (tok.tag.compareTo(Tag.INT) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            declList();
        }
        stmtList();
        eat(Tag.END);
    }

    //decl-list ::= decl {decl}
    private void declList() {
        if (tok.tag.compareTo(Tag.INT) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            decl();
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
        while (tok.tag.compareTo(Tag.INT) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            decl();
        }
    }

    //decl ::= type ident-list ";"
    private void decl() {
        if (tok.tag.compareTo(Tag.INT) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            type();
            identList();
            eat(Tag.DOT_COMMA);
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }

    private void type() {
        //type ::= in
        if (tok.tag.compareTo(Tag.INT) == 0) {
            eat(Tag.INT);
            // type ::= string
        } else if (tok.tag.compareTo(Tag.STRING) == 0) {
            eat(Tag.STRING);
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }

    // stmt-list ::= stmt {stmt}
    private void stmtList() {
        if (tok.tag.compareTo(Tag.DO) == 0
                || tok.tag.compareTo(Tag.SCAN) == 0
                //|| letter
                || tok.tag.compareTo(Tag.PRINT) == 0) {
            stmt();
        } else {
            listTagEsperadas.add(Tag.DO);
            listTagEsperadas.add(Tag.SCAN);
            listTagEsperadas.add(Tag.PRINT);
            //TODO letter
            error();
        }
        while (tok.tag.compareTo(Tag.DO) == 0
                || tok.tag.compareTo(Tag.SCAN) == 0
                // TODO  letter
                || tok.tag.compareTo(Tag.PRINT) == 0) {
            stmt();
        }
    }

    private void stmt() {
        // stmt ::= if-stmt
        if (tok.tag.compareTo(Tag.IF) == 0) {
            ifStmt();
        } // stmt ::=  while-stmt
        else if (tok.tag.compareTo(Tag.WHILE) == 0) {
            whileStmt();
        } // stmt ::= read-stmt ";"
        else if (tok.tag.compareTo(Tag.SCAN) == 0) {
            scan();
            eat(Tag.DOT_COMMA);
        } //stmt ::= write-stmt ";"
        else if (tok.tag.compareTo(Tag.PRINT) == 0) {
            write();
            eat(Tag.DOT_COMMA);
            // TODO letter
        } else {
            listTagEsperadas.add(Tag.IF);
            listTagEsperadas.add(Tag.WHILE);
            listTagEsperadas.add(Tag.SCAN);
            listTagEsperadas.add(Tag.PRINT);
            //TODO letter
            error();
        }
    }

    private void assingStmt() {

        //assign-stmt ::= identifier "=" simple_expr
        if (true) {//TODO letter
            identifier();
            eat(Tag.EQ);
            simpleExpr();
        } else {
            listTagEsperadas.add(Tag.IF);//TODO letter
            //letter
            error();
        }
    }

    private void ifStmt() {
        //if-stmt ::= if condition then stmt-list if-stmt’
        if (tok.tag.compareTo(Tag.IF) == 0) {
            eat(Tag.IF);
            condition();
            eat(Tag.THEN);
            stmtList();
            ifStmtPrime();
        } else {
            listTagEsperadas.add(Tag.IF);
            error();
        }
    }

    private void ifStmtPrime() {
        //if-stmt’ ::= end
        if (tok.tag.compareTo(Tag.END) == 0) {
            eat(Tag.END);
        }//if-stmt’ ::= else stmt-list end
        else if (tok.tag.compareTo(Tag.ELSE) == 0) {
            eat(Tag.ELSE);
            stmtList();
            eat(Tag.END);
        }else{
            listTagEsperadas.add(Tag.END);
            listTagEsperadas.add(Tag.ELSE);
            error();
        }
    }
    
    private void condition() {
        //condition ::= expression
        if (tok.tag.compareTo(Tag.NOT) == 0||
            tok.tag.compareTo(Tag.MINUS) == 0
            // TODO "
            // TODO letter 
            // TODO digit
           ) {
            expression();
        }else{
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            // TODO "
            // TODO letter 
            // TODO digit
            error();
        }
    }
    
    private void letter() {
        //eat(Tag.);
    }

    private void digit() {
        eat(Tag.NUM);//TODO digit
    }

    private void caracter() {
        eat(Tag.STRING); //TODO letter
    }

    private void identList() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void whileStmt() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void scan() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void write() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void simpleExpr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void identifier() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void expression() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


}
