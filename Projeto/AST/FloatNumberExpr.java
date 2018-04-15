package AST;

public class FloatNumberExpr extends Expr{
    private float numero;
    
    public FloatNumberExpr(float numero){
        this.numero = numero;
    }

    public void genC(PW pw){
        pw.printNI(Float.toString(numero));
    }
}