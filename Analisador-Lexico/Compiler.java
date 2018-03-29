import Lexer.*;
import Error.*;

public class Compiler {
	// para geracao de codigo
	public static final boolean GC = false;
	private Lexer lexer;
    private CompilerError error;

    public void compile( char []p_input ) {
        lexer = new Lexer(p_input, error);
        lexer.nextToken();
        program();

        if (lexer.token != Symbol.EOF)
			error.signal("nao chegou no fim do arq");
    }

    // Program -> 'PROGRAM' id 'BEGIN' pgm_body 'END'
    public void program(){
		// Verifica se começou com a palavra PROGRAM
		if (lexer.token != Symbol.PROGRAM){
			error.signal("faltou PROGRAM");
		}

		lexer.nextToken();
		id();

		// Verifica se tem a palavra BEGIN depois do id
		if (lexer.token != Symbol.BEGIN){
			error.signal("faltou BEGIN");
		}

		lexer.nextToken();
		pgm_body();

		// Verifica se tem a palavra END depois do pgm_body
		if (lexer.token != Symbol.END){
			error.signal("faltou END");
		}

		lexer.nextToken();
	}

	// id -> IDENTIFIER
	public void id(){
		// Verifica se é um IDENTIFICADOR
		if (lexer.token != Symbol.IDENTIFIER){
			error.signal("Identificador não encontrado");
		}

		lexer.nextToken();
	}

	// pgm_body -> decl func_declarations
	public void pgm_body(){
		decl();
		func_declarations();
	}

	// decl -> string_decl_list {decl} | var_decl_list {decl} | empty
	public void decl(){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			string_decl_list();

			if (lexer.token == Symbol.STRING || lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				decl();
			}
		}
		// Verifica se é uma variável
		else if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_list();

			if (lexer.token == Symbol.STRING || lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				decl();
			}
		}
	}

	// string_decl_list -> string_decl {string_decl_tail}
	public void string_decl_list(){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			string_decl();

			if (lexer.token == Symbol.STRING){
				string_decl_tail();
			}
		}
	}

	// string_decl -> STRING id := str ; | empty
	public void string_decl(){
		// Verifica se é a palavra STRING
		if (lexer.token == Symbol.STRING){
			lexer.nextToken();
			id();

			if (lexer.token != Symbol.ASSIGN){
				error.signal("faltou :=");
			}

			lexer.nextToken();
			str();

			if (lexer.token != Symbol.SEMICOLON){
				error.signal("faltou ;");
			}

			lexer.nextToken();
		}
	}

	// str -> STRINGLITERAL
	public void str(){
		int i = 0;
		// Se for aspas
		if (lexer.token == Symbol.QUOTA){
			lexer.nextToken();

			// Anda até encontrar a próxima aspas
			while (lexer.token != Symbol.QUOTA){
				if (i == 80){
					error.signal("String literal maior que 80 caracteres");
				}
				else{
					i++;
					lexer.nextToken();
				}
			}

			// Anda, pois encontrou outra aspas
			lexer.nextToken();
		}
		else {
			error.signal("faltou \"");
		}
	}

	// str_decl_tail -> string_decl {string_decl_tail}
	public void string_decl_tail(){
		string_decl();

		if (lexer.token == Symbol.STRING){
			string_decl_tail();
		}
	}

	// var_decl_list -> var_decl {var_decl_tail}
	public void var_decl_list(){
		var_decl();

		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_tail();
		}
	}

	// var_decl -> var_type id_list ; | empty
	public void var_decl(){
		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_type();
			id_list();

			// Verificando se tem o ;
			if (lexer.token != Symbol.SEMICOLON){
				error.signal("faltou ;");
			}
			lexer.nextToken();
		}
	}

	// var_type -> FLOAT | INT
	public void var_type(){
		if (lexer.token != Symbol.FLOAT || lexer.token != Symbol.INT){
			error.signal("tipo de variavel incorreto");
		}
	}

	// any_type -> var_type | VOID
	public void any_type(){
		// Se não for VOID, chama o var_type
		if (lexer.token != Symbol.VOID){
			var_type();
		}

		// Então é VOID
		lexer.nextToken();
	}

	// id_list -> id id_tail
	public void id_list(){
		id();
		id_tail();
	}

	// id_tail -> , id id_tail | empty
	public void id_tail(){
		// Se for uma virgula
		if (lexer.token == Symbol.COMMA){
			lexer.nextToken();
			id();
			id_tail();
		}
	}

	// var_decl_tail -> var_decl {var_decl_tail}
	public void var_decl_tail(){
		var_decl();

		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_tail();
		}
	}

	// param_decl_list -> param_decl param_decl_tail
	public void param_decl_list(){
		param_decl();
		param_decl_tail();
	}

	// param_decl -> var_type id
	public void param_decl(){
		var_type();
		id();
	}

	// param_decl_tail -> , param_decl param_decl_tail | empty
	public void param_decl_tail(){
		// Verifica se é uma virgula
		if (lexer.token == Symbol.COMMA){
			lexer.nextToken();
			param_decl();
			param_decl_tail();
		}
	}

	// func_declarations -> func_decl {func_decl_tail}
	public void func_declarations(){
		func_decl();

		if (lexer.token == Symbol.FUNCTION){
			func_decl_tail();
		}
	}

	// func_decl -> FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
	public void func_decl(){
		// Verifica se tem o FUNCTION
		if (lexer.token == Symbol.FUNCTION){
			lexer.nextToken();
			any_type();
			id();

			// Verifica se tem o '(' depois do function
			if (lexer.token != Symbol.OBRACK){
				error.signal("faltou (");
			}

			lexer.nextToken();

			while (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT || lexer.token == Symbol.IDENTIFIER){
				param_decl_list();
			}

			// Verifica se tem o ')' depois do param_decl_list
			if (lexer.token != Symbol.CBRACK){
				error.signal("faltou )");
			}

			lexer.nextToken();

			// Verifica se tem a palavra BEGIN depois do ')'
			if (lexer.token != Symbol.BEGIN){
				error.signal("faltou BEGIN");
			}

			lexer.nextToken();
			func_body();

			// Verifica se tem a palavra END depois do func_body
			if (lexer.token != Symbol.END){
				error.signal("faltou END");
			}

			lexer.nextToken();
		}
	}

	// func_decl_tail -> func_decl {func_decl_tail}
	public void func_decl_tail(){
		func_decl();

		if (lexer.token == Symbol.FUNCTION){
			func_decl_tail();
		}
	}

	// func_body -> decl stmt_list
	public void func_body(){
		decl();
		stmt_lis();
	}

	// stmt_list -> stmt stmt_tail | empty
	public void stmt_list(){
		if (lexer.token == Symbol.IDENTIFIER || lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR){
			stmt();
			stmt_tail();
		}
	}

	// stmt_tail -> stmt stmt_tail | empty
	public void stmt_tail(){
		if (lexer.token == Symbol.IDENTIFIER || lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR){
			stmt();
			stmt_tail();
		}
	}

	// stmt -> id assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt | id call_expr ;
	public void stmt(){
		if (lexer.token == Symbol.IDENTIFIER){
			id();
			if (lexer.token == Symbol.LPAR)
				call_expr();	//call_expr -> id ( {expr_list} )
			else if (lexer.token == Symbol.ASSIGN)
				assign_stmt();	//assign_stmt -> assign_expr ;
								//assign_expr -> id := expr
			else
				error.signal("Faltou := ou (")
		}
		//COLOCAR NO CASO DE TESTE

		else if (lexer.token == Symbol.READ)
		 	read_stmt();

		else if (lexer.token == Symbol.WRITE)
		 	write_stmt();

		else if (lexer.token == Symbol.RETURN)
		 	return_stmt();

		else if (lexer.token == Symbol.IF)
		 	if_stmt();

		else if (lexer.token == Symbol.FOR)
			for_stmt();
	}

	/* Basic Statements */
	// assign_stmt -> assign_expr ;
	public void assign_stmt(){
		assign_expr();
		if (lexer.token != Symbol.SEMICOLON)
			error.signal("Faltou ;");
		lexer.nextToken();
	}

	// assign_expr -> := expr
	public void assign_expr(){
		if (lexer.token != Symbol.ASSIGN)
			error.signal("Faltou :=");
		lexer.nextToken();
		expr();
	}

	// read_stmt -> READ ( id_list );
	public void read_stmt(){
		if (lexer.token != Symbol.READ)
			error.signal("Faltou READ");
		lexer.nextToken();

		if (lexer.token != Symbol.LPAR)
			error.signal("Faltou (");
		lexer.nextToken();

		if (lexer.token != Symbol.IDENTIFIER)
			error.signal("Faltou variavel");
		id_list();

		if (lexer.token != Symbol.RPAR)
			error.signal("Faltou )");
		lexer.nextToken();

		if (lexer.token != Symbol.SEMICOLON)
			error.signal("Faltou ;");
		lexer.nextToken();
	}

	// write_stmt -> WRITE ( id_list );
	public void write_stmt(){
		if (lexer.token != Symbol.WRITE)
			error.signal("Faltou WRITE");
		lexer.nextToken();

		if (lexer.token != Symbol.LPAR)
			error.signal("Faltou (");
		lexer.nextToken();

		if (lexer.token != Symbol.IDENTIFIER)
			error.signal("Faltou variavel");
		id_list();

		if (lexer.token != Symbol.RPAR)
			error.signal("Faltou )");
		lexer.nextToken();

		if (lexer.token != Symbol.SEMICOLON)
			error.signal("Faltou ;");
		lexer.nextToken();
	}

	// return_stmt -> RETURN expr ;
	public void return_stmt(){
		if (lexer.token != Symbol.RETURN)
			error.signal("Faltou RETURN");
		lexer.nextToken();
		expr();

		if (lexer.token != Symbol.SEMICOLON)
			error.signal("Faltou ;");
		lexer.nextToken();
	}


	/* Expressions */

	// expr -> factor expr_tail
	public void expr(){
		factor();
		expr_tail();
	}

	// expr_tail -> addop factor expr_tail | empty
	public void expr_tail(){
		if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS){
			addop();
			factor();
			expr_tail();
		}
	}

	// factor -> postfix_expr factor_tail
	public void factor(){

	}

	// factor_tail -> mulop postfix_expr factor_tail | empty
	public void factor_tail(){

	}

	// postfix_expr -> primary | call_expr
	public void postfix_expr(){

	}

	// call_expr -> ( {expr_list} )
	public void call_expr(){

	}

	// expr_list -> expr expr_list_tail
	public void expr_list(){

	}

	// expr_list_tail -> , expr expr_list_tail | empty
	public void expr_list_tail(){

	}

	// primary -> (expr) | id | INTLITERAL | FLOATLITERAL
	public void primary(){

	}

	// addop -> + | -
	public void addop(){

	}

	// mulop -> * | /
	public void mulop(){

	}


	/* Complex Statements and Condition */

	// if_stmt -> IF ( cond ) THEN stmt_list else_part ENDIF
	public void if_stmt(){

	}

	// else_part -> ELSE stmt_list | empty
	public void else_part(){

	}

	// cond -> expr compop expr
	public void cond(){

	}

	// compop -> < | > | =
	public void compop(){

	}

	// for_stmt -> FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
	public void for_stmt(){

	}}
