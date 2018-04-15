package AST;

public class VariableExpr extends Expr {
    private Variable v;

    public VariableExpr( Variable v ) {
        this.v = v;
    }
    
    public void genC(PW pw) {
        pw.printNI(v.getVar());
    }
}
