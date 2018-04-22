package AST;

public class IfStatement extends Statement{
    private IfBody corpo;
    private ElseStatement corpoElse;

    public IfStatement(Variable var, CompositeExpr expr){
        super(var, expr);
    }

    @Override
    public void genC(PW pw){
        pw.printBL();   // Quebra de linha

        // Cabeçalho do IF
        pw.printI();    // Ident
        pw.printNI("if ("); //Variavel
        super.getExpr().genC(pw);   // Expressão
        pw.printNI(")");  
        pw.printBL();

        // corpo do IF
        pw.print("{");
        pw.add();
        corpo.genC(pw);
        pw.sub();
        pw.println("}");

        // Else
        corpoElse.genC(pw);
    }

    public void setCorpo(IfBody corpo){
        this.corpo = corpo;
    }

    public void setCorpoElse(ElseStatement corpo){
        this.corpoElse = corpo;
    }

    public IfBody getCorpo(){
        return this.corpo;
    }

    public ElseStatement getCorpoElse(){
        return this.corpoElse;
    }
}