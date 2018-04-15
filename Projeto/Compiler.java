import Lexer.*;
import Error.*;
import AST.*;
import java.util.*;

public class Compiler {
	// para geracao de codigo
	public static final boolean GC = false;
	private Lexer lexer;
    private CompilerError error;

    public PgmBody compile( char []p_input ) {
		error = new CompilerError(null);
		lexer = new Lexer(p_input, error);
		error.setLexer(lexer);
        lexer.nextToken();
        PgmBody pg = program();

        if (lexer.token != Symbol.EOF)
			error.signal("nao chegou no fim do arq");

		return pg;
    }

	/* =================================================*/
	/* 						Program						*/
	/* =================================================*/

    // Program -> 'PROGRAM' id 'BEGIN' pgm_body 'END'
    public PgmBody program(){
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
		PgmBody pg = pgm_body();

		// Verifica se tem a palavra END depois do pgm_body
		if (lexer.token != Symbol.END){
			error.signal("faltou END");
		}

		lexer.nextToken();

		return pg;
	}

	// id -> IDENT
	public String id(){
		// Verifica se é um IDENTIFICADOR
		if (lexer.token != Symbol.IDENT){
			error.signal("Identificador não encontrado");
		}

		String var = lexer.getStringValue();
		lexer.nextToken();

		return var;
	}

	// pgm_body -> decl func_declarations
	public PgmBody pgm_body(){
		ArrayList<Variable> var = new ArrayList<Variable>();
		ArrayList<Function> func = new ArrayList<Function>();
		decl(var);
		func_declarations(func);
		return new PgmBody(var, func);
	}

	// decl -> string_decl_list {decl} | var_decl_list {decl} | empty
	public void decl(ArrayList<Variable> var){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			string_decl_list(var);

			if (lexer.token == Symbol.STRING || lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				decl(var);
			}
		}
		// Verifica se é uma variável
		else if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_list(var);

			if (lexer.token == Symbol.STRING || lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				decl(var);
			}
		}
	}

	/* =================================================*/
	/* 				Global String Declaration			*/
	/* =================================================*/

	// string_decl_list -> string_decl {string_decl_tail}
	public void string_decl_list(ArrayList<Variable> var){
		// Verifica se é uma string
		if (lexer.token == Symbol.STRING){
			var.add(string_decl());

			if (lexer.token == Symbol.STRING){
				string_decl_tail(var);
			}
		}
	}

	// string_decl -> STRING id := str ; | empty
	public Variable string_decl(){
		// Verifica se é a palavra STRING
		if (lexer.token == Symbol.STRING){
			lexer.nextToken();

			Variable var = new Variable(id(), Symbol.STRING, null);

			if (lexer.token != Symbol.ASSIGN){
				error.signal("faltou :=");
			}

			lexer.nextToken();
			var.setValor(str());	// Seta o valor no objeto Variable

			if (lexer.token != Symbol.SEMICOLON){
				error.signal("faltou ;");
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
			error.signal("erro na string");
		}

		return null;
	}

	// str_decl_tail -> string_decl {string_decl_tail}
	public void string_decl_tail(ArrayList<Variable> var){
		var.add(string_decl());

		if (lexer.token == Symbol.STRING){
			string_decl_tail(var);
		}
	}

	/* =================================================*/
	/* 				Variable Declaration				*/
	/* =================================================*/

	// var_decl_list -> var_decl {var_decl_tail}
	public void var_decl_list(ArrayList<Variable> var){
		var_decl(var);

		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_tail(var);
		}
	}

	// var_decl -> var_type id_list ; | empty
	public void var_decl(ArrayList<Variable> var){
		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			Symbol tipo = var_type();
			id_list(var, tipo);

			// Verificando se tem o ;
			if (lexer.token != Symbol.SEMICOLON){
				error.signal("faltou ;");
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
			error.signal("tipo de variavel incorreto");
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
	public void id_list(ArrayList<Variable> var, Symbol tipo){
		var.add(new Variable(id(), tipo, null));
		id_tail(var, tipo);
	}

	// id_tail -> , id id_tail | empty
	public void id_tail(ArrayList<Variable> var, Symbol tipo){
		// Se for uma virgula
		if (lexer.token == Symbol.COMMA){
			lexer.nextToken();
			var.add(new Variable(id(), tipo, null));
			id_tail(var, tipo);
		}
	}

	// var_decl_tail -> var_decl {var_decl_tail}
	public void var_decl_tail(ArrayList<Variable> var){
		var_decl(var);

		if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
			var_decl_tail(var);
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
				error.signal("faltou (");
			}

			lexer.nextToken();

			ArrayList<Variable> var = new ArrayList<Variable>();
			while (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT){
				param_decl_list(var);
			}
			func.setParametros(var);

			// Verifica se tem o ')' depois do param_decl_list
			if (lexer.token != Symbol.RPAR){
				error.signal("faltou )");
			}

			lexer.nextToken();

			// Verifica se tem a palavra BEGIN depois do ')'
			if (lexer.token != Symbol.BEGIN){
				error.signal("faltou BEGIN");
			}

			lexer.nextToken();
			func.setCorpo(func_body());

			// Verifica se tem a palavra END depois do func_body
			if (lexer.token != Symbol.END){
				error.signal("faltou END");
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
		decl(var);
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

			if (lexer.token == Symbol.LPAR){
				CompositeExpr expression = new CompositeExpr(null, null, null);
				call_expr(expression);	//call_expr -> id ( {expr_list} )

				if (lexer.token != Symbol.SEMICOLON) {
					error.signal("Faltou ;");
				}

				lexer.nextToken();
			}
			else if (lexer.token == Symbol.ASSIGN){
				Statement statement = new AssignStatement(new Variable(temp, null, null), null);

				assign_stmt(statement);		//assign_stmt -> assign_expr ;
				return statement;			//assign_expr -> id := expr
			}
			else{
				error.signal("Faltou := ou (");
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
			error.signal("Faltou ;");
		}

		lexer.nextToken();
	}

	// assign_expr -> := expr
	public CompositeExpr assign_expr(){
		if (lexer.token != Symbol.ASSIGN){
			error.signal("Faltou :=");
		}

		lexer.nextToken();

		CompositeExpr expression = new CompositeExpr(null, null, null);
		expr(expression);
		return expression;
	}

	// read_stmt -> READ ( id_list );
	public void read_stmt(ReadStatement statement){
		if (lexer.token != Symbol.READ){
			error.signal("Faltou READ");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Faltou (");
		}

		Symbol tipo = Symbol.INT;
		lexer.nextToken();

		// if (lexer.token != Symbol.IDENT){
		// 	error.signal("Faltou variavel");
		// }

		id_list(statement.getArrayVar(), tipo);

		if (lexer.token != Symbol.RPAR){
			error.signal("Faltou )");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Faltou ;");
		}

		lexer.nextToken();
	}

	// write_stmt -> WRITE ( id_list );
	public void write_stmt(WriteStatement statement){
		if (lexer.token != Symbol.WRITE){
			error.signal("Faltou WRITE");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Faltou (");
		}

		lexer.nextToken();

		// if (lexer.token != Symbol.IDENT)
		// 	error.signal("Faltou variavel");
		Symbol tipo = Symbol.INT;
		id_list(statement.getArrayVar(), tipo);

		if (lexer.token != Symbol.RPAR){
			error.signal("Faltou )");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Faltou ;");
		}

		lexer.nextToken();
	}

	// return_stmt -> RETURN expr ;
	public void return_stmt(ReturnStatement statement){
		if (lexer.token != Symbol.RETURN){
			error.signal("Faltou RETURN");
		}

		lexer.nextToken();
		expr(statement.getExpr());

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Faltou ;");
		}

		lexer.nextToken();
	}


	/* =================================================*/
	/* 					Expressions						*/
	/* =================================================*/

	// expr -> factor expr_tail
	public void expr(CompositeExpr expression){
		factor(expression);

		// if (expression.getDireita() == null){		
			expr_tail(expression);	
		// }
		// else{
		// 	// CompositeExpr temp = (CompositeExpr) expression.getDireita();

		// 	// while(temp.getDireita() != null){
		// 	// 	temp = (CompositeExpr) temp.getDireita();
		// 	// }
		// 	expr_tail((CompositeExpr) expression.getDireita());
		// }
	}

	// expr_tail -> addop factor expr_tail | empty
	public void expr_tail(CompositeExpr expression){
		if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS){
			addop(expression);
			expression.setDireita(new CompositeExpr(null, null, null));
			factor((CompositeExpr) expression.getDireita());
			expr_tail((CompositeExpr) expression.getDireita());
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
			mulop(expression);
			expression.setDireita(new CompositeExpr(null, null, null));
			postfix_expr((CompositeExpr) expression.getDireita());
			factor_tail((CompositeExpr) expression.getDireita());
		}
	}

	// postfix_expr -> primary | id call_expr

	// primary -> (expr) | id | INTLITERAL | FLOATLITERAL
	public void postfix_expr(CompositeExpr expression){
		if (lexer.token == Symbol.LPAR || lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL){
			primary(expression);
		}
		else if (lexer.token == Symbol.IDENT){
			Variable var = new Variable(id(), null, null);
			expression.setEsquerda(new VariableExpr(var));

			if (lexer.token == Symbol.LPAR){
				call_expr(expression);
			}
		}
	}

	// call_expr -> ( {expr_list} ) (LER ID ANTES DE CHAMAR A FUNÇÃO)
	public void call_expr(CompositeExpr expression){
		if (lexer.token != Symbol.LPAR){
			error.signal("Faltou (");
		}

		lexer.nextToken();

		while (lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL || lexer.token == Symbol.IDENT){
			expr_list(expression);
		}

		if (lexer.token != Symbol.RPAR){
			error.signal("Faltou )");
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
			expr(expression);
			expr_list_tail(expression);
		}
	}

	// primary -> (expr) | id | INTLITERAL | FLOATLITERAL
	public void primary(CompositeExpr expression){
		// Se for um '(', anda e chama expr e verifica se tem ')'
		if (lexer.token == Symbol.LPAR){
			lexer.nextToken();
			expr(expression);

			if (lexer.token != Symbol.RPAR){
				error.signal("Faltou )");
			}

			lexer.nextToken();
		}
		else if(lexer.token == Symbol.IDENT){
			expression.setEsquerda(new VariableExpr(new Variable(id(), null, null)));
		}
		else if(lexer.token == Symbol.INTLITERAL){
			expression.setEsquerda(new IntNumberExpr(lexer.getIntValue()));
			lexer.nextToken();
		}
		else{
			expression.setEsquerda(new FloatNumberExpr(lexer.getFloatValue()));
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
			error.signal("Faltou IF");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Faltou (");
		}

		lexer.nextToken();
		cond(statement.getExpr());

		if (lexer.token != Symbol.RPAR){
			error.signal("Faltou )");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.THEN){
			error.signal("Faltou THEN");
		}

		lexer.nextToken();
		statement.setCorpo(new IfBody(new ArrayList<Statement>()));
		stmt_list(statement.getCorpo().getArrayStmt());
		else_part(statement);

		if (lexer.token != Symbol.ENDIF){
			error.signal("Faltou ENDIF");
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
			error.signal("Faltou < ou > ou =");
		}
	}

	// for_stmt -> FOR ({assign_expr}; {cond}; {id assign_expr}) stmt_list ENDFOR
	public void for_stmt(ForStatement statement){
		if (lexer.token != Symbol.FOR){
			error.signal("Faltou FOR");
		}

		lexer.nextToken();

		if (lexer.token != Symbol.LPAR){
			error.signal("Faltou (");
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
			error.signal("Faltou ;");
		}

		lexer.nextToken();

		// Enquanto for um IDENT ou '(' ou ')' ou float ou int, chama o cond()
		while (lexer.token == Symbol.LPAR || lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL || lexer.token == Symbol.IDENT){
			cond(statement.getExpr());
		}

		if (lexer.token != Symbol.SEMICOLON){
			error.signal("Faltou ;");
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
			error.signal("Faltou )");
		}

		lexer.nextToken();
		
		// Criando um novo objeto ForBody e setando no statement
		statement.setCorpo(new ForBody(new ArrayList<Statement>()));

		// Pegando do ForBody o arrayStmt
		stmt_list(statement.getCorpo().getArrayStmt());

		if (lexer.token != Symbol.ENDFOR){
			error.signal("Faltou ENDFOR");
		}

		lexer.nextToken();
	}
}
