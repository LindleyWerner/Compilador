/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic_parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lexical_analyzer.Lexer;
import lexical_analyzer.Tag;
import lexical_analyzer.Token;
import semantic_parser.Node;
import semantic_parser.SymbolTableObject;

/**
 *
 * @author Lindley and Nícolas
 */
public class Syntatic {

    private final Lexer lexer;
    private Token tok;
    private final List<Tag> listTagEsperadas;
    private final boolean debug;
    private final FollowTable follow;
    private int error, semanticError;

    public Syntatic(String fileName, boolean debug) throws FileNotFoundException {
        lexer = new Lexer(fileName);
        listTagEsperadas = new ArrayList<>();
        follow = new FollowTable();
        error = 0;
        semanticError = 0;
        this.debug = debug;
    }

    public void start() {
        advance();
        program();
        System.out.println("\nFim da análise. " + lexer.howManyErrors() + " erro(s) léxico(s), " + error + " erro(s) sintático(s) e " + semanticError + " erro(s) semânticos\n");
    }

    private void advance() {
        try {
            tok = lexer.scan();
            if (debug) {
                System.out.println(tok.getLexeme());
            }
            if(tok.getTag().compareTo(Tag.LINE_COMMENT) == 0
                    || tok.getTag().compareTo(Tag.BLOCK_COMMENT) == 0){
                advance();
            }
            else if (!(tok.getTag().compareTo(Tag.EOF) == 0)) {
                if (tok.getTag().compareTo(Tag.ERROR_CARACTER_INESPERADO) == 0
                        || tok.getTag().compareTo(Tag.ERROR_CARACTER_INVALIDO) == 0) {
                    lexicalError();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Syntatic.class.getName()).log(Level.SEVERE, null, ex);
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
        System.out.print("Erro sintático, linha: " + lexer.getLine() + ", esperado(s): ");
        error++;
        listTagEsperadas.forEach((tag) -> {
            System.out.print("'" + tag.getName() + "' ");
        });
        System.out.println(", recebido: " + tok.getTag().getName());
        listTagEsperadas.clear();
        errorRecover(where);
    }

    private void errorRecover(String where) {
        List<Tag> program = follow.getFollows(where);
        if (debug) {
            System.out.println("Recuperando de erro sintático");
        }
        advance();
        boolean isStopToken = false;
        while (true) {
            if (tok.getTag().compareTo(Tag.EOF) == 0) {
                break;
            }
            for (Tag tag : program) {
                if (tok.getTag().compareTo(tag) == 0) {
                    if (debug) {
                        System.out.println(", stop token encontrado: " + tok.getTag().getName());
                    }
                    isStopToken = true;
                    break;
                }
            }
            //if (tok != null)            
            if (isStopToken) {
                break;
            }
            advance();
        }
    }

    private void lexicalError() {
        //leu caracter inesperado, reporta o erro e simula o recebimento do caracter
        if (tok.getTag().compareTo(Tag.ERROR_CARACTER_INESPERADO) == 0) {
            System.out.println("Erro léxico, linha: " + tok.getErrorLine()
                    + ", esperado: " + tok.getSimulatedToken().getTag().getName());
            tok = tok.getSimulatedToken();
        }// leu caracter inválido, reporta erro e ignora o caracter
        else if (tok.getTag().compareTo(Tag.ERROR_CARACTER_INVALIDO) == 0) {
            System.out.println("Erro léxico, linha: " + tok.getErrorLine()
                    + ", caracter inválido: " + tok.getLexeme());
            advance();
        }
    }
    
    private void semanticError(Tag expected, Tag received){
        semanticError ++;
        System.out.println("Erro semântico, linha: " + tok.getErrorLine()
                    + ", experado: " + expected.toString() + " recebido: " + received.toString());
    }
    
    private void semanticError2(String lexeme){
        semanticError ++;
        System.out.println("Erro semântico, linha: " + tok.getErrorLine()
                    + ", variável " + lexeme + " duplicada");
    }

    //program ::= program [decl-list] stmt-list end
    private Node program() {
        Node node = new Node();
        if (debug) {
            System.out.println("> program");
        }
        if (!eat(Tag.PROGRAM, "program")) {
            return node;
        }
        if (tok.getTag().compareTo(Tag.INT) == 0
                || tok.getTag().compareTo(Tag.STRING) == 0) {
            declList();
        }
        stmtList();
        if (!eat(Tag.END, "program")) {
            return node;
        }
        return node;
    }

    //decl-list ::= decl {decl}
    private Node declList() {
        Node node = new Node();
        if (debug) {
            System.out.println("> decList");
        }
        if (tok.getTag().compareTo(Tag.INT) == 0
                || tok.getTag().compareTo(Tag.STRING) == 0) {
            do {
                decl();
            } while (tok.getTag().compareTo(Tag.INT) == 0
                    || tok.getTag().compareTo(Tag.STRING) == 0);
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error("declList");
        }
        return node;
    }

    //decl ::= type ident-list ";"
    private Node decl() {
        Node node = new Node();
        if (debug) {
            System.out.println("> decl");
        }
        if (tok.getTag().compareTo(Tag.INT) == 0
                || tok.getTag().compareTo(Tag.STRING) == 0) {
            node.type = type().type;
            identList(node.type);
            if (!eat(Tag.DOT_COMMA, "decl")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error("decl");
        }
        return node;
    }

    private Node identList(Tag type) {
        Node node = new Node();
        if (debug) {
            System.out.println("> identList");
        }
        //ident-list ::= identifier {"," identifier}
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            identifier(type);
            while (tok.getTag().compareTo(Tag.COMMA) == 0) {
                if (!eat(Tag.COMMA, "identList")) {
                    return node;
                }
                identifier(type);
            }
        } else {
            listTagEsperadas.add(Tag.ID);
            error("identList");
        }
        return node;
    }

    private Node type() {
        Node node = new Node();
        if (debug) {
            System.out.println("> type");
        }
        //type ::= int
        if (tok.getTag().compareTo(Tag.INT) == 0) {            
            if (!eat(Tag.INT, "type")) {
                return node;
            }
            node.type = Tag.INT;
            // type ::= string
        } else if (tok.getTag().compareTo(Tag.STRING) == 0) {            
            if (!eat(Tag.STRING, "type")) {
                return node;
            }
            node.type = Tag.STRING;
        } else {
            listTagEsperadas.add(Tag.INT);
            listTagEsperadas.add(Tag.STRING);
            error("type");
        }
        return node;
    }

    // stmt-list ::= stmt {stmt}
    private Node stmtList() {
        Node node = new Node();
        if (debug) {
            System.out.println("> stmtList");
        }
        if (tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.IF) == 0
                || tok.getTag().compareTo(Tag.DO) == 0
                || tok.getTag().compareTo(Tag.SCAN) == 0
                || tok.getTag().compareTo(Tag.PRINT) == 0) {
            do {
                stmt();
            } while (tok.getTag().compareTo(Tag.ID) == 0
                    || tok.getTag().compareTo(Tag.IF) == 0
                    || tok.getTag().compareTo(Tag.DO) == 0
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
        return node;
    }

    private Node stmt() {
        Node node = new Node();
        if (debug) {
            System.out.println("> stmt");
        }
        // stmt ::= assign-stmt ";"
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            assingStmt();
            if (!eat(Tag.DOT_COMMA, "stmt")) {
                return node;
            }
        } // stmt ::= if-stmt
        else if (tok.getTag().compareTo(Tag.IF) == 0) {
            ifStmt();
        } // stmt ::=  while-stmt
        else if (tok.getTag().compareTo(Tag.DO) == 0) {
            whileStmt();
        } // stmt ::= read-stmt ";"
        else if (tok.getTag().compareTo(Tag.SCAN) == 0) {
            readStmt();
            if (!eat(Tag.DOT_COMMA, "stmt")) {
                return node;
            }
        } //stmt ::= write-stmt ";"
        else if (tok.getTag().compareTo(Tag.PRINT) == 0) {
            writeStmt();
            if (!eat(Tag.DOT_COMMA, "stmt")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.IF);
            listTagEsperadas.add(Tag.DO);
            listTagEsperadas.add(Tag.SCAN);
            listTagEsperadas.add(Tag.PRINT);
            error("stmt");
        }
        return node;
    }

    private Node assingStmt() {
        Node node = new Node();
        Tag aux;
        if (debug) {
            System.out.println("> assingStmt");
        }
        //assign-stmt ::= identifier "=" simple_expr
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            node.type = identifier(Tag.VOID).type;
            if (!eat(Tag.ASSIGN, "assingStmt")) {
                return node;
            }
            aux = simpleExpr().type;
            if(aux != node.type){
                semanticError(node.type, aux);
                node.type = Tag.ERROR;                
            }
        } else {
            listTagEsperadas.add(Tag.ID);
            error("assingStmt");
        }
        return node;
    }

    private Node ifStmt() {
        Node node = new Node();
        if (debug) {
            System.out.println("> ifStmt");
        }
        //if-stmt ::= if condition then stmt-list if-stmt’
        if (tok.getTag().compareTo(Tag.IF) == 0) {
            if (!eat(Tag.IF, "ifStmt")) {
                return node;
            }
            condition();
            if (!eat(Tag.THEN, "ifStmt")) {
                return node;
            }
            stmtList();
            ifStmtPrime();
        } else {
            listTagEsperadas.add(Tag.IF);
            error("ifStmt");
        }
        return node;
    }

    private Node ifStmtPrime() {
        Node node = new Node();
        if (debug) {
            System.out.println("> ifStmtPrime");
        }
        //if-stmt’ ::= end
        if (tok.getTag().compareTo(Tag.END) == 0) {
            if (!eat(Tag.END, "ifStmtPrime")) {
                return node;
            }
        }//if-stmt’ ::= else stmt-list end
        else if (tok.getTag().compareTo(Tag.ELSE) == 0) {
            if (!eat(Tag.ELSE, "ifStmtPrime")) {
                return node;
            }
            stmtList();
            if (!eat(Tag.END, "ifStmtPrime")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.END);
            listTagEsperadas.add(Tag.ELSE);
            error("ifStmtPrime");
        }
        return node;
    }

    private Node condition() {
        Node node = new Node();
        if (debug) {
            System.out.println("> condition");
        }
        //condition ::= expression
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
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
        return node;
    }

    private Node whileStmt() {
        Node node = new Node();
        if (debug) {
            System.out.println("> whileStmt");
        }
        //while-stmt ::= do stmt-list stmt-sufix
        if (tok.getTag().compareTo(Tag.DO) == 0) {
            if (!eat(Tag.DO, "whileStmt")) {
                return node;
            }
            stmtList();
            stmtSufix();
        } else {
            listTagEsperadas.add(Tag.DO);
            error("whileStmt");
        }
        return node;
    }

    private Node stmtSufix() {
        Node node = new Node();
        if (debug) {
            System.out.println("> stmtSufix");
        }
        //   stmt-sufix ::= while condition end  
        if (tok.getTag().compareTo(Tag.WHILE) == 0) {
            if (!eat(Tag.WHILE, "stmtSufix")) {
                return node;
            }
            condition();
            if (!eat(Tag.END, "stmtSufix")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.WHILE);
            error("stmtSufix");
        }
        return node;
    }

    private Node readStmt() {
        Node node = new Node();
        if (debug) {
            System.out.println("> readStmt");
        }
        //read-stmt ::= scan "(" identifier ")"
        if (tok.getTag().compareTo(Tag.SCAN) == 0) {
            if (!eat(Tag.SCAN, "readStmt")) {
                return node;
            }
            if (!eat(Tag.OPEN_PAREN, "readStmt")) {
                return node;
            }
            node.type = identifier(Tag.VOID).type;
            if (!eat(Tag.CLOSE_PAREN, "readStmt")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.SCAN);
            error("readStmt");
        }
        return node;
    }

    private Node writeStmt() {
        Node node = new Node();
        if (debug) {
            System.out.println("> writeStmt");
        }
        //write-stmt ::= print "(" writable ")"
        if (tok.getTag().compareTo(Tag.PRINT) == 0) {
            if (!eat(Tag.PRINT, "writeStmt")) {
                return node;
            }
            if (!eat(Tag.OPEN_PAREN, "writeStmt")) {
                return node;
            }
            node.type = writable().type;
            if (!eat(Tag.CLOSE_PAREN, "writeStmt")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.PRINT);
            error("writeStmt");
        }
        return node;
    }

    private Node writable() {
        Node node = new Node();
        if (debug) {
            System.out.println("> writable");
        }
        //writable ::= simple-expr
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = simpleExpr().type;
        }//writable ::= literal 
        else if (tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = literal().type;
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("writable");
        }
        return node;
    }

    private Node expression() {
        Node node = new Node();
        if (debug) {
            System.out.println("> expression");
        }
        // expression ::= simple-expr expression’
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = simpleExpr().type;
            expressionPrime(node.type);
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("expression");
        }
        return node;
    }

    private Node expressionPrime(Tag type) {
        Node node = new Node();
        if (debug) {
            System.out.println("> expressionPrime");
        }
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
            node.type = simpleExpr().type;
            if(node.type != type){
                semanticError(type, node.type);
                node.type = Tag.ERROR;                
            }
        } else {
            listTagEsperadas.add(Tag.EQ);
            listTagEsperadas.add(Tag.GT);
            listTagEsperadas.add(Tag.GE);
            listTagEsperadas.add(Tag.LT);
            listTagEsperadas.add(Tag.LE);
            listTagEsperadas.add(Tag.NE);
            error("expressionPrime");
        }
        return node;
    }

    private Node simpleExpr() {
        Node node = new Node();
        if (debug) {
            System.out.println("> simpleExpr");
        }
        //simple-expr ::= term simple-expr’
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = term().type;
            simpleExprPrime(node.type);
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("simpleExpr");
        }
        return node;
    }

    private Node simpleExprPrime(Tag type) {
        Node node = new Node();
        if (debug) {
            System.out.println("> simpleExprPrime");
        }
        //simple-expr’ ::=  𝛌
        if (tok.getTag().compareTo(Tag.DOT_COMMA) == 0
                || tok.getTag().compareTo(Tag.CLOSE_PAREN) == 0
                || tok.getTag().compareTo(Tag.THEN) == 0
                || tok.getTag().compareTo(Tag.END) == 0
                || tok.getTag().compareTo(Tag.GT) == 0
                || tok.getTag().compareTo(Tag.EQ) == 0
                || tok.getTag().compareTo(Tag.GE) == 0
                || tok.getTag().compareTo(Tag.LT) == 0
                || tok.getTag().compareTo(Tag.LE) == 0
                || tok.getTag().compareTo(Tag.NOT) == 0) {
        }//simple-expr’ ::= addop term simple-expr’
        else if (tok.getTag().compareTo(Tag.PLUS) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OR) == 0) {
            addop();
            node.type = term().type;
            if (node.type != type){
                semanticError(type, node.type);
                node.type = Tag.ERROR;
            }
            simpleExprPrime(node.type);
        } else {
            listTagEsperadas.add(Tag.PLUS);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OR);
            error("simpleExprPrime");
        }
        return node;
    }

    private Node term() {
        Node node = new Node();
        if (debug) {
            System.out.println("> term");
        }
        //term ::= factor-a term’
        if (tok.getTag().compareTo(Tag.NOT) == 0
                || tok.getTag().compareTo(Tag.MINUS) == 0
                || tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = factorA().type;
            if (node.type != Tag.INT){
                semanticError(Tag.INT, node.type);
                node.type = Tag.ERROR;                
            }
            termPrime();
        } else {
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("term");
        }
        return node;
    }

    private Node termPrime() {
        Node node = new Node();
        Tag aux;
        if (debug) {
            System.out.println("> termPrime");
        }
        //term’ ::=  𝛌
        if (tok.getTag().compareTo(Tag.DOT_COMMA) == 0
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
                || tok.getTag().compareTo(Tag.PLUS) == 0) {
        }//term’ ::= mulop factor-a term’
        else if (tok.getTag().compareTo(Tag.MULT) == 0
                || tok.getTag().compareTo(Tag.DIV) == 0
                || tok.getTag().compareTo(Tag.AND) == 0) {
            mulop();
            aux = factorA().type;
            if (aux != Tag.INT){
                semanticError(Tag.INT, aux);
                node.type = Tag.ERROR;                
            }
            termPrime();
        } else {
            listTagEsperadas.add(Tag.MULT);
            listTagEsperadas.add(Tag.DIV);
            listTagEsperadas.add(Tag.AND);
            error("termPrime");
        }
        return node;
    }

    private Node factorA() {
        Node node = new Node();
        if (debug) {
            System.out.println("> factorA");
        }
        //factor-a ::= factor
        if (tok.getTag().compareTo(Tag.OPEN_PAREN) == 0
                || tok.getTag().compareTo(Tag.ID) == 0
                || tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = factor().type;
        }//factor-a ::= ! factor
        else if (tok.getTag().compareTo(Tag.NOT) == 0) {
            if (!eat(Tag.NOT, "factorA")) {
                return node;
            }
            node.type = factor().type;
        }//factor-a ::= "-" factor
        else if (tok.getTag().compareTo(Tag.MINUS) == 0) {
            if (!eat(Tag.MINUS, "factorA")) {
                return node;
            }
            node.type = factor().type;
        } else {
            listTagEsperadas.add(Tag.OPEN_PAREN);
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            listTagEsperadas.add(Tag.NOT);
            listTagEsperadas.add(Tag.MINUS);
            error("factorA");
        }
        return node;
    }

    private Node factor() {
        Node node = new Node();
        if (debug) {
            System.out.println("> factor");
        }
        //factor ::= identifier
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            node.type = identifier(Tag.VOID).type;
        }//factor ::= constant
        else if (tok.getTag().compareTo(Tag.NUM) == 0
                || tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = constant().type;
        }//factor ::= "(" expression ")"
        else if (tok.getTag().compareTo(Tag.OPEN_PAREN) == 0) {
            if (!eat(Tag.OPEN_PAREN, "factor")) {
                return node;
            }
            node.type = expression().type;
            if (!eat(Tag.CLOSE_PAREN, "factor")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.ID);
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            listTagEsperadas.add(Tag.OPEN_PAREN);
            error("factor");
        }
        return node;
    }

    private Node relop() {
        Node node = new Node();
        if (debug) {
            System.out.println("> relop");
        }
        //relop ::= "=="
        if (tok.getTag().compareTo(Tag.EQ) == 0) {
            if (!eat(Tag.EQ, "relop")) {
                return node;
            }
        }//relop ::= ">"
        else if (tok.getTag().compareTo(Tag.GT) == 0) {
            if (!eat(Tag.GT, "relop")) {
                return node;
            }
        }//relop ::= ">="
        else if (tok.getTag().compareTo(Tag.GE) == 0) {
            if (!eat(Tag.GE, "relop")) {
                return node;
            }
        }//relop ::= "<"
        else if (tok.getTag().compareTo(Tag.LT) == 0) {
            if (!eat(Tag.LT, "relop")) {
                return node;
            }
        }//relop ::= "<="
        else if (tok.getTag().compareTo(Tag.LE) == 0) {
            if (!eat(Tag.LE, "relop")) {
                return node;
            }
        }//relop ::= "!="
        else if (tok.getTag().compareTo(Tag.NOT) == 0) {
            if (!eat(Tag.NE, "relop")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.EQ);
            listTagEsperadas.add(Tag.GT);
            listTagEsperadas.add(Tag.GE);
            listTagEsperadas.add(Tag.LT);
            listTagEsperadas.add(Tag.LE);
            listTagEsperadas.add(Tag.NE);
            error("relop");
        }
        return node;
    }

    private Node addop() {
        Node node = new Node();
        if (debug) {
            System.out.println("> addop");
        }
        //addop ::= "+"
        if (tok.getTag().compareTo(Tag.PLUS) == 0) {
            if (!eat(Tag.PLUS, "addop")) {
                return node;
            }
        }//addop ::= "-"
        else if (tok.getTag().compareTo(Tag.MINUS) == 0) {
            if (!eat(Tag.MINUS, "addop")) {
                return node;
            }
        }//addop ::= "||"
        else if (tok.getTag().compareTo(Tag.OR) == 0) {
            if (!eat(Tag.OR, "addop")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.PLUS);
            listTagEsperadas.add(Tag.MINUS);
            listTagEsperadas.add(Tag.OR);
            error("addop");
        }
        return node;
    }

    private Node mulop() {
        Node node = new Node();
        if (debug) {
            System.out.println("> mulop");
        }
        //mulop ::= "*"
        if (tok.getTag().compareTo(Tag.MULT) == 0) {
            if (!eat(Tag.MULT, "mulop")) {
                return node;
            }
        }//mulop ::= "/"
        else if (tok.getTag().compareTo(Tag.DIV) == 0) {
            if (!eat(Tag.DIV, "mulop")) {
                return node;
            }
        }//mulop ::= "&&"
        else if (tok.getTag().compareTo(Tag.AND) == 0) {
            if (!eat(Tag.AND, "mulop")) {
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.MULT);
            listTagEsperadas.add(Tag.DIV);
            listTagEsperadas.add(Tag.AND);
            error("mulop");
        }
        return node;
    }

    private Node constant() {
        Node node = new Node();
        if (debug) {
            System.out.println("> constant");
        }
        //constant ::= integer_const
        if (tok.getTag().compareTo(Tag.NUM) == 0) {
            node.type = integerConst().type;
        }//constant ::= literal 
        else if (tok.getTag().compareTo(Tag.TEXT) == 0) {
            node.type = literal().type;
        } else {
            listTagEsperadas.add(Tag.NUM);
            listTagEsperadas.add(Tag.TEXT);
            error("constant");
        }
        return node;
    }

    private Node integerConst() {
        Node node = new Node();
        if (debug) {
            System.out.println("> integerConst");
        }
        //integer_const ::= digit {digit} 
        if (tok.getTag().compareTo(Tag.NUM) == 0) {
            if (!eat(Tag.NUM, "integerConst")) {                
                return node;
            }
        } else {
            listTagEsperadas.add(Tag.NUM);
            error("integerConst");
        }
        node.setType(Tag.INT);
        return node;
    }

    private Node literal() {
        Node node = new Node();
        if (debug) {
            System.out.println("> literal");
        }
        // literal ::= " “" {caractere} "”" 
        if (tok.getTag().compareTo(Tag.TEXT) == 0) {
            if (!eat(Tag.TEXT, "literal")) {                
                return node;
            }
            node.setType(Tag.STRING);
        } else {
            listTagEsperadas.add(Tag.TEXT);
            error("literal");
        }         
        return node;
    }

    private Node identifier(Tag type) {
        Node node = new Node();
        Hashtable symbolTable;
        SymbolTableObject obj;
        
        if (debug) {
            System.out.println("> identifier");
        }
        // identifier ::= letter {letter | digit }
        if (tok.getTag().compareTo(Tag.ID) == 0) {
            if (!eat(Tag.ID, "identifier")) {
                return node;
            }
            //Pega o tipo da tabela de símbolos
            symbolTable = lexer.getSymbolTable();
            obj = (SymbolTableObject) symbolTable.get(tok.getLexeme());
            if (obj != null) {
                if (type == Tag.VOID){
                    node.type = obj.getType();
                }else{
                    node.type = type;
                    if(obj.getType() != Tag.VOID){
                        System.out.println("Erro em syntatic, linha 977, deveria ser void");
                        semanticError2(obj.getWord().getLexeme());
                    }else{
                        obj.setType(type);
                        lexer.setSymbolTable(symbolTable);
                    }                    
                }
            }else{
                System.out.println("Erro em syntatic, linha 985");
            }
        } else {
            listTagEsperadas.add(Tag.ID);
            error("identifier");
        }
        return node;
    }
}
