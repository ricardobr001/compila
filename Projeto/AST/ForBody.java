package AST;

import java.util.ArrayList;

public class ForBody{
    private ArrayList<Statement> arrayStmt;

    public ForBody(ArrayList<Statement> stmt){
        this.arrayStmt = stmt;
    }

    public void genC(PW pw){
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