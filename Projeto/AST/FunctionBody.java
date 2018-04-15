package AST;

import java.util.ArrayList;

public class FunctionBody{
    private ArrayList<Variable> arrayVar;
    private ArrayList<Statement> arrayStmt;
    
    public FunctionBody(ArrayList<Variable> var, ArrayList<Statement> stmt){
        this.arrayVar = var;
        this.arrayStmt = stmt;
    }

    public void genC(PW pw){
        for (Variable v : arrayVar){
            v.genC(pw);
        }
        //pw.println("arrayStmt = " + arrayStmt.size());
        for (Statement s : arrayStmt){
            s.genC(pw);
        }
    }

    public void setArrayVar(ArrayList<Variable> var){
        this.arrayVar = var;
    }
}