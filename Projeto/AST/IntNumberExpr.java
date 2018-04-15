package AST;

public class IntNumberExpr extends Expr{
    private int numero;
    
    public NumberExpr(int numero){
        this.numero = numero;
    }

    public void genC(PW pw){
        pw.print(Integer.toString(numero));
    }
}