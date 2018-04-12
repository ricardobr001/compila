package AST;

public class AssignmentStatement {

    public AssignmentStatement( Variable v, Expr expr ) {
        this.v = v;
        this.expr = expr;
    }
 
    public void genC() {
        System.out.print("Assign " + v.getName() + " ");
        expr.genC();
		System.out.println("FimAssign");
    }

    private Variable v;
    private Expr expr;
}
