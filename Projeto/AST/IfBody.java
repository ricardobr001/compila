package AST;

import java.util.ArrayList;

public class IfBody{
    private ArrayList<Statement> arrayStmt;
    
    public IfBody(ArrayList<Statement> stmt){
        this.arrayStmt = stmt;
    }

    public void genC(PW pw){
        // pw.println("arrayStmt = " + arrayStmt.size());
        for (Statement s : arrayStmt){
            s.genC(pw);
        }
    }

    public void setArrayVar(ArrayList<Statement> stmt){
        this.arrayStmt = stmt;
    }

    public ArrayList<Statement> getArrayStmt(){
        return this.arrayStmt;
    }
}