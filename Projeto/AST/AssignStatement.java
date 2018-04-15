package AST;

public class AssignStatement extends Statement{
    public AssignStatement(Variable var, CompositeExpr expr){
        super(var, expr);
    }

    @Override
    public void genC(PW pw){
        pw.printBL();   // Quebra de linha

        pw.printI();    // Ident
        pw.printNI(super.getVar().getVar() + " = "); //Variavel
        super.getExpr().genC(pw);   // Expressão        
        pw.printNI(";");    // ';'

        pw.printBL();   //Quebra de linha
    }

    public void genCFOR(PW pw){
        pw.printNI(super.getVar().getVar() + " = ");
        super.getExpr().genC(pw);
    }
}