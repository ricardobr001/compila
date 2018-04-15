package AST;

import java.util.ArrayList;
import Lexer.Symbol;

public class ReadStatement extends Statement{
    private ArrayList<Variable> arrayVar;

    public ReadStatement(Variable v, CompositeExpr e, ArrayList<Variable> vars){
        super(v, e);
        this.arrayVar = vars;
    }

    public ArrayList<Variable> getArrayVar(){
        return this.arrayVar;
    }

    @Override
    public void genC(PW pw){
        pw.printBL();   // Quebra de linha

        pw.printI();    // Ident
        pw.printNI("scanf(\"");

        // Imprime a mascara de leitura das variáveis
        for (int i = 0 ; i < arrayVar.size() ; i++){
            if (arrayVar.get(i).getTipo() == Symbol.INT){
                pw.printNI("%d");
            }
            else {
                pw.printNI("%f");
            }            
        }

        // Acabou a mascara
        pw.printNI("\", ");

        // Imprimindo as variaveis
        for (int i = 0 ; i < arrayVar.size() ; i++){
            if (i + 1 == arrayVar.size()){
                if (arrayVar.get(i).getTipo() == Symbol.INT){
                    pw.printNI("&" + arrayVar.get(i).getVar());
                }
                else {
                    pw.printNI("&" + arrayVar.get(i).getVar());
                }
            }
            else{
                if (arrayVar.get(i).getTipo() == Symbol.INT){
                    pw.printNI("&" + arrayVar.get(i).getVar() + ", ");
                }
                else {
                    pw.printNI("&" + arrayVar.get(i).getVar() + ", ");
                }
            }
        }
     
        pw.printNI(");");    // ');'
        pw.printBL();   //Quebra de linha
    }
}