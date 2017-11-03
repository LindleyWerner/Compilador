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

    private final Lexer lexer;
    private Token tok;
    private final List<Tag> listTagEsperadas;

    public Semantic(String fileName) throws FileNotFoundException {
        lexer = new Lexer(fileName);
        listTagEsperadas = new ArrayList<>();
    }

    public void start() {
        advance();
        program();
    }

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
        System.out.println("Erro linha: " + lexer.getLine()); 
        System.out.print("Token(s) esperado(s): ");        
        listTagEsperadas.forEach((tag) -> {
            System.out.print(tag.getName() + " ");
        });
        System.out.println("\nToken recebido: " + tok.tag.name() + "\n");
        listTagEsperadas.clear();
    }

    //program ::= program [decl-list] stmt-list end
    private void program() {
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
            do{
                decl();
            }while (tok.tag.compareTo(Tag.INT) == 0
                    || tok.tag.compareTo(Tag.STRING) == 0);            
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error();
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

    private void identList() {
        //ident-list ::= identifier {"," identifier}
        if (tok.tag.compareTo(Tag.ID) == 0) {
            identifier();
            while (tok.tag.compareTo(Tag.COMMA) == 0) {
                eat(Tag.COMMA);
                identifier();
            }
        } else {
            listTagEsperadas.add(Tag.ID);
            error();
        }
    }

    private void type() {
        //type ::= int
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
        if (tok.tag.compareTo(Tag.ID) == 0
                ||tok.tag.compareTo(Tag.IF) == 0
                || tok.tag.compareTo(Tag.DO) == 0
                || tok.tag.compareTo(Tag.SCAN) == 0
                || tok.tag.compareTo(Tag.PRINT) == 0) {
            do{
                stmt();
            }while(tok.tag.compareTo(Tag.ID) == 0
                    ||tok.tag.compareTo(Tag.IF) == 0 
                    ||tok.tag.compareTo(Tag.DO) == 0
                    || tok.tag.compareTo(Tag.SCAN) == 0
                    || tok.tag.compareTo(Tag.PRINT) == 0);       
        } else {
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.IF);
            listTagEsperadas.add(Tag.DO);
            listTagEsperadas.add(Tag.SCAN);
            listTagEsperadas.add(Tag.PRINT);
            error();
        }        
    }

    private void stmt() {
        // stmt ::= assign-stmt ";"
        if (tok.tag.compareTo(Tag.ID) == 0) {
            assingStmt();
            eat(Tag.DOT_COMMA);
        }
        // stmt ::= if-stmt
        else if (tok.tag.compareTo(Tag.IF) == 0) {
            ifStmt();
        } // stmt ::=  while-stmt
        else if (tok.tag.compareTo(Tag.DO) == 0) {
            whileStmt();
        } // stmt ::= read-stmt ";"
        else if (tok.tag.compareTo(Tag.SCAN) == 0) {
            readStmt();
            eat(Tag.DOT_COMMA);
        } //stmt ::= write-stmt ";"
        else if (tok.tag.compareTo(Tag.PRINT) == 0) {
            writeStmt();
            eat(Tag.DOT_COMMA);
        } else {
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.IF);
            listTagEsperadas.add(Tag.DO);
            listTagEsperadas.add(Tag.SCAN);
            listTagEsperadas.add(Tag.PRINT);
            error();
        }
    }

    private void assingStmt() {
        //assign-stmt ::= identifier "=" simple_expr
        if (tok.tag.compareTo(Tag.ID) == 0) {
            identifier();
            eat(Tag.ASSIGN);
            simpleExpr();
        } else {
            listTagEsperadas.add(Tag.ID);
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
        } else {
            listTagEsperadas.add(Tag.END);
            listTagEsperadas.add(Tag.ELSE);
            error();
        }
    }

    private void condition() {
        //condition ::= expression
        if (tok.tag.compareTo(Tag.NOT) == 0
                || tok.tag.compareTo(Tag.MINUS) == 0
                || tok.tag.compareTo(Tag.OPEN_PAREN) == 0
                || tok.tag.compareTo(Tag.ID) == 0
                || tok.tag.compareTo(Tag.NUM) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            expression();
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }

    private void whileStmt() {
        //while-stmt ::= do stmt-list stmt-sufix
        if (tok.tag.compareTo(Tag.DO) == 0) {
            eat(Tag.DO);
            stmtList();
            stmtSufix();
        } else {
            listTagEsperadas.add(Tag.DO);
            error();
        }
    }

    private void stmtSufix() {
        //   stmt-sufix ::= while condition end  
        if (tok.tag.compareTo(Tag.WHILE) == 0) {
            eat(Tag.WHILE);
            condition();
            eat(Tag.END);
        } else {
            listTagEsperadas.add(Tag.WHILE);
            error();
        }
    }

    private void readStmt() {
        //read-stmt ::= scan "(" identifier ")"
        if (tok.tag.compareTo(Tag.SCAN) == 0) {
            eat(Tag.SCAN);
            eat(Tag.OPEN_PAREN);
            identifier();
            eat(Tag.CLOSE_PAREN);
        } else {
            listTagEsperadas.add(Tag.SCAN);
            error();
        }
    }

    private void writeStmt() {
        //write-stmt ::= print "(" writable ")"
        if (tok.tag.compareTo(Tag.PRINT) == 0) {
            eat(Tag.PRINT);
            eat(Tag.OPEN_PAREN);
            writable();
            eat(Tag.CLOSE_PAREN);
        } else {
            listTagEsperadas.add(Tag.PRINT);
            error();
        }
    }

    private void writable() {
        //writable ::= simple-expr
        if (tok.tag.compareTo(Tag.NOT) == 0
                || tok.tag.compareTo(Tag.MINUS) == 0
                || tok.tag.compareTo(Tag.OPEN_PAREN) == 0
                || tok.tag.compareTo(Tag.ID) == 0
                || tok.tag.compareTo(Tag.NUM) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            simpleExpr();
        }//writable ::= literal 
        else if (tok.tag.compareTo(Tag.STRING) == 0) {
            literal();
        }
        else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }

    private void expression() {
        // expression ::= simple-expr expression’
        if (tok.tag.compareTo(Tag.NOT) == 0
                || tok.tag.compareTo(Tag.MINUS) == 0
                || tok.tag.compareTo(Tag.OPEN_PAREN) == 0
                || tok.tag.compareTo(Tag.ID) == 0
                || tok.tag.compareTo(Tag.NUM) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            simpleExpr();
            expressionPrime();
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }

    private void expressionPrime() {
        //expression’ ::= 𝛌
        if (tok.tag.compareTo(Tag.THEN) == 0
                || tok.tag.compareTo(Tag.END) == 0
                || tok.tag.compareTo(Tag.CLOSE_PAREN) == 0) {
        } //expression’ ::= relop simple-expr
        else if (tok.tag.compareTo(Tag.EQ) == 0
                || tok.tag.compareTo(Tag.GT) == 0
                || tok.tag.compareTo(Tag.GE) == 0
                || tok.tag.compareTo(Tag.LT) == 0
                || tok.tag.compareTo(Tag.LE) == 0
                || tok.tag.compareTo(Tag.NE) == 0) {
            relop();
            simpleExpr();            
        }else{
            listTagEsperadas.add(Tag.EQ);
            listTagEsperadas.add(Tag.GT);
            listTagEsperadas.add(Tag.GE);
            listTagEsperadas.add(Tag.LT);
            listTagEsperadas.add(Tag.LE);
            listTagEsperadas.add(Tag.NE);
            error();
        }
    }

    private void simpleExpr() {
        //simple-expr ::= term simple-expr’
        if (tok.tag.compareTo(Tag.NOT) == 0
                || tok.tag.compareTo(Tag.MINUS) == 0
                || tok.tag.compareTo(Tag.OPEN_PAREN) == 0
                || tok.tag.compareTo(Tag.ID) == 0
                || tok.tag.compareTo(Tag.NUM) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            term();
            simpleExprPrime();
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }
    
    private void simpleExprPrime() {
        //simple-expr’ ::=  𝛌
        if(tok.tag.compareTo(Tag.DOT_COMMA) == 0
                || tok.tag.compareTo(Tag.CLOSE_PAREN) == 0
                || tok.tag.compareTo(Tag.THEN) == 0
                || tok.tag.compareTo(Tag.END) == 0
                || tok.tag.compareTo(Tag.GT) == 0
                || tok.tag.compareTo(Tag.EQ) == 0
                || tok.tag.compareTo(Tag.GE) == 0
                || tok.tag.compareTo(Tag.LT) == 0
                || tok.tag.compareTo(Tag.LE) == 0
                || tok.tag.compareTo(Tag.NOT) == 0){            
        }//simple-expr’ ::= addop term simple-expr’
        else if (tok.tag.compareTo(Tag.PLUS) == 0
                || tok.tag.compareTo(Tag.MINUS) == 0
                || tok.tag.compareTo(Tag.OR) == 0){
            addop();
            term();
            simpleExprPrime();
        }else{
            listTagEsperadas.add(Tag.PLUS);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OR);
            error();
        }
    }
    
    private void term() {
        //term ::= factor-a term’
        if (tok.tag.compareTo(Tag.NOT) == 0
                || tok.tag.compareTo(Tag.MINUS) == 0
                || tok.tag.compareTo(Tag.OPEN_PAREN) == 0
                || tok.tag.compareTo(Tag.ID) == 0
                || tok.tag.compareTo(Tag.NUM) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            factorA();
            termPrime();
        }else{
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }
    
    private void termPrime(){
        //term’ ::=  𝛌
        if(tok.tag.compareTo(Tag.DOT_COMMA) == 0
                || tok.tag.compareTo(Tag.CLOSE_PAREN) == 0
                || tok.tag.compareTo(Tag.THEN) == 0
                || tok.tag.compareTo(Tag.END) == 0
                || tok.tag.compareTo(Tag.GT) == 0
                || tok.tag.compareTo(Tag.EQ) == 0
                || tok.tag.compareTo(Tag.GE) == 0
                || tok.tag.compareTo(Tag.LT) == 0
                || tok.tag.compareTo(Tag.LE) == 0
                || tok.tag.compareTo(Tag.NOT) == 0
                || tok.tag.compareTo(Tag.MINUS) == 0
                || tok.tag.compareTo(Tag.OR) == 0
                || tok.tag.compareTo(Tag.PLUS) == 0){            
        }//term’ ::= mulop factor-a term’
        else if(tok.tag.compareTo(Tag.MULT) == 0
                    || tok.tag.compareTo(Tag.DIV) == 0
                    || tok.tag.compareTo(Tag.AND) == 0){
            mulop();
            factorA();
            termPrime();
        }else{
            listTagEsperadas.add(Tag.MULT);
            listTagEsperadas.add(Tag.DIV);
            listTagEsperadas.add(Tag.AND);
            error();
        }            
    }
    
    private void factorA() {
        //factor-a ::= factor
        if (tok.tag.compareTo(Tag.OPEN_PAREN) == 0
                || tok.tag.compareTo(Tag.ID) == 0
                || tok.tag.compareTo(Tag.NUM) == 0
                || tok.tag.compareTo(Tag.STRING) == 0) {
            factor();
        }//factor-a ::= ! factor
        else if(tok.tag.compareTo(Tag.NOT) == 0){
            eat(Tag.NOT);
            factor();
        }//factor-a ::= "-" factor
        else if(tok.tag.compareTo(Tag.MINUS) == 0){
            eat(Tag.MINUS);
            factor();
        }else{           
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.STRING);
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            error();
        }
    }
    
    private void factor() {
        //factor ::= identifier
        if(tok.tag.compareTo(Tag.ID) == 0){
            identifier();
        }//factor ::= constant
        else if(tok.tag.compareTo(Tag.NUM) == 0
                    || tok.tag.compareTo(Tag.STRING) == 0){
            constant();
        }//factor ::= "(" expression ")"
        else if(tok.tag.compareTo(Tag.OPEN_PAREN) == 0){
            eat(Tag.OPEN_PAREN);
            expression();
            eat(Tag.CLOSE_PAREN);
        }else{            
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.STRING);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            error();
        }
    }
    
    private void relop() {
        //relop ::= "=="
        if(tok.tag.compareTo(Tag.EQ) == 0){
            eat(Tag.EQ);
        }//relop ::= ">"
        else if(tok.tag.compareTo(Tag.GT) == 0){
            eat(Tag.GT);
        }//relop ::= ">="
        else if(tok.tag.compareTo(Tag.GE) == 0){
            eat(Tag.GE);
        }//relop ::= "<"
        else if(tok.tag.compareTo(Tag.LT) == 0){
            eat(Tag.LT);
        }//relop ::= "<="
        else if(tok.tag.compareTo(Tag.LE) == 0){
            eat(Tag.LE);
        }//relop ::= "!="
        else if(tok.tag.compareTo(Tag.NOT) == 0){
            eat(Tag.NE);
        }else{
            listTagEsperadas.add(Tag.EQ);
            listTagEsperadas.add(Tag.GT);
            listTagEsperadas.add(Tag.GE);
            listTagEsperadas.add(Tag.LT);
            listTagEsperadas.add(Tag.LE);
            listTagEsperadas.add(Tag.NE);
            error();
        }
    }
    
    private void addop() {
        //addop ::= "+"
        if(tok.tag.compareTo(Tag.PLUS) == 0){
            eat(Tag.PLUS);
        }//addop ::= "-"
        else if(tok.tag.compareTo(Tag.MINUS) == 0){
            eat(Tag.MINUS);
        }//addop ::= "||"
        else if(tok.tag.compareTo(Tag.OR) == 0){
            eat(Tag.OR);
        }else{
            listTagEsperadas.add(Tag.PLUS);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OR);
            error();
        }
    }
    
    private void mulop() {
        //mulop ::= "*"
        if(tok.tag.compareTo(Tag.MULT) == 0){
            eat(Tag.MULT);
        }//mulop ::= "/"
        else if(tok.tag.compareTo(Tag.DIV) == 0){
            eat(Tag.DIV);
        }//mulop ::= "&&"
        else if(tok.tag.compareTo(Tag.AND) == 0){
            eat(Tag.AND);
        }else{
            listTagEsperadas.add(Tag.MULT);
            listTagEsperadas.add(Tag.DIV);
            listTagEsperadas.add(Tag.AND);
            error();
        }
    }
    
    private void constant() {
        //constant ::= integer_const
        if(tok.tag.compareTo(Tag.NUM) == 0){
            integerConst();
        }//constant ::= literal 
        else if(tok.tag.compareTo(Tag.STRING) == 0){
            literal();
            eat(Tag.STRING); // TODO não é assim que faz, ler da tabela?
        }
    }
    
    private void integerConst(){
        //integer_const ::= digit {digit} 
        if(tok.tag.compareTo(Tag.NUM) == 0){            
            eat(Tag.NUM); // TODO não é assim que faz, ler da tabela?
        }else{
            listTagEsperadas.add(Tag.NUM);
            error();
        }
    }
    
    private void literal() {
        // literal ::= " “" {caractere} "”" 
        if(tok.tag.compareTo(Tag.STRING) == 0){            
            eat(Tag.STRING); // TODO não é assim que faz, ler da tabela?
        }else{
            listTagEsperadas.add(Tag.STRING);
            error();
        }
    }
    
    private void identifier() {
        // identifier ::= letter {letter | digit }
        if(tok.tag.compareTo(Tag.ID) == 0){            
            eat(Tag.ID); // TODO não é assim que faz, ler da tabela?
        }else{
            listTagEsperadas.add(Tag.ID);
            error();
        }
    }
}
