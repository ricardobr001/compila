package AST;

import Lexer.*;

public class CompositeExpr extends Expr {

    public CompositeExpr( Expr pleft, Symbol poper, Expr pright ) {
        left = pleft;
        oper = poper;
        right = pright;
    }
    public void genC(PW pw) {
        pw.print("(");
        left.genC(pw);
        pw.print(" " + oper.toString() + " ");
        right.genC(pw);
        pw.print(")");
    }
   
    private Expr left, right;
    private Symbol oper;
}

