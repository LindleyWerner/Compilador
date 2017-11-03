/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic_parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexical_analyzer.Lexer;
import lexical_analyzer.Tag;
import lexical_analyzer.Token;

/**
 *
 * @author Lindley and Nícolas
 */
public class Semantic {

    private final Lexer lexer;
    private Token tok;
    private final List<Tag> listTagEsperadas;
    private final boolean debug;
    private final FollowTable follow;
    private int error;

    public Semantic(String fileName, boolean debug) throws FileNotFoundException {
        lexer = new Lexer(fileName);
        listTagEsperadas = new ArrayList<>();
        follow = new FollowTable();
        error = 0;
        this.debug = debug;
    }

    public void start() {        
        advance();
        program();
    }

    private void advance() {
        try {
            tok = lexer.scan();
            if(tok == null){
                System.out.println("Fim da análise. " + lexer.howManyErrors() + " erro(s) léxico(s) e " + error + " erro(s) sintático(s)\n\n");
            }else{
                if(debug) System.out.println(tok.getLexeme());
                if (tok.getTag().compareTo(Tag.ERROR_CARACTER_INESPERADO) == 0
                        || tok.getTag().compareTo(Tag.ERROR_CARACTER_INVALIDO) == 0){
                    lexicalError();                        
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Semantic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean eat(Tag tag, String where) {
        if (tok.getTag() == tag) {
            advance();
            return true;
        } else {
            listTagEsperadas.add(tag);            
            error(where);
            return false;
        }
    }

    private void error(String where) {
        System.out.print("Erro semântico, linha: " + lexer.getLine() + ", esperado(s): ");
        error++;
        listTagEsperadas.forEach((tag) -> {
            System.out.print("'"+tag.getName()+ "' ");
        });
        System.out.println(", recebido: " + tok.getTag().getName());
        listTagEsperadas.clear();
        errorRecover(where);
    }
    
    private void errorRecover(String where){
        List<Tag> program = follow.getFollows(where);
        if(debug){
            System.out.println("Recuperando de erro semântico");
        }
        advance();
        boolean isStopToken = false;
        while(true){
            for(Tag tag: program){
                /*if(tok==null){
                    isStopToken = true;
                    break;
                }*/
                if (tok.getTag().compareTo(tag)==0){
                    if(debug){
                        System.out.println(", stop token encontrado: " + tok.getTag().getName());                        
                    }
                    isStopToken = true;
                    break;
                }
            }
            //if (tok != null)            
            if (isStopToken) break;      
            advance();
        }      
    }
    
    private void lexicalError(){
        //leu caracter inesperado, reporta o erro e simula o recebimento do caracter
        if (tok.getTag().compareTo(Tag.ERROR_CARACTER_INESPERADO) == 0){
                System.out.println("Erro léxico, linha: " + tok.getErrorLine() +
                    ", esperado: " +tok.getSimulatedToken().getTag().getName());
                tok = tok.getSimulatedToken();
        }// leu caracter inválido, reporta erro e ignora o caracter
        else if (tok.getTag().compareTo(Tag.ERROR_CARACTER_INVALIDO) == 0){
            System.out.println("Erro léxico, linha: " + tok.getErrorLine() + 
                    ", caracter inválido: " + tok.getLexeme());
            advance();
        }
    }

    //program ::= program [decl-list] stmt-list end
    private boolean program() {
        if(debug) System.out.println("> program");
        if(!eat(Tag.PROGRAM, "program")) return false;
        if (tok.getTag().compareTo(Tag.INT) == 0
                || tok.getTag().compareTo(Tag.STRING) == 0) {
            declList();
        }
        stmtList();
        if(!eat(Tag.END, "program")) return false;
        return true;
    }

    //decl-list ::= decl {decl}
    private boolean declList() {
        if(debug) System.out.println("> decList");
        if (tok.getTag().compareTo(Tag.INT) == 0
                || tok.getTag().compareTo(Tag.STRING) == 0) {
            do{
                decl();
            }while (tok.getTag().compareTo(Tag.INT) == 0
                    || tok.getTag().compareTo(Tag.STRING) == 0);            
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error("declList");
        }        
        return true;
    }

    //decl ::= type ident-list ";"
    private boolean decl() {
        if(debug) System.out.println("> decl");
        if (tok.getTag().compareTo(Tag.INT) == 0
                || tok.getTag().compareTo(Tag.STRING) == 0) {
            type();
            identList();
            if(!eat(Tag.DOT_COMMA, "decl")) return false;
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error("decl");
        }
        return true;
    }

    private boolean identList() {
        if(debug) System.out.println("> identList");
        //ident-list ::= identifier {"," identifier}
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            identifier();
            while (tok.getTag().compareTo(Tag.COMMA) == 0) {
                if(!eat(Tag.COMMA, "identList")) return false;
                identifier();
            }
        } else {
            listTagEsperadas.add(Tag.ID);
            error("identList");
        }
        return true;
    }

    private boolean type() {
        if(debug) System.out.println("> type");
        //type ::= int
        if (tok.getTag().compareTo(Tag.INT) == 0) {
            if(!eat(Tag.INT, "type")) return false;
            // type ::= string
        } else if (tok.getTag().compareTo(Tag.STRING) == 0) {
            if(!eat(Tag.STRING, "type")) return false;
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error("type");
        }
        return true;
    }

    // stmt-list ::= stmt {stmt}
    private boolean stmtList() {
        if(debug) System.out.println("> stmtList");
        if (tok.getTag().compareTo(Tag.ID) == 0
                ||tok.getTag().compareTo(Tag.IF) == 0
                || tok.getTag().compareTo(Tag.DO) == 0
                || tok.getTag().compareTo(Tag.SCAN) == 0
                || tok.getTag().compareTo(Tag.PRINT) == 0) {
            do{
                stmt();
            }while(tok.getTag().compareTo(Tag.ID) == 0
                    ||tok.getTag().compareTo(Tag.IF) == 0 
                    ||tok.getTag().compareTo(Tag.DO) == 0
                    || tok.getTag().compareTo(Tag.SCAN) == 0
                    || tok.getTag().compareTo(Tag.PRINT) == 0);       
        } else {
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.IF);
            listTagEsperadas.add(Tag.DO);
            listTagEsperadas.add(Tag.SCAN);
            listTagEsperadas.add(Tag.PRINT);
            error("stmtList");
        }        
        return true;
    }

    private boolean stmt() {
        if(debug) System.out.println("> stmt");
        // stmt ::= assign-stmt ";"
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            assingStmt();
            if(!eat(Tag.DOT_COMMA, "stmt")) return false;
        }
        // stmt ::= if-stmt
        else if (tok.getTag().compareTo(Tag.IF) == 0) {
            ifStmt();
        } // stmt ::=  while-stmt
        else if (tok.getTag().compareTo(Tag.DO) == 0) {
            whileStmt();
        } // stmt ::= read-stmt ";"
        else if (tok.getTag().compareTo(Tag.SCAN) == 0) {
            readStmt();
            if(!eat(Tag.DOT_COMMA, "stmt")) return false;
        } //stmt ::= write-stmt ";"
        else if (tok.getTag().compareTo(Tag.PRINT) == 0) {
            writeStmt();
            if(!eat(Tag.DOT_COMMA, "stmt")) return false;
        } else {
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.IF);
            listTagEsperadas.add(Tag.DO);
            listTagEsperadas.add(Tag.SCAN);
            listTagEsperadas.add(Tag.PRINT);
            error("stmt");
        }
        return true;
    }

    private boolean assingStmt() {
        if(debug) System.out.println("> assingStmt");
        //assign-stmt ::= identifier "=" simple_expr
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            identifier();
            if(!eat(Tag.ASSIGN, "assingStmt")) return false;
            simpleExpr();
        } else {
            listTagEsperadas.add(Tag.ID);
            error("assingStmt");
        }
        return true;
    }

    private boolean ifStmt() {
        if(debug) System.out.println("> ifStmt");
        //if-stmt ::= if condition then stmt-list if-stmt’
        if (tok.getTag().compareTo(Tag.IF) == 0) {
            if(!eat(Tag.IF, "ifStmt")) return false;
            condition();
            if(!eat(Tag.THEN, "ifStmt")) return false;
            stmtList();
            ifStmtPrime();
        } else {
            listTagEsperadas.add(Tag.IF);
            error("ifStmt");
        }
        return true;
    }

    private boolean ifStmtPrime() {
        if(debug) System.out.println("> ifStmtPrime");
        //if-stmt’ ::= end
        if (tok.getTag().compareTo(Tag.END) == 0) {
            if(!eat(Tag.END, "ifStmtPrime")) return false;
        }//if-stmt’ ::= else stmt-list end
        else if (tok.getTag().compareTo(Tag.ELSE) == 0) {
            if(!eat(Tag.ELSE, "ifStmtPrime")) return false;
            stmtList();
            if(!eat(Tag.END, "ifStmtPrime")) return false;
        } else {
            listTagEsperadas.add(Tag.END);
            listTagEsperadas.add(Tag.ELSE);
            error("ifStmtPrime");
        }
        return true;
    }

    private boolean condition() {
        if(debug) System.out.println("> condition");
        //condition ::= expression
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.STRING) == 0) {
            expression();
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("condition");
        }
        return true;
    }

    private boolean whileStmt() {
        if(debug) System.out.println("> whileStmt");
        //while-stmt ::= do stmt-list stmt-sufix
        if (tok.getTag().compareTo(Tag.DO) == 0) {
            if(!eat(Tag.DO, "whileStmt")) return false;
            stmtList();
            stmtSufix();
        } else {
            listTagEsperadas.add(Tag.DO);
            error("whileStmt");
        }
        return true;
    }

    private boolean stmtSufix() {
        if(debug) System.out.println("> stmtSufix");
        //   stmt-sufix ::= while condition end  
        if (tok.getTag().compareTo(Tag.WHILE) == 0) {
            if(!eat(Tag.WHILE, "stmtSufix")) return false;
            condition();
            if(!eat(Tag.END, "stmtSufix")) return false;
        } else {
            listTagEsperadas.add(Tag.WHILE);
            error("stmtSufix");
        }
        return true;
    }

    private boolean readStmt() {
        if(debug) System.out.println("> readStmt");
        //read-stmt ::= scan "(" identifier ")"
        if (tok.getTag().compareTo(Tag.SCAN) == 0) {
            if(!eat(Tag.SCAN, "readStmt")) return false;
            if(!eat(Tag.OPEN_PAREN, "readStmt")) return false;
            identifier();
            if(!eat(Tag.CLOSE_PAREN, "readStmt")) return false;
        } else {
            listTagEsperadas.add(Tag.SCAN);
            error("readStmt");
        }
        return true;
    }

    private boolean writeStmt() {
        if(debug) System.out.println("> writeStmt");
        //write-stmt ::= print "(" writable ")"
        if (tok.getTag().compareTo(Tag.PRINT) == 0) {
            if(!eat(Tag.PRINT, "writeStmt")) return false;
            if(!eat(Tag.OPEN_PAREN, "writeStmt")) return false;
            writable();
            if(!eat(Tag.CLOSE_PAREN, "writeStmt")) return false;
        } else {
            listTagEsperadas.add(Tag.PRINT);
            error("writeStmt");
        }
        return true;
    }

    private boolean writable() {
        if(debug) System.out.println("> writable");
        //writable ::= simple-expr
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            simpleExpr();
        }//writable ::= literal 
        else if (tok.getTag().compareTo(Tag.TEXT) == 0) {
            literal();
        }
        else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("writable");
        }
        return true;
    }

    private boolean expression() {
        if(debug) System.out.println("> expression");
        // expression ::= simple-expr expression’
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            simpleExpr();
            expressionPrime();
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("expression");
        }
        return true;
    }

    private boolean expressionPrime() {
        if(debug) System.out.println("> expressionPrime");
        //expression’ ::= 𝛌
        if (tok.getTag().compareTo(Tag.THEN) == 0
                || tok.getTag().compareTo(Tag.END) == 0
                || tok.getTag().compareTo(Tag.CLOSE_PAREN) == 0) {
        } //expression’ ::= relop simple-expr
        else if (tok.getTag().compareTo(Tag.EQ) == 0
                || tok.getTag().compareTo(Tag.GT) == 0
                || tok.getTag().compareTo(Tag.GE) == 0
                || tok.getTag().compareTo(Tag.LT) == 0
                || tok.getTag().compareTo(Tag.LE) == 0
                || tok.getTag().compareTo(Tag.NE) == 0) {
            relop();
            simpleExpr();            
        }else{
            listTagEsperadas.add(Tag.EQ);
            listTagEsperadas.add(Tag.GT);
            listTagEsperadas.add(Tag.GE);
            listTagEsperadas.add(Tag.LT);
            listTagEsperadas.add(Tag.LE);
            listTagEsperadas.add(Tag.NE);
            error("expressionPrime");
        }
        return true;
    }

    private boolean simpleExpr() {
        if(debug) System.out.println("> simpleExpr");
        //simple-expr ::= term simple-expr’
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            term();
            simpleExprPrime();
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("simpleExpr");
        }
        return true;
    }
    
    private boolean simpleExprPrime() {
        if(debug) System.out.println("> simpleExprPrime");
        //simple-expr’ ::=  𝛌
        if(tok.getTag().compareTo(Tag.DOT_COMMA) == 0
                || tok.getTag().compareTo(Tag.CLOSE_PAREN) == 0
                || tok.getTag().compareTo(Tag.THEN) == 0
                || tok.getTag().compareTo(Tag.END) == 0
                || tok.getTag().compareTo(Tag.GT) == 0
                || tok.getTag().compareTo(Tag.EQ) == 0
                || tok.getTag().compareTo(Tag.GE) == 0
                || tok.getTag().compareTo(Tag.LT) == 0
                || tok.getTag().compareTo(Tag.LE) == 0
                || tok.getTag().compareTo(Tag.NOT) == 0){            
        }//simple-expr’ ::= addop term simple-expr’
        else if (tok.getTag().compareTo(Tag.PLUS) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OR) == 0){
            addop();
            term();
            simpleExprPrime();
        }else{
            listTagEsperadas.add(Tag.PLUS);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OR);
            error("simpleExprPrime");
        }
        return true;
    }
    
    private boolean term() {
        if(debug) System.out.println("> term");
        //term ::= factor-a term’
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            factorA();
            termPrime();
        }else{
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("term");
        }
        return true;
    }
    
    private boolean termPrime(){
        if(debug) System.out.println("> termPrime");
        //term’ ::=  𝛌
        if(tok.getTag().compareTo(Tag.DOT_COMMA) == 0
                || tok.getTag().compareTo(Tag.CLOSE_PAREN) == 0
                || tok.getTag().compareTo(Tag.THEN) == 0
                || tok.getTag().compareTo(Tag.END) == 0
                || tok.getTag().compareTo(Tag.GT) == 0
                || tok.getTag().compareTo(Tag.EQ) == 0
                || tok.getTag().compareTo(Tag.GE) == 0
                || tok.getTag().compareTo(Tag.LT) == 0
                || tok.getTag().compareTo(Tag.LE) == 0
                || tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OR) == 0
                || tok.getTag().compareTo(Tag.PLUS) == 0){            
        }//term’ ::= mulop factor-a term’
        else if(tok.getTag().compareTo(Tag.MULT) == 0
                    || tok.getTag().compareTo(Tag.DIV) == 0
                    || tok.getTag().compareTo(Tag.AND) == 0){
            mulop();
            factorA();
            termPrime();
        }else{
            listTagEsperadas.add(Tag.MULT);
            listTagEsperadas.add(Tag.DIV);
            listTagEsperadas.add(Tag.AND);
            error("termPrime");
        }  
        return true;
    }
    
    private boolean factorA() {
        if(debug) System.out.println("> factorA");
        //factor-a ::= factor
        if (tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            factor();
        }//factor-a ::= ! factor
        else if(tok.getTag().compareTo(Tag.NOT) == 0){
            if(!eat(Tag.NOT, "factorA")) return false;
            factor();
        }//factor-a ::= "-" factor
        else if(tok.getTag().compareTo(Tag.MINUS) == 0){
            if(!eat(Tag.MINUS, "factorA")) return false;
            factor();
        }else{           
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            error("factorA");
        }
        return true;
    }
    
    private boolean factor() {
        if(debug) System.out.println("> factor");
        //factor ::= identifier
        if(tok.getTag().compareTo(Tag.ID) == 0){
            identifier();
        }//factor ::= constant
        else if(tok.getTag().compareTo(Tag.NUM) == 0
                    || tok.getTag().compareTo(Tag.TEXT) == 0){
            constant();
        }//factor ::= "(" expression ")"
        else if(tok.getTag().compareTo(Tag.OPEN_PAREN) == 0){
            if(!eat(Tag.OPEN_PAREN, "factor")) return false;
            expression();
            if(!eat(Tag.CLOSE_PAREN, "factor")) return false;
        }else{            
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            error("factor");
        }
        return true;
    }
    
    private boolean relop() {
        if(debug) System.out.println("> relop");
        //relop ::= "=="
        if(tok.getTag().compareTo(Tag.EQ) == 0){
            if(!eat(Tag.EQ, "relop")) return false;
        }//relop ::= ">"
        else if(tok.getTag().compareTo(Tag.GT) == 0){
            if(!eat(Tag.GT, "relop")) return false;
        }//relop ::= ">="
        else if(tok.getTag().compareTo(Tag.GE) == 0){
            if(!eat(Tag.GE, "relop")) return false;
        }//relop ::= "<"
        else if(tok.getTag().compareTo(Tag.LT) == 0){
            if(!eat(Tag.LT, "relop")) return false;
        }//relop ::= "<="
        else if(tok.getTag().compareTo(Tag.LE) == 0){
            if(!eat(Tag.LE, "relop")) return false;
        }//relop ::= "!="
        else if(tok.getTag().compareTo(Tag.NOT) == 0){
            if(!eat(Tag.NE, "relop")) return false;
        }else{
            listTagEsperadas.add(Tag.EQ);
            listTagEsperadas.add(Tag.GT);
            listTagEsperadas.add(Tag.GE);
            listTagEsperadas.add(Tag.LT);
            listTagEsperadas.add(Tag.LE);
            listTagEsperadas.add(Tag.NE);
            error("relop");
        }
        return true;
    }
    
    private boolean addop() {
        if(debug) System.out.println("> addop");
        //addop ::= "+"
        if(tok.getTag().compareTo(Tag.PLUS) == 0){
            if(!eat(Tag.PLUS, "addop")) return false;
        }//addop ::= "-"
        else if(tok.getTag().compareTo(Tag.MINUS) == 0){
            if(!eat(Tag.MINUS, "addop")) return false;
        }//addop ::= "||"
        else if(tok.getTag().compareTo(Tag.OR) == 0){
            if(!eat(Tag.OR, "addop")) return false;
        }else{
            listTagEsperadas.add(Tag.PLUS);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OR);
            error("addop");
        }
        return true;
    }
    
    private boolean mulop() {
        if(debug) System.out.println("> mulop");
        //mulop ::= "*"
        if(tok.getTag().compareTo(Tag.MULT) == 0){
            if(!eat(Tag.MULT, "mulop")) return false;
        }//mulop ::= "/"
        else if(tok.getTag().compareTo(Tag.DIV) == 0){
            if(!eat(Tag.DIV, "mulop")) return false;
        }//mulop ::= "&&"
        else if(tok.getTag().compareTo(Tag.AND) == 0){
            if(!eat(Tag.AND, "mulop")) return false;
        }else{
            listTagEsperadas.add(Tag.MULT);
            listTagEsperadas.add(Tag.DIV);
            listTagEsperadas.add(Tag.AND);
            error("mulop");
        }
        return true;
    }
    
    private boolean constant() {
        if(debug) System.out.println("> constant");
        //constant ::= integer_const
        if(tok.getTag().compareTo(Tag.NUM) == 0){
            integerConst();
        }//constant ::= literal 
        else if(tok.getTag().compareTo(Tag.TEXT) == 0){
            literal();
            if(!eat(Tag.TEXT, "constant")) return false; // TODO não é assim que faz, ler da tabela?
        }else{
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("constant");
        }
        return true;
    }
    
    private boolean integerConst(){
        if(debug) System.out.println("> integerConst");
        //integer_const ::= digit {digit} 
        if(tok.getTag().compareTo(Tag.NUM) == 0){            
            if(!eat(Tag.NUM, "integerConst")) return false; // TODO não é assim que faz, ler da tabela?
        }else{
            listTagEsperadas.add(Tag.NUM);
            error("integerConst");
        }
        return true;
    }
    
    private boolean literal() {
        if(debug) System.out.println("> literal");
        // literal ::= " “" {caractere} "”" 
        if(tok.getTag().compareTo(Tag.TEXT) == 0){            
            if(!eat(Tag.TEXT, "literal")) return false; // TODO não é assim que faz, ler da tabela?
        }else{
            listTagEsperadas.add(Tag.TEXT);
            error("literal");
        }
        return true;
    }
    
    private boolean identifier() {
        if(debug) System.out.println("> identifier");
        // identifier ::= letter {letter | digit }
        if(tok.getTag().compareTo(Tag.ID) == 0){            
            if(!eat(Tag.ID, "identifier")) return false; // TODO não é assim que faz, ler da tabela?
        }else{
            listTagEsperadas.add(Tag.ID);
            error("identifier");
        }
        return true;
    }
}
