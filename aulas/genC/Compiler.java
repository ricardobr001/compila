/*
    comp6
Mesma gramática usada no compilador 5, mas agora com a implementacao da classes da AST (Abstract Syntax Tree).
O genC eh usado para gerar codigo em C.
 
    Grammar:
       Program ::= 'begin' VarDecList ';' AssignmentStatement 'end'
       VarDecList ::= Variable | Variable ',' VarDecList
       Variable ::= Letter {Letter}
       Letter ::= 'A' | 'B' | ... | 'Z' | 'a' | ... |'z'
       AssignmentStatement ::= Variable '=' Expr ';'      
       Expr ::= Oper  Expr Expr  | Number | Variable
       Oper ::= '+' | '-' | '*' | '/'
       Number ::= Digit {Digit} 
       Digit ::= '0'| '1' | ... | '9'
*/

import Lexer.*;
import Error.*;
import AST.*;
import java.util.*;

public class Compiler {


    public Program compile( char []p_input ) {
        lexer = new Lexer(p_input, error);
        error = new CompilerError(lexer);
        lexer.nextToken();
        Program p = program();
        if (error.wasAnErrorSignalled())
            return null;
        return p;
    }
    
    // Program ::= VarDecList ';' AssignmentStatement
    public Program program(){
        if(lexer.token != Symbol.BEGIN)
            error.signal("'begin' expected");
        lexer.nextToken();
    	ArrayList<Variable> vl = varDecList();
    	if (lexer.token != Symbol.SEMICOLON)
    		error.signal("; expected");
    	lexer.nextToken();
    	AssignmentStatement as = assignmentStatement();
        if(lexer.token != Symbol.END)
            error.signal("'end' expected");
        lexer.nextToken();
        return new Program(vl, as);
    }
       
	// VarDecList ::= Variable | Variable ',' VarDecList
	public ArrayList<Variable> varDecList(){
        ArrayList<Variable> vl = new ArrayList<Variable>();
        if (lexer.token != Symbol.IDENT)
            error.signal("variable name expected");
        Variable v = new Variable(lexer.getStringValue());
        vl.add(v);
        lexer.nextToken();
        
		while (lexer.token == Symbol.COMMA){
            lexer.nextToken();
            if (lexer.token != Symbol.IDENT)
                error.signal("variable name expected");
            v = new Variable(lexer.getStringValue());
            vl.add(v);
            lexer.nextToken();
        }
        return vl;
	}

    //AssignmentStatement ::= Variable '=' Expr ';'
    public AssignmentStatement assignmentStatement(){
        if (lexer.token != Symbol.IDENT)
            error.signal("variable name expected");
        Variable v = new Variable(lexer.getStringValue());
        lexer.nextToken();

        if (lexer.token != Symbol.ASSIGN)
            error.signal ("= expected");
        lexer.nextToken();
        
        
        Expr e = expr();
        if (lexer.token != Symbol.SEMICOLON)
            error.signal("; expected");
        lexer.nextToken();
        
        return new AssignmentStatement(v, e);
    }
    
    // Expr ::= Oper  Expr Expr  | Number
    public Expr expr(){
        Expr e = null;
        if (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS || lexer.token == Symbol.MULT || lexer.token == Symbol.DIV){
            Symbol op = lexer.token;

            lexer.nextToken();

            Expr l = expr();
            Expr r = expr();
            
            e = new CompositeExpr(l, op, r);
        }
        else if (lexer.token == Symbol.NUMBER) {
            e = new NumberExpr(lexer.getNumberValue());
            lexer.nextToken();
        }
        else if (lexer.token == Symbol.IDENT) {
            e = new VariableExpr(new Variable(lexer.getStringValue()));
            lexer.nextToken();
        }
        else
            error.signal("operator or int number expected");
        return e;
    }
    
	private Lexer lexer;
    private CompilerError error;

}
    
    
    
