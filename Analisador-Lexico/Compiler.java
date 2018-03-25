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
    
    // Program ::= 'PROGRAM' id 'BEGIN' pgm_body 'END'
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
	
	// id ::= IDENTIFIER
	public void id(){
		// Verifica se é um IDENTIFICADOR
		if (lexer.token != Symbol.IDENTIFIER){
			error.signal("Identificador não encontrado");
		}

		lexer.nextToken();
	}

	// pgm_body ::= decl func_declarations
	public void pgm_body(){
		decl();
		func_declarations();
	}

	// decl ::= string_decl_list {decl} | var_decl_list {decl} | empty
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

	// string_decl_list ::= string_decl {string_decl_tail}
	public void string_decl_list(){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			string_decl();
			string_decl_tail();
		}
	}

	// string_decl ::= STRING id := str ; | empty
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
		}
	}

	// str := STRINGLITERAL
	public void str(){
		// ##################### Não sei como fazer =(
	}

	// str_decl_tail := string_decl {string_decl_tail}
	public void string_decl_tail(){
		string_decl();
		string_decl_tail();		// Estou em duvida dessa chamada #############################
	}
}
    
    
    
