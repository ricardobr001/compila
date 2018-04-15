package AST;

public class ElseStatement extends Statement{
    private IfBody corpo;

    public ElseStatement(Variable var, CompositeExpr expr){
        super(var, expr);
    }

    @Override
    public void genC(PW pw){
        // pw.printBL();   // Quebra de linha

        // Cabeçalho do IF
        // pw.printI();    // Ident
        pw.println("else"); //else  

        // corpo do IF
        pw.print("{");
        pw.add();
        corpo.genC(pw);
        pw.sub();
        pw.println("}");
    }

    public void setCorpo(IfBody corpo){
        this.corpo = corpo;
    }

    public IfBody getCorpo(){
        return this.corpo;
    }
}