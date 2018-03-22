/*
    comp5
 Vamos aprimorar o analisador lexico. Na linguagem abaixo, temos palavras-chave com mais que um caracter, nomes de variaveis formado por 1 ou mais caracteres e numeros inteiros positivos. Note que nesta linguagem eh necessario declarar pelo menos uma variavel. Alem disso, esta linguagem aceita como comentario uma linha que inicia com //.
 A mensagem de erro eh de responsabilidade da classe CompileError.
 O codigo fonte pode ser escrito em um arquivo separado.
 Utilize o GC para geracao de codigo em C e DEBUGLEXER para imprimir os tokens reconhecidos.

    Grammar:
       Program ::= 'begin' VarDecList ';' AssignStatement 'end'
       VarDecList ::= Variable | Variable ',' VarDecList
       Variable ::= Letter {Letter}
       Letter ::= 'A' | 'B' | ... | 'Z' | 'a' | ... |'z'
       AssignStatement ::= Variable '=' Expr ';'
       Expr ::= Oper  Expr Expr  | Number | Variable
       Oper ::= '+' | '-' | '*' | '/'
       Number ::= Digit {Digit}
       Digit ::= '0'| '1' | ... | '9'
*/

import Lexer.*;
import Error.*;

public class Compiler {
	private Lexer lexer;
	private CompilerError error;

	// para geracao de codigo
	public static final boolean GC = false;

    public void compile( char []p_input ) {
        lexer = new Lexer(p_input, error);
        lexer.nextToken();
        program();
    }

    // Program ::= 'begin' VarDecList ';' AssignStatement
    public void program(){
		if (lexer.token != Symbol.BEGIN){
			error.signal("Faltou begin");
		}
		lexer.nextToken();

		varDecList();

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Faltou ;");
		}
		lexer.nextToken();

		assignStatement();

		if (lexer.token != Symbol.END){
			error.signal("Faltou end");
		}
		lexer.nextToken();
    }

	// VarDecList ::= Variable | Variable ',' VarDecList
	public void varDecList(){
		if (lexer.token != Symbol.IDENT){
			error.signal("ident não encontrado");
		}
		lexer.nextToken();

		if (lexer.token == Symbol.COMMA){
			lexer.nextToken();
			varDecList();
		}
	}

    //AssignStatement ::= Variable '=' Expr ';'
    public void assignStatement(){
		if (lexer.token != Symbol.IDENT){
			error.signal("ident não encontrado");
		}
		lexer.nextToken();

		if (lexer.token != Symbol.ASSIGN){
			error.signal("Faltou =");
		}
		lexer.nextToken();

		expr();

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Faltou ;");
		}
    }

    // Expr ::= Oper  Expr Expr  | Number | Variable
    public void expr(){
		if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS ||
			lexer.token == Symbol.MULT || lexer.token == Symbol.DIV){
			lexer.nextToken();
			expr();
			expr();
		}
		else if (lexer.token == Symbol.NUMBER){
			lexer.nextToken();
		}
		else if (lexer.token == Symbol.IDENT){
			lexer.nextToken();
		}
		else{
			error.signal("Expressão errada");
		}
    }
}
