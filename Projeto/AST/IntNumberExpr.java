package AST;

public class IntNumberExpr extends Expr{
    private int numero;
    
    public IntNumberExpr(int numero){
        this.numero = numero;
    }

    public void genC(PW pw){
        pw.printNI(Integer.toString(numero));
    }
}