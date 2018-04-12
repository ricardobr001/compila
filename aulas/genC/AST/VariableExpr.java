package AST;

public class VariableExpr extends Expr {

    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC(PW pw) {
        pw.print( v.getName() );
    }

    private Variable v;
}
