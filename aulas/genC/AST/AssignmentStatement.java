package AST;

public class AssignmentStatement {

    public AssignmentStatement( Variable v, Expr expr ) {
        this.v = v;
        this.expr = expr;
    }
 
    public void genC(PW pw) {
        pw.print( v.getName() + " = " );
        expr.genC(pw);
        pw.println(";");
    }

    private Variable v;
    private Expr expr;
}
