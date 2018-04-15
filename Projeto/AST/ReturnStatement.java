package AST;

public class ReturnStatement extends Statement{
    public ReturnStatement(Variable v, CompositeExpr e){
        super(v, e);
    }

    @Override
    public void genC(PW pw){
        pw.printBL();   // Quebra de linha

        pw.printI();    // Ident
        pw.printNI("return ");  // Return

        super.getExpr().genC(pw);   // Expressão        
        pw.printNI(";");    // ';'

        pw.printBL();   //Quebra de linha
    }
}