package AST;

public class Statement{
    private Variable var;
    private CompositeExpr expr;
    //private ArrayList<Variable> expressao;

    public Statement(Variable v, CompositeExpr e){
        this.var = v;
        this.expr = e;
    }

    public void genC(PW pw){
        //faz nada
    }

    public Variable getVar(){
        return this.var;
    }

    public CompositeExpr getExpr(){
        return this.expr;
    }

    public void setVar(Variable var){
        this.var = var;
    }

    public void setExpr(CompositeExpr expr){
        this.expr = expr;
    }
}