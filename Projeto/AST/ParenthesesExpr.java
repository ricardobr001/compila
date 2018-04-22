package AST;

public class ParenthesesExpr extends Expr{
    private String par;

    public ParenthesesExpr(String s){
        this.par = s;
    }

    public void genC(PW pw){
        pw.printNI(par);
    }
}