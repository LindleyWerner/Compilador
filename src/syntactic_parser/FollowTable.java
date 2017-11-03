/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package syntactic_parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lexical_analyzer.Tag;

/**
 *
 * @author Lindley and Nícolas
 */
public class FollowTable {

    // create map to store    
    Map<String, List<Tag>> follow = new HashMap<>();

    public FollowTable() {
        // program
        List<Tag> program = new ArrayList<>();
        program.add(Tag.EOF);
        follow.put("program", program);

        // declList
        List<Tag> declList = new ArrayList<>();
        declList.add(Tag.ID);
        declList.add(Tag.IF);
        declList.add(Tag.DO);
        declList.add(Tag.SCAN);
        declList.add(Tag.PRINT);
        follow.put("declList", declList);

        // decl
        List<Tag> decl = new ArrayList<>();
        decl.add(Tag.INT);
        decl.add(Tag.STRING);
        decl.add(Tag.ID);
        decl.add(Tag.IF);
        decl.add(Tag.DO);
        decl.add(Tag.SCAN);
        decl.add(Tag.PRINT);
        follow.put("decl", decl);

        // identList
        List<Tag> identList = new ArrayList<>();
        identList.add(Tag.DOT_COMMA);
        follow.put("identList", identList);

        //type
        List<Tag> type = new ArrayList<>();
        type.add(Tag.ID);
        follow.put("type", type);

        //stmtList
        List<Tag> stmtList = new ArrayList<>();
        stmtList.add(Tag.END);
        stmtList.add(Tag.ELSE);
        stmtList.add(Tag.WHILE);
        follow.put("stmtList", stmtList);

        //stmt
        List<Tag> stmt = new ArrayList<>();
        stmt.add(Tag.ID);
        stmt.add(Tag.IF);
        stmt.add(Tag.DO);
        stmt.add(Tag.SCAN);
        stmt.add(Tag.PRINT);
        stmt.add(Tag.END);
        stmt.add(Tag.ELSE);
        stmt.add(Tag.WHILE);
        follow.put("stmt", stmt);

        //assingStmt
        List<Tag> assingStmt = new ArrayList<>();
        assingStmt.add(Tag.DOT_COMMA);
        follow.put("assingStmt", assingStmt);

        //ifStmt
        List<Tag> ifStmt = new ArrayList<>();
        ifStmt.add(Tag.ID);
        ifStmt.add(Tag.IF);
        ifStmt.add(Tag.DO);
        ifStmt.add(Tag.SCAN);
        ifStmt.add(Tag.PRINT);
        follow.put("ifStmt", ifStmt);

        //ifStmtPrime
        List<Tag> ifStmtPrime = new ArrayList<>();
        ifStmtPrime.add(Tag.ID);
        ifStmtPrime.add(Tag.IF);
        ifStmtPrime.add(Tag.DO);
        ifStmtPrime.add(Tag.SCAN);
        ifStmtPrime.add(Tag.PRINT);
        follow.put("ifStmtPrime", ifStmtPrime);

        //condition
        List<Tag> condition = new ArrayList<>();
        condition.add(Tag.THEN);
        condition.add(Tag.END);
        follow.put("condition", condition);

        //whileStmt
        List<Tag> whileStmt = new ArrayList<>();
        whileStmt.add(Tag.ID);
        whileStmt.add(Tag.IF);
        whileStmt.add(Tag.DO);
        whileStmt.add(Tag.SCAN);
        whileStmt.add(Tag.PRINT);
        whileStmt.add(Tag.END);
        whileStmt.add(Tag.ELSE);
        whileStmt.add(Tag.WHILE);
        follow.put("whileStmt", whileStmt);

        //stmtSufix
        List<Tag> stmtSufix = new ArrayList<>();
        stmtSufix.add(Tag.ID);
        stmtSufix.add(Tag.IF);
        stmtSufix.add(Tag.DO);
        stmtSufix.add(Tag.SCAN);
        stmtSufix.add(Tag.PRINT);
        stmtSufix.add(Tag.END);
        stmtSufix.add(Tag.ELSE);
        stmtSufix.add(Tag.WHILE);
        follow.put("stmtSufix", stmtSufix);

        //readStmt
        List<Tag> readStmt = new ArrayList<>();
        readStmt.add(Tag.DOT_COMMA);
        follow.put("readStmt", readStmt);

        //writeStmt
        List<Tag> writeStmt = new ArrayList<>();
        writeStmt.add(Tag.DOT_COMMA);
        follow.put("writeStmt", writeStmt);

        //writable
        List<Tag> writable = new ArrayList<>();
        writable.add(Tag.CLOSE_PAREN);
        follow.put("writable", writable);

        //expression
        List<Tag> expression = new ArrayList<>();
        expression.add(Tag.CLOSE_PAREN);
        expression.add(Tag.THEN);
        expression.add(Tag.END);
        follow.put("expression", expression);

        //expressionPrime
        List<Tag> expressionPrime = new ArrayList<>();
        expressionPrime.add(Tag.CLOSE_PAREN);
        expressionPrime.add(Tag.THEN);
        expressionPrime.add(Tag.END);
        follow.put("expressionPrime", expressionPrime);

        //simpleExpr
        List<Tag> simpleExpr = new ArrayList<>();
        simpleExpr.add(Tag.DOT_COMMA);
        simpleExpr.add(Tag.CLOSE_PAREN);
        simpleExpr.add(Tag.THEN);
        simpleExpr.add(Tag.END);
        simpleExpr.add(Tag.GT);
        simpleExpr.add(Tag.EQ);
        simpleExpr.add(Tag.GE);
        simpleExpr.add(Tag.LT);
        simpleExpr.add(Tag.LE);
        simpleExpr.add(Tag.NOT);
        follow.put("simpleExpr", simpleExpr);

        //simpleExprPrime
        List<Tag> simpleExprPrime = new ArrayList<>();
        simpleExprPrime.add(Tag.DOT_COMMA);
        simpleExprPrime.add(Tag.CLOSE_PAREN);
        simpleExprPrime.add(Tag.THEN);
        simpleExprPrime.add(Tag.END);
        simpleExprPrime.add(Tag.GT);
        simpleExprPrime.add(Tag.EQ);
        simpleExprPrime.add(Tag.GE);
        simpleExprPrime.add(Tag.LT);
        simpleExprPrime.add(Tag.LE);
        simpleExprPrime.add(Tag.NOT);
        follow.put("simpleExprPrime", simpleExprPrime);

        //term
        List<Tag> term = new ArrayList<>();
        term.add(Tag.DOT_COMMA);
        term.add(Tag.CLOSE_PAREN);
        term.add(Tag.THEN);
        term.add(Tag.END);
        term.add(Tag.GT);
        term.add(Tag.EQ);
        term.add(Tag.GE);
        term.add(Tag.LT);
        term.add(Tag.LE);
        term.add(Tag.NOT);
        term.add(Tag.MINUS);
        term.add(Tag.OR);
        term.add(Tag.PLUS);
        follow.put("term", term);

        //termPrime
        List<Tag> termPrime = new ArrayList<>();
        termPrime.add(Tag.DOT_COMMA);
        termPrime.add(Tag.CLOSE_PAREN);
        termPrime.add(Tag.THEN);
        termPrime.add(Tag.END);
        termPrime.add(Tag.GT);
        termPrime.add(Tag.EQ);
        termPrime.add(Tag.GE);
        termPrime.add(Tag.LT);
        termPrime.add(Tag.LE);
        termPrime.add(Tag.NOT);
        termPrime.add(Tag.MINUS);
        termPrime.add(Tag.OR);
        termPrime.add(Tag.PLUS);
        follow.put("termPrime", termPrime);

        //factorA
        List<Tag> factorA = new ArrayList<>();
        factorA.add(Tag.DOT_COMMA);
        factorA.add(Tag.CLOSE_PAREN);
        factorA.add(Tag.THEN);
        factorA.add(Tag.END);
        factorA.add(Tag.GT);
        factorA.add(Tag.EQ);
        factorA.add(Tag.GE);
        factorA.add(Tag.LT);
        factorA.add(Tag.LE);
        factorA.add(Tag.NOT);
        factorA.add(Tag.MINUS);
        factorA.add(Tag.OR);
        factorA.add(Tag.PLUS);
        factorA.add(Tag.MULT);
        factorA.add(Tag.DIV);
        factorA.add(Tag.AND);
        follow.put("factorA", factorA);

        //factor
        List<Tag> factor = new ArrayList<>();
        factor.add(Tag.DOT_COMMA);
        factor.add(Tag.CLOSE_PAREN);
        factor.add(Tag.THEN);
        factor.add(Tag.END);
        factor.add(Tag.GT);
        factor.add(Tag.EQ);
        factor.add(Tag.GE);
        factor.add(Tag.LT);
        factor.add(Tag.LE);
        factor.add(Tag.NOT);
        factor.add(Tag.MINUS);
        factor.add(Tag.OR);
        factor.add(Tag.PLUS);
        factor.add(Tag.MULT);
        factor.add(Tag.DIV);
        factor.add(Tag.AND);
        follow.put("factor", factor);

        //relop
        List<Tag> relop = new ArrayList<>();
        relop.add(Tag.ID);
        relop.add(Tag.NUM);
        relop.add(Tag.TEXT);
        relop.add(Tag.OPEN_PAREN);
        relop.add(Tag.NOT);
        relop.add(Tag.MINUS);
        follow.put("relop", relop);

        //addop
        List<Tag> addop = new ArrayList<>();
        addop.add(Tag.ID);
        addop.add(Tag.NUM);
        addop.add(Tag.TEXT);
        addop.add(Tag.OPEN_PAREN);
        addop.add(Tag.NOT);
        addop.add(Tag.MINUS);
        follow.put("addop", addop);

        //mulop
        List<Tag> mulop = new ArrayList<>();
        mulop.add(Tag.ID);
        mulop.add(Tag.NUM);
        mulop.add(Tag.TEXT);
        mulop.add(Tag.OPEN_PAREN);
        follow.put("mulop", mulop);

        //constant
        List<Tag> constant = new ArrayList<>();
        constant.add(Tag.DOT_COMMA);
        constant.add(Tag.CLOSE_PAREN);
        constant.add(Tag.THEN);
        constant.add(Tag.END);
        constant.add(Tag.GT);
        constant.add(Tag.EQ);
        constant.add(Tag.GE);
        constant.add(Tag.LT);
        constant.add(Tag.LE);
        constant.add(Tag.NOT);
        constant.add(Tag.MINUS);
        constant.add(Tag.OR);
        constant.add(Tag.PLUS);
        constant.add(Tag.MULT);
        constant.add(Tag.DIV);
        constant.add(Tag.AND);
        follow.put("constant", constant);

        //integerConst
        List<Tag> integerConst = new ArrayList<>();
        integerConst.add(Tag.DOT_COMMA);
        integerConst.add(Tag.CLOSE_PAREN);
        integerConst.add(Tag.THEN);
        integerConst.add(Tag.END);
        integerConst.add(Tag.GT);
        integerConst.add(Tag.EQ);
        integerConst.add(Tag.GE);
        integerConst.add(Tag.LT);
        integerConst.add(Tag.LE);
        integerConst.add(Tag.NOT);
        integerConst.add(Tag.MINUS);
        integerConst.add(Tag.OR);
        integerConst.add(Tag.PLUS);
        integerConst.add(Tag.MULT);
        integerConst.add(Tag.DIV);
        integerConst.add(Tag.AND);
        follow.put("integerConst", integerConst);

        //literal
        List<Tag> literal = new ArrayList<>();
        literal.add(Tag.DOT_COMMA);
        literal.add(Tag.CLOSE_PAREN);
        literal.add(Tag.THEN);
        literal.add(Tag.END);
        literal.add(Tag.GT);
        literal.add(Tag.EQ);
        literal.add(Tag.GE);
        literal.add(Tag.LT);
        literal.add(Tag.LE);
        literal.add(Tag.NOT);
        literal.add(Tag.MINUS);
        literal.add(Tag.OR);
        literal.add(Tag.PLUS);
        literal.add(Tag.MULT);
        literal.add(Tag.DIV);
        literal.add(Tag.AND);
        follow.put("literal", literal);

        //identifier
        List<Tag> identifier = new ArrayList<>();
        identifier.add(Tag.DOT_COMMA);
        identifier.add(Tag.CLOSE_PAREN);
        identifier.add(Tag.THEN);
        identifier.add(Tag.END);
        identifier.add(Tag.GT);
        identifier.add(Tag.EQ);
        identifier.add(Tag.GE);
        identifier.add(Tag.LT);
        identifier.add(Tag.LE);
        identifier.add(Tag.NOT);
        identifier.add(Tag.MINUS);
        identifier.add(Tag.OR);
        identifier.add(Tag.PLUS);
        identifier.add(Tag.MULT);
        identifier.add(Tag.DIV);
        identifier.add(Tag.AND);
        identifier.add(Tag.ASSIGN);
        identifier.add(Tag.COMMA);
        follow.put("identifier", identifier);
    }

    public List<Tag> getFollows(String key) {
        return follow.get(key);
    }
}
