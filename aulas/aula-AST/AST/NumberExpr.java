package AST;

public class NumberExpr extends Expr {

    public NumberExpr( int value ) { 
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }

    public void genC() {
        System.out.println("Number " + value);
    }

    private int value;
}
