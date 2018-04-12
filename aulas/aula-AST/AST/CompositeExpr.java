package AST;

import Lexer.*;

public class CompositeExpr extends Expr {

    public CompositeExpr( Expr pleft, Symbol poper, Expr pright ) {
        left = pleft;
        oper = poper;
        right = pright;
    }
    public void genC() {
        System.out.print("CompositeExpr " + oper.toString() + " ");
        left.genC();
        right.genC();
        System.out.println("Fim CompositeExpr");
    }
   
    private Expr left, right;
    private Symbol oper;
}

