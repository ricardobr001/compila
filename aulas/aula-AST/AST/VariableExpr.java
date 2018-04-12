package AST;

public class VariableExpr extends Expr {

    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC() {
        System.out.println("VariableExpr " + v.getName() );
    }

    private Variable v;
}
