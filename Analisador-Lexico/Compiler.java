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
			decl();
		}

		// Verifica se é uma variável
		if (lexer.token == Symbol.IDENTIFIER){
			var_decl_list();
			decl();
		}
	}

	// string_decl_list -> string_decl {string_decl_tail}
	public void string_decl_list(){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			string_decl();
			string_decl_tail();
		}
	}

	// string_decl -> STRING id := str ; | empty
	public void string_decl(){
		// Verifica se é a palavra STRING
		if (lexer.token == Symbol.STRING){	// Duvida se é assim que faz um OU com empty
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
		}
	}

	// str -> STRINGLITERAL
	public void str(){
		// Se for aspas
		if (lexer.token == Symbol.QUOTA){
			lexer.nextToken();

			// Anda até encontrar a próxima aspas
			while (lexer.token != Symbol.QUOTA){
				lexer.nextToken();
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

		while (lexer.token == Symbol.STRING){ 	// Estou em duvida dessa chamada #############################
			string_decl_tail();					// Coloca direto o string_decl dentro do while???
		}
	}

	// var_decl_list -> var_decl {var_decl_tail}
	public void var_decl_list(){
		var_decl();

		while (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){	// Mesma duvida do método string_decl_tail
			var_decl_tail();
		}
	}

	// var_decl -> var_type id_list ; | empty
	public void var_decl(){
		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){	// Duvida se é assim que faz um OU com empty
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
	}

	// id_list -> id id_tail
	public void id_list(){
		id();
		id_tail();
	}

	// id_tail -> , id id_tail | empty
	public void id_tail(){
		// Se for uma virgula
		if (lexer.token == Symbol.COMMA){	// Duvida se é assim que faz um OU com empty
			lexer.nextToken();
			id();
			id_tail();
		}
	}

	// var_decl_tail -> var_decl {var_decl_tail}
	public void var_decl_tail(){
		var_decl();
		
		while (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){	// Mesma duvida do método string_decl_tail
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
		if (lexer.token == Symbol.COMMA){	// Duvida se é assim que faz um OU com empty
			lexer.nextToken();
			param_decl();
			param_decl_tail();
		}
	}

	// func_declarations -> func_decl {func_decl_tail}
	public void func_declarations(){
		func_decl();

		while (lexer.token == Symbol.FUNCTION){		// Mesma duvida do método string_decl_tail
			func_decl_tail();
		}
	}

	// func_decl -> FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
	public void func_decl(){
		// Verifica se tem o FUNCTION
		if (lexer.token == Symbol.FUNCTION){	// Duvida se é assim que faz um OU com empty
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

		if (lexer.token == Symbol.FUNCTION){		// Mesma duvida do método string_decl_tail
			func_decl_tail();
		}
	}

	// func_body -> decl stmt_list
	public void func_body(){
		decl();
		stmt_lis();
	}
}
    
    
    
