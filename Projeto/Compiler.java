import Lexer.*;
import Error.*;
import AST.*;
import java.util.*;

public class Compiler {
	// para geracao de codigo
	public static final boolean GC = false;
	private Lexer lexer;
	private CompilerError error;
	private SymbolTable table;
	private int GLOBAL = 0, LOCAL = 1, EXISTS = 2;
	private Function FUNC;
	private Variable ATTR;

    public PgmBody compile( char []p_input ) {
		error = new CompilerError(null);
		lexer = new Lexer(p_input, error);
		table = new SymbolTable();
		error.setLexer(lexer);
        lexer.nextToken();
        PgmBody pg = program();

        if (lexer.token != Symbol.EOF)
			error.signal("Didn't find the end of the file");

		return pg;
    }

	/* =================================================*/
	/* 						Program						*/
	/* =================================================*/

    // Program -> 'PROGRAM' id 'BEGIN' pgm_body 'END'
    public PgmBody program(){
		// Verifica se começou com a palavra PROGRAM
		if (lexer.token != Symbol.PROGRAM){
			error.signal("Expected 'PROGRAM' but found '" + lexer.getStringValue() + "'\nFirst word should be 'PROGRAM'");
		}

		lexer.nextToken();
		id();

		// Verifica se tem a palavra BEGIN depois do id
		if (lexer.token != Symbol.BEGIN){
			error.signal("Expected 'BEGIN' but found '" + lexer.getStringValue() + "'\nThe program need to be between 'BEGIN' and 'END' words");
		}

		lexer.nextToken();
		PgmBody pg = pgm_body();

		// Verifica se tem a palavra END depois do pgm_body
		if (lexer.token != Symbol.END){
			error.signal("Expected 'END' but found '" + lexer.getStringValue() + "'\nThe program need to be between 'BEGIN' and 'END' words");
		}

		lexer.nextToken();

		return pg;
	}

	// id -> IDENT
	public String id(){
		// Verifica se é um IDENTIFICADOR
		if (lexer.token != Symbol.IDENT){
			error.signal("Expected variable but found '" + lexer.getStringValue() + "'");
		}

		String var = lexer.getStringValue();
		lexer.nextToken();

		return var;
	}

	// pgm_body -> decl func_declarations
	public PgmBody pgm_body(){
		ArrayList<Variable> var = new ArrayList<Variable>();
		ArrayList<Function> func = new ArrayList<Function>();
		
		//flag de variavel global
		decl(var, GLOBAL);
		func_declarations(func);
		return new PgmBody(var, func);
	}

	// decl -> string_decl_list {decl} | var_decl_list {decl} | empty
	public void decl(ArrayList<Variable> var, int flag){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			string_decl_list(var, flag);

			if (lexer.token == Symbol.STRING || lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				decl(var, flag);
			}
		}
		// Verifica se é uma variável
		else if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_list(var, flag);

			if (lexer.token == Symbol.STRING || lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				decl(var, flag);
			}
		}
	}

	/* =================================================*/
	/* 				Global String Declaration			*/
	/* =================================================*/

	// true -> variavel global
	// false -> variavel local
	// string_decl_list -> string_decl {string_decl_tail}
	public void string_decl_list(ArrayList<Variable> var, int flag){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			Variable str = string_decl();

			// Se a chamada veio de uma declaração global
			if (flag == GLOBAL){
				// Insere na tabela global
				table.putGlobal(str.getVar(), str);
			}
			else if (flag == LOCAL){
				// Caso contrário na tabela da função local
				table.putLocal(str.getVar(), str);
			}

			var.add(str);

			if (lexer.token == Symbol.STRING){
				string_decl_tail(var, flag);
			}
		}
	}

	// string_decl -> STRING id := str ; | empty
	public Variable string_decl(){
		// Verifica se é a palavra STRING
		if (lexer.token == Symbol.STRING){
			lexer.nextToken();

			Variable var = new Variable(id(), Symbol.STRING, null);

			// Análise semântica, verificamos se essa variavél ja foi declarada anteriormente
			// Verificando se ela existe na tabela de variáveis globais
			if (table.returnGlobal(var.getVar()) != null){
				error.signal("Error, variable '" + var.getVar() + "' has already been declared as global variable");
			}

			// Verificando se ela existe na tabela de variáveis locais
			if (table.returnLocal(var.getVar()) != null){
				error.signal("Error, variable '" + var.getVar() + "' has already been declared as local variable");
			}

			if (lexer.token != Symbol.ASSIGN){
				error.signal("Expected ':=' but found '" + lexer.getStringValue() + "'\nStrings should be declared one per line");
			}

			lexer.nextToken();
			var.setValor(str());	// Seta o valor no objeto Variable

			if (lexer.token != Symbol.SEMICOLON){
				error.signal("Expected ';' but found '" + lexer.getStringValue() + "'\nAfter a string declaration, need to have ';' in the end");
			}

			lexer.nextToken();
			return var;
		}

		// Se não tem declaração de string
		return null;
	}

	// str -> STRINGLITERAL
	public String str(){

		if (lexer.token == Symbol.STRINGLITERAL){
			String str = lexer.getStringValue();
			lexer.nextToken();
			return str;
		}
		else {
			error.signal("Error in the string");
		}

		return null;
	}

	// true -> variavel global
	// false -> variavel local
	// str_decl_tail -> string_decl {string_decl_tail}
	public void string_decl_tail(ArrayList<Variable> var, int flag){
		Variable str = string_decl();

		// Se a chamada veio de uma declaração global insere na tabela de variaveis globais
		if (flag == GLOBAL){
			table.putGlobal(str.getVar(), str);
		}
		else {
			// Caso contrário na tabela da função local
			table.putLocal(str.getVar(), str);
		}

		var.add(str);

		if (lexer.token == Symbol.STRING){
			string_decl_tail(var, flag);
		}
	}

	/* =================================================*/
	/* 				Variable Declaration				*/
	/* =================================================*/

	// var_decl_list -> var_decl {var_decl_tail}
	public void var_decl_list(ArrayList<Variable> var, int flag){
		var_decl(var, flag);

		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_tail(var, flag);
		}
	}

	// var_decl -> var_type id_list ; | empty
	public void var_decl(ArrayList<Variable> var, int flag){
		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			Symbol tipo = var_type();
			id_list(var, tipo, flag);

			// Verificando se tem o ;
			if (lexer.token != Symbol.SEMICOLON){
				error.signal("Expected ;");
			}
			lexer.nextToken();
		}
	}

	// var_type -> FLOAT | INT
	public Symbol var_type(){
		if (lexer.token == Symbol.FLOAT){
			lexer.nextToken();
			return Symbol.FLOAT;
		}
		else if (lexer.token == Symbol.INT){
			lexer.nextToken();
			return Symbol.INT;
		}
		else{
			error.signal("Incorrect variable type\nExpected 'INT' or 'FLOAT'");
			return null;
		}
	}

	// any_type -> var_type | VOID
	public Symbol any_type(){
		// Se não for VOID, chama o var_type
		if (lexer.token != Symbol.VOID){
			return var_type();
		}

		// Então é VOID
		else {
			lexer.nextToken();
			return Symbol.VOID;
		}
	}

	// id_list -> id id_tail
	public void id_list(ArrayList<Variable> var, Symbol tipo, int flag){

		Variable nova = new Variable(id(), tipo, null);

		// Se a chamada for na declaração da variável
		if (flag != EXISTS){
			// Análise semântica, verificamos se essa variavél ja foi declarada anteriormente
			// Verificando se ela existe na tabela de variáveis globais
			if (table.returnGlobal(nova.getVar()) != null){
				error.signal("Error, variable '" + nova.getVar() + "' has already been declared as global variable");
			}

			// Verificando se ela existe na tabela de variáveis locais
			if (table.returnLocal(nova.getVar()) != null){
				error.signal("Error, variable '" + nova.getVar() + "' has already been declared as local variable");
			}

			// Se a chamada veio de uma declaração global insere na tabela de variaveis globais
			if (flag == GLOBAL){			
				table.putGlobal(nova.getVar(), nova);
			}
			else if (flag == LOCAL){
				// Caso contrário na local
				table.putLocal(nova.getVar(), nova);
			}

			var.add(nova);
		}
		// Se a chamada for na verificação, ou seja, ver se a variável ja foi declarada
		else {
			Variable exist = (Variable) table.returnLocal(nova.getVar());

			//System.out.println("Oi\n\n" + teste.getVar() + "\n" + teste.getValor() + "\n" + teste.getTipo());
			if (table.returnLocal(nova.getVar()) == null && table.returnGlobal(nova.getVar()) == null){
				error.signal("Error, variable '" + nova.getVar() + "' has not been declared");
			}
			
			var.add(exist);
		}

		id_tail(var, tipo, flag);
	}

	// id_tail -> , id id_tail | empty
	public void id_tail(ArrayList<Variable> var, Symbol tipo, int flag){
		// Se for uma virgula
		if (lexer.token == Symbol.COMMA){
			lexer.nextToken();

			Variable nova = new Variable(id(), tipo, null);

			if (flag != EXISTS){
				// Análise semântica, verificamos se essa variavél ja foi declarada anteriormente
				// Verificando se ela existe na tabela de variáveis globais
				if (table.returnGlobal(nova.getVar()) != null){
					error.signal("Error, variable '" + nova.getVar() + "' has already been declared as global variable");
				}

				// Verificando se ela existe na tabela de variáveis locais
				if (table.returnLocal(nova.getVar()) != null){
					error.signal("Error, variable '" + nova.getVar() + "' has already been declared as local variable");
				}

				// Se a chamada veio de uma declaração global insere na tabela de variaveis globais
				if (flag == GLOBAL){			
					table.putGlobal(nova.getVar(), nova);
				}
				else if (flag == LOCAL){
					// Caso contrário na local
					table.putLocal(nova.getVar(), nova);
				}

				var.add(nova);
			}
			// Se a chamada for na verificação, ou seja, ver se a variável ja foi declarada
			else {
				Variable exist = (Variable) table.returnLocal(nova.getVar());

				//System.out.println("Oi\n\n" + teste.getVar() + "\n" + teste.getValor() + "\n" + teste.getTipo());
				if (table.returnLocal(nova.getVar()) == null && table.returnGlobal(nova.getVar()) == null){
					error.signal("Error, variable '" + nova.getVar() + "' has not been declared");
				}
				
				var.add(exist);
			}
			//var.add(new Variable(id(), tipo, null));
			id_tail(var, tipo, flag);
		}
	}

	// var_decl_tail -> var_decl {var_decl_tail}
	public void var_decl_tail(ArrayList<Variable> var, int flag){
		var_decl(var, flag);

		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_tail(var, flag);
		}
	}

	/* =================================================*/
	/* 				Function Paramater List				*/
	/* =================================================*/

	// param_decl_list -> param_decl param_decl_tail
	public void param_decl_list(ArrayList<Variable> var){
		var.add(param_decl());
		param_decl_tail(var);
	}

	// param_decl -> var_type id
	public Variable param_decl(){
		Variable var = new Variable(null, var_type(), null);
		var.setVar(id());
		return var;
	}

	// param_decl_tail -> , param_decl param_decl_tail | empty
	public void param_decl_tail(ArrayList<Variable> var){
		// Verifica se é uma virgula
		if (lexer.token == Symbol.COMMA){
			lexer.nextToken();
			var.add(param_decl());
			param_decl_tail(var);
		}
	}

	/* =================================================*/
	/* 				   Function Declarations			*/
	/* =================================================*/

	// func_declarations -> func_decl {func_decl_tail}
	public void func_declarations(ArrayList<Function> func){
		func.add(func_decl());

		if (lexer.token == Symbol.FUNCTION){
			func_decl_tail(func);
		}
	}

	// func_decl -> FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
	public Function func_decl(){
		// Verifica se tem o FUNCTION
		if (lexer.token == Symbol.FUNCTION){
			lexer.nextToken();
			Function func = new Function(any_type(), id(), null);
			//id();

			// Verifica se tem o '(' depois do function
			if (lexer.token != Symbol.LPAR){
				error.signal("Expected (");
			}

			lexer.nextToken();

			ArrayList<Variable> var = new ArrayList<Variable>();
			while (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				param_decl_list(var);
			}
			func.setParametros(var);

			// Se for a função main, não pode ter parametros
			if (func.getNome().equals("main")){
				if (!var.isEmpty()){
					error.signal("Error, the main function should not have any parameters");
				}	
			}

			// Verifica se tem o ')' depois do param_decl_list
			if (lexer.token != Symbol.RPAR){
				error.signal("Expected )");
			}

			lexer.nextToken();

			// Verifica se tem a palavra BEGIN depois do ')'
			if (lexer.token != Symbol.BEGIN){
				error.signal("Expected BEGIN");
			}

			lexer.nextToken();
			func.setCorpo(func_body());

			// Verifica se tem a palavra END depois do func_body
			if (lexer.token != Symbol.END){
				error.signal("Expected END");
			}

			lexer.nextToken();
			return func;
		}

		return null;
	}

	// func_decl_tail -> func_decl {func_decl_tail}
	public void func_decl_tail(ArrayList<Function> func){
		func.add(func_decl());

		if (lexer.token == Symbol.FUNCTION){
			func_decl_tail(func);
		}
	}

	// func_body -> decl stmt_list
	public FunctionBody func_body(){
		ArrayList<Variable> var = new ArrayList<Variable>();
		ArrayList<Statement> statement = new ArrayList<Statement>();

		//flag de variavel local
		decl(var, LOCAL);
		stmt_list(statement);

		return new FunctionBody(var, statement);
	}

	/* =================================================*/
	/* 					Statement List					*/
	/* =================================================*/

	// stmt_list -> stmt stmt_tail | empty
	public void stmt_list(ArrayList<Statement> statement){
		if (lexer.token == Symbol.IDENT || lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR){
			statement.add(stmt());
			stmt_tail(statement);
		}
	}

	// stmt_tail -> stmt stmt_tail | empty
	public void stmt_tail(ArrayList<Statement> statement){
		if (lexer.token == Symbol.IDENT || lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR){
			statement.add(stmt());
			stmt_tail(statement);
		}
	}

	// stmt -> id assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt | id call_expr ;
	public Statement stmt(){
		if (lexer.token == Symbol.IDENT){
			String temp = id();

			// Procuramos a variavel para ver se está ja foi declarada
			Variable localVar = (Variable) table.returnLocal(temp);
			Variable globalVar = (Variable) table.returnGlobal(temp);
			Variable exist;

			if (localVar != null){
				exist = localVar;
			}
			else if (globalVar != null) {
				exist = globalVar;
			}
			else {
				exist = null;
			}

			// Verificamos se a variavel foi encontrada
			if (exist != null){
				ATTR = exist;
				if (lexer.token == Symbol.LPAR){
					CompositeExpr expression = new CompositeExpr(null, null, null);
					call_expr(expression);	//call_expr -> id ( {expr_list} )
	
					if (lexer.token != Symbol.SEMICOLON) {
						error.signal("Expected ;");
					}
	
					lexer.nextToken();
				}
				else if (lexer.token == Symbol.ASSIGN){
					Statement statement = new AssignStatement(new Variable(temp, null, null), null);
	
					assign_stmt(statement);		//assign_stmt -> assign_expr ;
					return statement;			//assign_expr -> id := expr
				}
				else{
					error.signal("Expected ':=' ou '(' but found '" + lexer.getStringValue() + "'");
				}
			}
			// Caso não seja encontrado, avisamos que a variavel nao foi declarada
			else{
				error.signal("Error, variable '" + temp + "' has not been declared");
			}
		}
		else if (lexer.token == Symbol.READ){
			Statement statement = new ReadStatement(null, null, new ArrayList<Variable>());
			read_stmt((ReadStatement) statement);
			return statement;
		}
		else if (lexer.token == Symbol.WRITE){
			Statement statement = new WriteStatement(null, null, new ArrayList<Variable>());
			write_stmt((WriteStatement) statement);
			return statement;
		}
		else if (lexer.token == Symbol.RETURN){
			Statement statement = new ReturnStatement(null, new CompositeExpr(null, null, null));
			return_stmt((ReturnStatement) statement);
			return statement;
		}
		else if (lexer.token == Symbol.IF){
			Statement statement = new IfStatement(null, new CompositeExpr(null, null, null));
			if_stmt((IfStatement) statement);
			return statement;
		}
		else if (lexer.token == Symbol.FOR){
			Statement statement = new ForStatement(null, new CompositeExpr(null, null, null));
			for_stmt((ForStatement) statement);
			return statement;
		}

		return null;
	}

	/* =================================================*/
	/* 					Basic Statements				*/
	/* =================================================*/

	// assign_stmt -> assign_expr ;
	public void assign_stmt(Statement statement){
		statement.setExpr(assign_expr());

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Expected ';' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();
	}

	// assign_expr -> := expr
	public CompositeExpr assign_expr(){
		if (lexer.token != Symbol.ASSIGN){
			error.signal("Expected ':=' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		CompositeExpr expression = new CompositeExpr(null, null, null);
		expr(expression);
		return expression;
	}

	// read_stmt -> READ ( id_list );
	public void read_stmt(ReadStatement statement){
		if (lexer.token != Symbol.READ){
			error.signal("Expected 'READ' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Expected '(' but found '" + lexer.getStringValue() + "'");
		}

		Symbol tipo = Symbol.INT;
		lexer.nextToken();

		// if (lexer.token != Symbol.IDENT){
		// 	error.signal("Expected variavel");
		// }

		id_list(statement.getArrayVar(), tipo, EXISTS);

		if (lexer.token != Symbol.RPAR){
			error.signal("Expected ')' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Expected ';' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();
	}

	// write_stmt -> WRITE ( id_list );
	public void write_stmt(WriteStatement statement){
		if (lexer.token != Symbol.WRITE){
			error.signal("Expected 'WRITE' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Expected '(' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		// if (lexer.token != Symbol.IDENT)
		// 	error.signal("Expected variavel");
		Symbol tipo = Symbol.INT;
		id_list(statement.getArrayVar(), tipo, EXISTS);

		if (lexer.token != Symbol.RPAR){
			error.signal("Expected ')' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Expected ';' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();
	}

	// return_stmt -> RETURN expr ;
	public void return_stmt(ReturnStatement statement){
		if (lexer.token != Symbol.RETURN){
			error.signal("Expected 'RETURN' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();
		expr(statement.getExpr());

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Expected ';' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();
	}


	/* =================================================*/
	/* 					Expressions						*/
	/* =================================================*/

	// expr -> factor expr_tail
	public void expr(CompositeExpr expression){
		if (expression.getDireita() == null){
			factor(expression);
		}
		else {
			CompositeExpr temp = (CompositeExpr) expression.getDireita();

			while (temp.getDireita() != null){
				temp = (CompositeExpr) temp.getDireita();
			}
			factor(temp);
		}

		if (expression.getDireita() == null){
			expr_tail(expression);
		}
		else{
			CompositeExpr temp = (CompositeExpr) expression.getDireita();

			while(temp.getDireita() != null){
				temp = (CompositeExpr) temp.getDireita();
			}
			expr_tail(temp);
		}
	}

	// expr_tail -> addop factor expr_tail | empty
	public void expr_tail(CompositeExpr expression){
		if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS){
			if (expression.getDireita() == null){
				addop(expression);
			}
			else {
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				addop(temp);
			}

			// addop(expression);

			if (expression.getDireita() == null){
				expression.setDireita(new CompositeExpr(null, null, null));
				factor((CompositeExpr) expression.getDireita());
			}
			else {
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				temp.setDireita(new CompositeExpr(null, null, null));
				factor((CompositeExpr) temp.getDireita());
			}
			// expression.setDireita(new CompositeExpr(null, null, null));
			// factor((CompositeExpr) expression.getDireita());

			if (expression.getDireita() == null){
				expr_tail((CompositeExpr) expression.getDireita());
			}
			else{
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				expr_tail(temp);
			}
			// expr_tail((CompositeExpr) expression.getDireita());
		}
	}

	// factor -> postfix_expr factor_tail
	public void factor(CompositeExpr expression){
		postfix_expr(expression);
		factor_tail(expression);
	}

	// factor_tail -> mulop postfix_expr factor_tail | empty
	public void factor_tail(CompositeExpr expression){
		if (lexer.token == Symbol.MULT || lexer.token == Symbol.DIV){
			if (expression.getDireita() == null){
				mulop(expression);
			}
			else {
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				mulop(temp);
			}
			// mulop(expression);

			if (expression.getDireita() == null){
				expression.setDireita(new CompositeExpr(null, null, null));
				postfix_expr((CompositeExpr) expression.getDireita());
			}
			else {
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				temp.setDireita(new CompositeExpr(null, null, null));
				postfix_expr((CompositeExpr) temp.getDireita());
			}
			// expression.setDireita(new CompositeExpr(null, null, null));
			// postfix_expr((CompositeExpr) expression.getDireita());

			if (expression.getDireita() == null){
				factor_tail((CompositeExpr) expression.getDireita());
			}
			else{
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				factor_tail(temp);
			}
			// factor_tail((CompositeExpr) expression.getDireita());
		}
	}

	// postfix_expr -> primary | id call_expr
	public void postfix_expr(CompositeExpr expression){
		if (lexer.token == Symbol.LPAR || lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL){
			primary(expression);
		}
		else if (lexer.token == Symbol.IDENT){
			String ident = id();

			// Procuramos a variavel para ver se está ja foi declarada
			Variable localVar = (Variable) table.returnLocal(ident);
			Variable globalVar = (Variable) table.returnGlobal(ident);
			Variable exist;

			if (localVar != null){
				exist = localVar;
			}
			else if (globalVar != null) {
				exist = globalVar;
			}
			else {
				exist = null;
			}

			// Se a variavel não tiver sido declarada, lança um erro
			if (exist == null){
				error.signal("Error, variable '" + ident + "' has not been declared");
			}

			System.out.println("ATTR " + ATTR.getTipo());
			System.out.println("exist " + exist.getTipo());
			// Se o tipo de atribuição for incorreto, lança um erro
			if (ATTR.getTipo() != exist.getTipo()){
				error.signal("Error, variable '" + ATTR.getVar() + "' has type '" + ATTR.getTipo() + "'\nAnd variable '" + ATTR.getVar() + "' has type '" + ATTR.getTipo() + "' incompatible types");
			}

			// Se tudo estiver correto, continua normalmente
			Variable var = new Variable(ident, null, null);

			if (expression.getEsquerda() == null){
				expression.setEsquerda(new VariableExpr(var));
			}
			else if (expression.getDireita() == null){
				expression.setDireita(new CompositeExpr(new VariableExpr(var), null, null));
			}
			else {
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				if (temp.getEsquerda() == null){
					temp.setEsquerda(new VariableExpr(var));
				}
				else {
					temp.setDireita(new CompositeExpr(new VariableExpr(var), null, null));
				}
			}

			if (lexer.token == Symbol.LPAR){
				call_expr(expression);
			}			
		}
	}

	// call_expr -> ( {expr_list} ) (LER ID ANTES DE CHAMAR A FUNÇÃO)
	public void call_expr(CompositeExpr expression){
		if (lexer.token != Symbol.LPAR){
			error.signal("Expected '(' but found '" + lexer.getStringValue() + "'");
		}

		if (expression.getEsquerda() == null){
			expression.setEsquerda(new ParenthesesExpr("("));
		}
		else if (expression.getDireita() == null){
			expression.setDireita(new CompositeExpr(null, null, null));
			CompositeExpr temp = (CompositeExpr) expression.getDireita();
			temp.setEsquerda(new ParenthesesExpr("("));
		}
		else {
			CompositeExpr temp = (CompositeExpr) expression.getDireita();

			while (temp.getDireita() != null){
				temp = (CompositeExpr) temp.getDireita();
			}

			ParenthesesExpr par = new ParenthesesExpr("(");

			if (temp.getEsquerda() == null){
				temp.setEsquerda(par);
				temp.setDireita(new CompositeExpr(null, null, null));
			}
			else {
				temp.setDireita(new CompositeExpr(par, null, null));
			}
		}

		lexer.nextToken();

		while (lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL || lexer.token == Symbol.IDENT){
			expr_list(expression);
		}

		if (lexer.token != Symbol.RPAR){
			error.signal("Expected ')' but found '" + lexer.getStringValue() + "'");
		}

		if (expression.getDireita() == null){
			ParenthesesExpr par = new ParenthesesExpr(")");
			expression.setDireita(new CompositeExpr(par, null, null));
		}
		else {
			CompositeExpr temp = (CompositeExpr) expression.getDireita();

			while (temp.getDireita() != null){
				temp = (CompositeExpr) temp.getDireita();
			}

			ParenthesesExpr par = new ParenthesesExpr(")");

			if (temp.getEsquerda() == null){
				temp.setEsquerda(par);
			}
			else {
				temp.setDireita(new CompositeExpr(par, null, null));
			}
		}

		lexer.nextToken();
	}

	// expr_list -> expr expr_list_tail
	public void expr_list(CompositeExpr expression){
		expr(expression);
		expr_list_tail(expression);
	}

	// expr_list_tail -> , expr expr_list_tail | empty
	public void expr_list_tail(CompositeExpr expression){
		// Se for uma virgula, chama expr e expr_list_tail
		if (lexer.token == Symbol.COMMA){
			lexer.nextToken();

			if (expression.getDireita() == null){
				expression.setOperador(Symbol.COMMA);
			}
			else {
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				temp.setOperador(Symbol.COMMA);
			}

			expr(expression);
			expr_list_tail(expression);
		}
	}

	// primary -> (expr) | id | INTLITERAL | FLOATLITERAL
	public void primary(CompositeExpr expression){
		// Se for um '(', anda e chama expr e verifica se tem ')'
		if (lexer.token == Symbol.LPAR){
			lexer.nextToken();

			if (expression.getEsquerda() == null){
				expression.setEsquerda(new ParenthesesExpr("("));
				expr(expression);
			}
			else if (expression.getDireita() == null){
				expression.setDireita(new CompositeExpr(null, null, null));
				CompositeExpr temp = (CompositeExpr) expression.getDireita();
				temp.setEsquerda(new ParenthesesExpr("("));

				expr(temp);
			}
			else{
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				ParenthesesExpr par = new ParenthesesExpr("(");

				if (temp.getEsquerda() == null){
					temp.setEsquerda(par);

					temp.setDireita(new CompositeExpr(null, null, null));
					expr((CompositeExpr) temp.getDireita());
				}
				else {
					temp.setDireita(new CompositeExpr(par, null, null));
					expr((CompositeExpr) temp.getDireita());
				}
			}

			// expr(expression);

			if (lexer.token != Symbol.RPAR){
				error.signal("Expected ')' but found '" + lexer.getStringValue() + "'");
			}

			if (expression.getDireita() == null){
				ParenthesesExpr par = new ParenthesesExpr(")");
				expression.setDireita(new CompositeExpr(par, null, null));
			}
			else{
				CompositeExpr temp = (CompositeExpr) expression.getDireita();

				while (temp.getDireita() != null){
					temp = (CompositeExpr) temp.getDireita();
				}

				ParenthesesExpr par = new ParenthesesExpr(")");

				if (temp.getEsquerda() == null){
					temp.setEsquerda(par);
				}
				else {
					temp.setDireita(new CompositeExpr(par, null, null));
				}
			}

			lexer.nextToken();
		}
		else if(lexer.token == Symbol.IDENT){
			if (expression.getEsquerda() == null){
				expression.setEsquerda(new VariableExpr(new Variable(id(), null, null)));
			}
			else {
				String ident = id();
				CompositeExpr temp = new CompositeExpr(null, null, null);
				VariableExpr var = new VariableExpr(new Variable(ident, null, null));
				temp.setEsquerda(var);
				expression.setDireita(temp);
			}
		}
		else if(lexer.token == Symbol.INTLITERAL){

			if (ATTR.getTipo() != Symbol.INTLITERAL){
				error.signal("Error, variable '" + ATTR.getVar() + "' has type '" + ATTR.getTipo() + "' but found 'INT'");
			}

			if (expression.getEsquerda() == null){
				expression.setEsquerda(new IntNumberExpr(lexer.getIntValue()));
			}
			else {
				CompositeExpr temp = new CompositeExpr(new IntNumberExpr(lexer.getIntValue()), null, null);
				expression.setDireita(temp);
			}
			
			lexer.nextToken();
		}
		else{
			if (expression.getEsquerda() == null){
				expression.setEsquerda(new FloatNumberExpr(lexer.getFloatValue()));
			}
			else {
				CompositeExpr temp = new CompositeExpr(new FloatNumberExpr(lexer.getFloatValue()), null, null);
				expression.setDireita(temp);
			}
			
			lexer.nextToken();
		}
	}

	// addop -> + | -
	public void addop(CompositeExpr e){
		if (lexer.token == Symbol.PLUS){
			lexer.nextToken();
			e.setOperador(Symbol.PLUS);
		}
		else if (lexer.token == Symbol.MINUS){
			lexer.nextToken();
			e.setOperador(Symbol.MINUS);
		}
		// else{
		// 	lexer.nextToken();
		// }

	}

	// mulop -> * | /
	public void mulop(CompositeExpr e){
		if (lexer.token == Symbol.MULT){
			lexer.nextToken();
			e.setOperador(Symbol.MULT);
		}
		else if (lexer.token == Symbol.DIV){
			lexer.nextToken();
			e.setOperador(Symbol.DIV);
		}
		// else{
		// 	lexer.nextToken();
		// }
	}


	/* =================================================*/
	/* 			Complex Statements and Condition		*/
	/* =================================================*/

	// if_stmt -> IF ( cond ) THEN stmt_list else_part ENDIF
	public void if_stmt(IfStatement statement){
		if (lexer.token != Symbol.IF){
			error.signal("Expected 'IF' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Expected '(' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();
		cond(statement.getExpr());

		if (lexer.token != Symbol.RPAR){
			error.signal("Expected ')' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.THEN){
			error.signal("Expected 'THEN' but found '" + lexer.getStringValue() + "'\nAfter the condition in an IF, need to write the word 'THEN'");
		}

		lexer.nextToken();
		statement.setCorpo(new IfBody(new ArrayList<Statement>()));
		stmt_list(statement.getCorpo().getArrayStmt());
		else_part(statement);

		if (lexer.token != Symbol.ENDIF){
			error.signal("Expected 'ENDIF' but found '" + lexer.getStringValue() + "'\nAn IF or ELSE block need to end with the word 'ENDIF'");
		}
		lexer.nextToken();
	}

	// else_part -> ELSE stmt_list | empty
	public void else_part(IfStatement statement){
		// Se tiver else, anda e chama stmt_list
		if (lexer.token == Symbol.ELSE){
			lexer.nextToken();
			// Cria um objeto ElseStatement e seta no IfStatement Atual
			statement.setCorpoElse(new ElseStatement(null, new CompositeExpr(null, null, null)));

			// Cria um novo IfBody e seta no ElseStatement
			statement.getCorpoElse().setCorpo(new IfBody(new ArrayList<Statement>()));

			// Passa o arrayStmt do novo IfBody para o stmt_list
			stmt_list(statement.getCorpoElse().getCorpo().getArrayStmt());
		}
	}

	// cond -> expr compop expr
	public void cond(CompositeExpr expression){
		expr(expression);
		compop(expression);
		expression.setDireita(new CompositeExpr(null, null, null));
		expr((CompositeExpr) expression.getDireita());
	}

	// compop -> < | > | =
	public void compop(CompositeExpr expression){
		// Verifica se é um dos simbolos, se for anda o token
		if (lexer.token == Symbol.LT){
			lexer.nextToken();
			expression.setOperador(Symbol.LT);
		}
		else if (lexer.token == Symbol.GT){
			lexer.nextToken();
			expression.setOperador(Symbol.GT);
		}
		else if (lexer.token == Symbol.EQUAL){
			lexer.nextToken();
			expression.setOperador(Symbol.EQUAL);
		}
		else{
			error.signal("Expected '<' or '>' or '=' but found '" + lexer.getStringValue() + "'");
		}
	}

	// for_stmt -> FOR ({assign_expr}; {cond}; {id assign_expr}) stmt_list ENDFOR
	public void for_stmt(ForStatement statement){
		if (lexer.token != Symbol.FOR){
			error.signal("Expected 'FOR' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Expected '(' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		// Enquanto for declaração de variáveis no for, verifica as variáveis
		while (lexer.token == Symbol.IDENT){
			Variable var = new Variable(id(), Symbol.INT, null);
			CompositeExpr ce = assign_expr();

			// Seta o novo Array
			statement.setAtribuicaoInicio(new ArrayList<AssignStatement>());
			// Adiciona no Array do inicio do statement
			// Uma nova variavel e uma nova atribuição
			statement.getAtribuicaoInicio().add(new AssignStatement(var, ce));
		}

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Expected ';' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		// Enquanto for um IDENT ou '(' ou ')' ou float ou int, chama o cond()
		while (lexer.token == Symbol.LPAR || lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL || lexer.token == Symbol.IDENT){
			cond(statement.getExpr());
		}

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Expected ';' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		// Enquanto for um IDENT, chama o id() e assign_expr()
		while(lexer.token == Symbol.IDENT){
			Variable var = new Variable(id(), Symbol.INT, null);
			CompositeExpr ce = assign_expr();
			// id();
			// assign_expr();
			// Seta o novo Array
			statement.setAtribuicaoFinal(new ArrayList<AssignStatement>());
			// Adiciona no Array do inicio do statement
			// Uma nova variavel e uma nova atribuição
			statement.getAtribuicaoFinal().add(new AssignStatement(var, ce));
			// statement.getAtribuicaoFinal().add(new AssignStatement(new Variable(id(), Symbol.INT, null), assign_expr()));
		}

		if (lexer.token != Symbol.RPAR){
			error.signal("Expected ')' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();

		// Criando um novo objeto ForBody e setando no statement
		statement.setCorpo(new ForBody(new ArrayList<Statement>()));

		// Pegando do ForBody o arrayStmt
		stmt_list(statement.getCorpo().getArrayStmt());

		if (lexer.token != Symbol.ENDFOR){
			error.signal("Expected 'ENDFOR' but found '" + lexer.getStringValue() + "'");
		}

		lexer.nextToken();
	}
}
