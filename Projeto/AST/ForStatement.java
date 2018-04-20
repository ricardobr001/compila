package AST;

import java.util.ArrayList;

public class ForStatement extends Statement{
    // super expr sera as condições do for

    private ForBody corpo;
    private ArrayList<AssignStatement> atribuicaoInicio;
    private ArrayList<AssignStatement> atribuicaoFinal;

    public ForStatement(Variable var, CompositeExpr expr){
        super(var, expr);
    }

    @Override
    public void genC(PW pw){
        pw.printBL();   // Quebra de linha

        // ----- Cabeçalho do FOR ------
        pw.printI();    // Ident
        pw.printNI("for ("); //inicio

        // Atribuições do inicio
        for (AssignStatement as : atribuicaoInicio){
            as.genCFOR(pw);
        }

        pw.printNI(" ; ");

        // Condição do for
        super.getExpr().genC(pw);

        pw.printNI(" ; ");

        // Atribuições do fim
        for (AssignStatement as : atribuicaoFinal){
            as.genCFOR(pw);
        }
        pw.printNI(")");
        pw.printBL();
        // -----------------------------

        // corpo do FOR
        pw.print("{");
        pw.add();
        corpo.genC(pw);
        pw.sub();
        pw.println("}");
    }

    public void setAtribuicaoInicio(ArrayList<AssignStatement> inicio){
        this.atribuicaoInicio = inicio;
    }

    public ArrayList<AssignStatement> getAtribuicaoInicio(){
        return this.atribuicaoInicio;
    }

    public void setAtribuicaoFinal(ArrayList<AssignStatement> fim){
        this.atribuicaoFinal = fim;
    }

    public ArrayList<AssignStatement> getAtribuicaoFinal(){
        return this.atribuicaoFinal;
    }

    public void setCorpo(ForBody corpo){
        this.corpo = corpo;
    }

    public ForBody getCorpo(){
        return this.corpo;
    }
}
