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

	// apenas para verificacao lexica
	public static final boolean DEBUGLEXER = true;

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

        //adicionados
        keywordsTable.put( "function", Symbol.FUNCTION);
        keywordsTable.put( "read", Symbol.READ);
        keywordsTable.put( "write", Symbol.WRITE);
        keywordsTable.put( "if", Symbol.IF);
        keywordsTable.put( "then", Symbol.THEN);
        keywordsTable.put( "else", Symbol.ELSE);
        keywordsTable.put( "endif", Symbol.ENDIF);
        keywordsTable.put( "return", Symbol.RETURN);
        keywordsTable.put( "for", Symbol.FOR);
        keywordsTable.put( "endfor", Symbol.ENDFOR);
        keywordsTable.put( "float", Symbol.FLOAT);
        keywordsTable.put( "int", Symbol.INT);
        keywordsTable.put( "void", Symbol.VOID);
        keywordsTable.put( "string", Symbol.STRING);

        //nao tenho certeza
        keywordsTable.put( "eof", Symbol.EOF);
        keywordsTable.put( "Ident", Symbol.IDENT);
        keywordsTable.put( "IntNumber", Symbol.INTLITERAL);
        keywordsTable.put( "FloatNumber", Symbol.FLOATLITERAL);
        keywordsTable.put( "StringLiteral", Symbol.STRINGLITERAL);
        keywordsTable.put( "program", Symbol.PROGRAM);
    }

    public void nextToken() {
    	//Pula espaços e quebra de linhas
		while (input[tokenPos] == ' ' || input[tokenPos] == '\n' || input[tokenPos] != '\t'){
			if (input[tokenPos] == '\n'){
				lineNumber++;
			}
			tokenPos++;
		}

		//verifica se chegou no fim
		if (input[tokenPos] == '\0'){
			token = Symbol.EOF;
			return;
		}

		//Verifica linhas comentadas
		if (input[tokenPos] == '/' && input[tokenPos + 1] == '/'){
			while (input[tokenPos] != '\n'){
				tokenPos++;
			}

			nextToken();
			return;
		}

		//Verifica se é um número
		String aux;
		while(Character.isDigit(input[tokenPos])){
			aux = aux + input[tokenPos];
			tokenPos++;
		}

		if (!aux.equal("")){
			numberValue = Integer.parseInt(aux);
			if (numberValue > MaxValueInteger){
				error.signal("Numero maior que 32768");
			}
			token = Symbol.NUMBER;
		}
		//Verifica se é string
		else {
			while(Character.isLetter(input[tokenPos])){
				aux = aux + input[tokenPos];
				tokenPos++;
			}

			if (!aux.equals("")){
				Symbol temp;

				temp = keywordsTable.get(aux);
				//se nao for palavra reservada, é nome de variavel (Duvida: entao nome de variavel nao pode ter numero?)
				if (temp == null){
					token = Symbol.IDENT;
					stringValue = aux;
				}
				else {
					token = temp;





				
				//define qual palavra reservada é (NAO TENHO CERTEZA DE NADA)
					switch (token){
						case 'begin':
							token = Symbol.BEGIN;
						break;

						case 'end':
							token = Symbol.END;
						break;

						case 'function':
							token = Symbol.FUNCTION;
						break;

						case 'read':
							token = Symbol.READ;
						break;

						case 'write':
							token = Symbol.WRITE;
						break;

						case 'if':
							token = Symbol.IF;
						break;

						case 'then':
							token = Symbol.THEN;
						break;

						case 'else':
							token = Symbol.ELSE;
						break;

						case 'endif':
							token = Symbol.ENDIF;
						break;

						case 'return':
							token = Symbol.RETURN;
						break;

						case 'for':
							token = Symbol.FOR;
						break;

						case 'endfor':
							token = Symbol.ENDFOR;
						break;

						case 'float':
							token = Symbol.FLOAT;
						break;

						case 'int':
							token = Symbol.INT;
						break;

						case 'void':
							token = Symbol.VOID;
						break;

						case 'string':
							token = Symbol.STRING;
						break;

						case 'eof':
							token = Symbol.EOF;
						break;

						case 'Ident':
							token = Symbol.IDENT;
						break;

						case 'IntNumber':
							token = Symbol.INTLITERAL;
						break;

						case 'FloatNumber':
							token = Symbol.FLOATLITERAL;
						break;

						case 'StringLiteral':
							token = Symbol.STRINGLITERAL;
						break;

						case 'program':
							token = Symbol.PROGRAM;
						break;

					default:
						error.signal("Erro lexico");
				}
				//FIM DA PARTE Q EU NAO TENHO CERTEZA NENHUMA



			}
			//verifica se eh um dos operadores
			else {
				switch (input[tokenPos]){
					case '+':
						token = Symbol.PLUS;
					break;

					case '-':
						token = Symbol.MINUS;
					break;

					case '*':
						token = Symbol.MULT;
					break;

					case '/':
						token = Symbol.DIV;
					break;

					//se pa isso foi feito errado na sala
					// case '=':
					// 	token = Symbol.ASSIGN;
					// break;

					//eh assim
					case ':':
						token = Symbol.ASSIGN;
						tokenPos++;	//n tenho certeza disso
					break;

					case '=':
						token = Symbol.EQUAL;
					break;

					case ',':
						token = Symbol.COMMA;
					break;

					case ';':
						token = Symbol.SEMICOLON;
					break;

					//tava faltando esses
					case '(':
						token = Symbol.LPAR;
					break;

					case ')':
						token = Symbol.RPAR;
					break;

					case '>':
						token = Symbol.GT;
					break;

					case '<':
						token = Symbol.LT;
					break;

					default:
						error.signal("Erro lexico");
				}
				tokenPos++;
			}
		}

		if (DEBUGLEXER)
			System.out.println(token.toString());
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
