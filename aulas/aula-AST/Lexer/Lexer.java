/*
 Program ::= 'begin' VarDecList ';' AssignStatment 'end'
 VarDecList ::= Variable | Variable ',' VarDecList
 Variable ::= Letter {Letter}
 Letter ::= 'A' | 'B' | ... | 'Z' | 'a' | ... |'z'
 AssignStatment ::= Variable '=' Expr ';'
 Expr ::= Oper  Expr Expr  | Number
 Oper ::= '+' | '-' | '*' | '/'
 Number ::= Digit {Digit}
 Digit ::= '0'| '1' | ... | '9'
 */

package Lexer;

import java.util.*;
import Error.*;

public class Lexer {
    
    public Lexer( char []input, CompilerError error ) {
        this.input = input;
        // add an end-of-file label to make it easy to do the lexer
        input[input.length - 1] = '\0';
        // number of the current line
        lineNumber = 1;
        tokenPos = 0;
        this.error = error;
    }
    
    // contains the keywords
    static private Hashtable<String, Symbol> keywordsTable;
    
    // this code will be executed only once for each program execution
    static {
        keywordsTable = new Hashtable<String, Symbol>();
        keywordsTable.put( "begin", Symbol.BEGIN );
        keywordsTable.put( "end", Symbol.END );
    }
    
    
    public void nextToken() {
        char ch;
        
        while ( (ch = input[tokenPos]) == ' ' || ch == '\r' || ch == '\t' || ch == '\n' )  {
            // count the number of lines
            if ( ch == '\n')
                lineNumber++;
            tokenPos++;
        }
        if ( ch == '\0')
            token = Symbol.EOF;
        else
            if ( input[tokenPos] == '/' && input[tokenPos + 1] == '/' ) {
                // comment found
                while ( input[tokenPos] != '\0'&& input[tokenPos] != '\n' )
                    tokenPos++;
                nextToken();
            }
            else {
                if ( Character.isLetter( ch ) ) {
                    // get an identifier or keyword
                    StringBuffer ident = new StringBuffer();
                    while ( Character.isLetter( input[tokenPos] ) ) {
                        ident.append(input[tokenPos]);
                        tokenPos++;
                    }
                    stringValue = ident.toString();
                    // if identStr is in the list of keywords, it is a keyword !
                    Symbol value = keywordsTable.get(stringValue);
                    if ( value == null )
                        token = Symbol.IDENT;
                    else
                        token = value;
                    if ( Character.isDigit(input[tokenPos]) )
                        error.signal("Word followed by a number");
                }
                else if ( Character.isDigit( ch ) ) {
                    // get a number
                    StringBuffer number = new StringBuffer();
                    while ( Character.isDigit( input[tokenPos] ) ) {
                        number.append(input[tokenPos]);
                        tokenPos++;
                    }
                    token = Symbol.NUMBER;
                    try {
                        numberValue = Integer.valueOf(number.toString()).intValue();
                    } catch ( NumberFormatException e ) {
                        error.signal("Number out of limits");
                    }
                    if ( numberValue >= MaxValueInteger )
                        error.signal("Number out of limits");
                    
                } else {
                    tokenPos++;
                    switch ( ch ) {
                        case '+' :
                            token = Symbol.PLUS;
                            break;
                        case '-' :
                            token = Symbol.MINUS;
                            break;
                        case '*' :
                            token = Symbol.MULT;
                            break;
                        case '/' :
                            token = Symbol.DIV;
                            break;
                        case '=' :
                                token = Symbol.ASSIGN;
                            break;
                        case ',' :
                            token = Symbol.COMMA;
                            break;
                        case ';' :
                            token = Symbol.SEMICOLON;
                            break;
                        default :
                            error.signal("Invalid Character: '" + ch + "'");
                    }
                }
            }
        lastTokenPos = tokenPos - 1;
    }
    
    // return the line number of the last token got with getToken()
    public int getLineNumber() {
        return lineNumber;
    }
    
    public String getCurrentLine() {
        int i = lastTokenPos;
        if ( i == 0 )
            i = 1;
        else
            if ( i >= input.length )
                i = input.length;
        
        StringBuffer line = new StringBuffer();
        // go to the beginning of the line
        while ( i >= 1 && input[i] != '\n' )
            i--;
        if ( input[i] == '\n' )
            i++;
        // go to the end of the line putting it in variable line
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }
        return line.toString();
    }
    
    public String getStringValue() {
        return stringValue;
    }
    
    public int getNumberValue() {
        return numberValue;
    }
    
    public char getCharValue() {
        return charValue;
    }
    // current token
    public Symbol token;
    private String stringValue;
    private int numberValue;
    private char charValue;
    
    private int  tokenPos;
    //  input[lastTokenPos] is the last character of the last token
    private int lastTokenPos;
    // program given as input - source code
    private char []input;
    
    // number of current line. Starts with 1
    private int lineNumber;
    
    private CompilerError error;
    private static final int MaxValueInteger = 32768;
}
