package com.ust;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;


public class Syntaxer {


    //yung susunod na token
    Token currentToken = null;

    //yung parse tree na gagawin
    JTree parseTree = null;

    //mga components ng parseTree
    DefaultMutableTreeNode nodeProgram = null;


    //Initialize Lexer
    Lexer lexer = null;

    //Constructor
    public Syntaxer(String filename) {
        //initialize na yung Lexical Anal
        lexer = new Lexer(filename);
    }

    /*
        Function getParseTree
        para ibabalik na ang punong nabuo
     */

    public JTree getParseTree() {
        parseTree = new JTree(nodeProgram);
        return this.parseTree;
    }


    /*
     * Function program --> Main parsing beings here!
	 * Parses string in the language generated by the rule
	 * <program> -> <statement> { ; <statement> }
	 */

    public void program() {
        System.out.println("PARSING STARTED");

        //Create the main tree node called Program
        nodeProgram = new DefaultMutableTreeNode("Program");

        //Start of analysis
        currentToken = lexer.nextToken();


        if (currentToken.getLexeme().equals("LEGGO")) {


            currentToken = lexer.nextToken();
            stmt();

            while (currentToken.getTokenClass() == Token.DELIMITER) {

                currentToken = lexer.nextToken();

                if (currentToken.getTokenClass() == Token.EOF) break;

                stmt();

            }

            if (currentToken.getTokenClass() != Token.STOP_KEYWORD) {
                System.out.println("STATEMENT SHOULD END WITH SEMICOLON at line:" + currentToken.getLineNumber());
                return;
            } else {
                System.out.println("PARSING COMPLETE");
            }

            System.out.println("PARSE COMPLETE");

        } else {
            System.out.println("ERROR ON LINE " + currentToken.getLineNumber() + "\n LEGGO EXPECTED");
        }

    }

    //<STMT> → <IO> | <CONDSTMT> | <ASSIGNSTMT> | <ITERSTMT> |<LOGICALOP>|<EXPR_STMT>|<DECSTMT>
    public void stmt() {
        System.out.println("STATEMENT");

        switch (currentToken.getTokenClass()) {
            case Token.VARIABLE:
                currentToken = lexer.nextToken();
                assignstmt();
                break;
            case Token.NUMINT:
            case Token.NUMDEC:
                currentToken = lexer.nextToken();
                expr_stmt();
                //TODO
                break;
            case Token.STRING:
                currentToken=lexer.nextToken();
                string_stmt();
                break;
            case Token.MAKELAGAY_KEYWORD://IO
            case Token.MAKELIMBAG_KEYWORD:
                System.out.println("ENTERED IO");
                currentToken=lexer.nextToken();
                expr_stmt();
                System.out.println("EXITED IO");
                break;
            case Token.IFKUNG_KEYWORD:
                currentToken = lexer.nextToken();
                ifKungStmt();
                break;
            case Token.WHILE_KEYWORD:
                currentToken=lexer.nextToken();
                while_stmt();
            case Token.FOR_KEYWORD:
                currentToken  = lexer.nextToken();
                forkung_stmt();
                break;
            case Token.GAWINTHIS_KEYWORD:


                gawinThis_stmt();
                break;

        }
    }
    //TODO
    public void gawinThis_stmt(){

        if(currentToken.getTokenClass()==Token.OPENCURLYBRACKET){

            currentToken= lexer.nextToken();

            stmt();

            while (currentToken.getTokenClass() == Token.DELIMITER) {

                currentToken = lexer.nextToken();

                if (currentToken.getTokenClass() == Token.EOF) break;

                stmt();

            }

            if(currentToken.getTokenClass()== Token.CLOSECURLYBRACKET){
                currentToken = lexer.nextToken();

                if(currentToken.getTokenClass()==Token.WHILE_KEYWORD){

                    currentToken = lexer.nextToken();

                    if(currentToken.getTokenClass()==Token.OPENPARENTHESIS){

                        currentToken = lexer.nextToken();

                        booleanexprstmt();

                        if(currentToken.getTokenClass()==Token.CLOSEPARENTHESIS){

                            currentToken = lexer.nextToken();

                        }else{
                            System.out.println("CLOSE PARENTHESIS EXPECTED at line "+currentToken.getLineNumber());
                        }


                    }else{
                        System.out.println("( EXPECTED at LINE "+ currentToken.getLineNumber());
                    }

                }
                else{
                    System.out.println("EXPECTED WHILE AT LINE"+ currentToken.getLineNumber());
                }

            }else{
                System.out.println("} EXPECTED AT LINE "+ currentToken.getLineNumber());
            }

        }else{
            System.out.println("{ EXPECTED AT LINE"+currentToken.getLineNumber());
        }
    }


    //TODO
    public void forkung_stmt(){

    }

    public void while_stmt(){

        System.out.println("ENTERED WHILESTMT");
        if(currentToken.getTokenClass()==Token.OPENPARENTHESIS){
            currentToken = lexer.nextToken();

            booleanexprstmt();

            if(currentToken.getTokenClass()==Token.CLOSEPARENTHESIS){

                currentToken = lexer.nextToken();

                if(currentToken.getTokenClass()==Token.OPENCURLYBRACKET){

                    currentToken = lexer.nextToken();

                    stmt();


                    while (currentToken.getTokenClass() == Token.DELIMITER) {

                        currentToken = lexer.nextToken();

                        if (currentToken.getTokenClass() == Token.EOF) break;

                        stmt();

                    }

                    if(currentToken.getTokenClass()==Token.CLOSECURLYBRACKET){
                        currentToken = lexer.nextToken();
                    }else{

                        System.out.println("} EXPECTED at line "+currentToken.getLineNumber());
                    }



                }else{
                    System.out.println("{ EXPECTED at line "+currentToken.getLineNumber());
                }


            }else{
                System.out.println(") EXPECTED at line "+currentToken.getLineNumber());
            }

        }else{
            System.out.println("( EXPECTED at line "+currentToken.getLineNumber());
        }


        System.out.println("EXITED WHILESTMT");
    }

    //TODO
    public void input_stmt(){

    }

    //TODO
    public void print_stmt(){

    }


    //TODO
    public void string_stmt(){
        System.out.println("ENTERED STRING STMT");
        while(currentToken.getTokenClass()!=Token.DELIMITER){

            if(currentToken.getTokenClass()==Token.CONCATOP){
                currentToken=lexer.nextToken();
            }
            else if (currentToken.getTokenClass()==Token.NUMDEC
                    |currentToken.getTokenClass()==Token.NUMINT
                    |currentToken.getTokenClass()==Token.VARIABLE){
                currentToken = lexer.nextToken();
            }
            else{
                System.out.println(". OR |" + " EXPECTED");
            }
        }
        
        System.out.println("EXITED STRING STMT");

    }

    /*
     * Function assignment_statement
	 * Parses strings of the langauge generated by the rule
	 * <assign_statement> -> <identifier> is <expr_statement>
	 */

    public void assignstmt() {
        System.out.println("Entered Assignment");

        if (currentToken.getTokenClass() == Token.IS_KEYWORD) {
            currentToken = lexer.nextToken();
            expr_stmt();
        }

        System.out.println("Exited Assignment");
    }

    public void IO() {
        System.out.println("ENTERED IO");

        expr_stmt();

        System.out.println("EXITED IO");

    }

    public void ifKungStmt() {
        System.out.println("ENTERED IF KUNG");
        if (currentToken.getTokenClass() == Token.OPENPARENTHESIS) {

            currentToken = lexer.nextToken();

            booleanexprstmt();

            if (currentToken.getTokenClass() == Token.CLOSEPARENTHESIS) {

                currentToken = lexer.nextToken();

                if (currentToken.getTokenClass() == Token.OPENCURLYBRACKET) {

                    currentToken = lexer.nextToken();

                    stmt();

                    while (currentToken.getTokenClass() == Token.DELIMITER) {

                        currentToken = lexer.nextToken();

                        if (currentToken.getTokenClass() == Token.EOF) {
                            System.out.println("UNEXPECTED END OF FILE");
                            break;
                        }

                        stmt();

                    }

                    if (currentToken.getTokenClass() == Token.CLOSECURLYBRACKET) {
                        currentToken = lexer.nextToken();
                    } else {
                        System.out.println("} EXPECTED AFTER STATEMENTS");
                    }

                } else {
                    System.out.print("{ EXPECTED AT LINE " + currentToken.getLineNumber());
                }

            } else {
                System.out.println(") EXPECTED AT LINE " + currentToken.getLineNumber());
            }

        } else {
            System.out.println("( EXPECTED AT LINE " + currentToken.getLineNumber());
        }
        System.out.println("EXITED IF KUNG");
    }


    //<BOOLEAN> 		→ Yah|Nah|<RELATIONALEXPR>
    public void booleanexprstmt() {
        System.out.println("ENTERED BOOLEAN EXPRSTMT");

        switch (currentToken.getTokenClass()) {
            case Token.YAH_KEYWORD:
            case Token.NAH_KEYWORD:
                currentToken = lexer.nextToken();
                break;
            case Token.VARIABLE:
            case Token.NUMDEC:
            case Token.NUMINT:
                currentToken = lexer.nextToken();
                relationalexpr();
                break;
            default:
                break;
        }
        System.out.println("EXITED BOOLEAN EXPRSTMT");
    }

    public void relationalexpr() {
        System.out.println("ENTERED RELATIONAL EXPR");
        if (currentToken.getTokenClass() == Token.GREATERTHAN
                | currentToken.getTokenClass() == Token.GREATERTHANOREQUAL
                | currentToken.getTokenClass() == Token.LESSTHAN
                | currentToken.getTokenClass() == Token.LESSTHANOREQUAL
                | currentToken.getTokenClass() == Token.ISEQUALOP
                | currentToken.getTokenClass() == Token.NOTEQUALOP) {
            currentToken = lexer.nextToken();

            if (currentToken.getTokenClass() == Token.VARIABLE |
                    currentToken.getTokenClass() == Token.NUMDEC |
                    currentToken.getTokenClass() == Token.NUMINT) {
                currentToken = lexer.nextToken();


            } else {
                System.out.println("IDENTIFIER EXPECTED AT LINE " + currentToken.getLineNumber());
            }

        } else if (currentToken.getTokenClass() == Token.AND_KEYWORD |
                currentToken.getTokenClass() == Token.OR_KEYWORD) {

            currentToken = lexer.nextToken();


            if (currentToken.getTokenClass() == Token.VARIABLE |
                    currentToken.getTokenClass() == Token.NUMDEC |
                    currentToken.getTokenClass() == Token.NUMINT) {
                currentToken = lexer.nextToken();

            } else {
                System.out.println("IDENTIFIER EXPECTED AT LINE " + currentToken.getLineNumber());
            }

        } else {
            System.out.println("LOGICAL OR OPERATIONAL OPERATOR NEEDED AT LINE" + currentToken.getLineNumber());
        }
        System.out.println("EXITED RELATIONAL EXPR");
    }


    /*
     * Function expression_statement
	 * Parses string of the langauges generated by the rule
	 * <expr_stmt> -> <term> {(+|-) <term> }
	 */
//<EXPR_STMT> →  <ARITHMETICEXPR>  |<STRINGEXPR> |<ID>| <ASSIGNEXPR>
    public void expr_stmt() {
        System.out.println("ENTERED EXPRESSION STATEMENT");

        switch (currentToken.getTokenClass()) {
            case Token.ADDOP:
            case Token.SUBOP:
            case Token.MULTOP:
            case Token.DIVOP:
            case Token.POWOP:
                currentToken = lexer.nextToken();
                expr_stmt();
                break;
            case Token.STRING:
                string_stmt();
                //TODO
                break;
            case Token.VARIABLE:
            case Token.NUMDEC:
            case Token.NUMINT:
                //TODO
                break;


        }

        System.out.println("EXITED EXPRESSION STATEMENT");


    }


    //<ID> 			→ <VARIABLE> | <NUMBER> | <STRINGEXPR>
    public void id() {
        System.out.println("ENTERED ID");
        int tokenClass = currentToken.getTokenClass();

        if (tokenClass == Token.VARIABLE|tokenClass==Token.NUMDEC|tokenClass==Token.NUMINT) {
            currentToken = lexer.nextToken();
        } else {
            System.out.println("INVALID ID");
        }
        System.out.println("EXITED ID");
    }



}
