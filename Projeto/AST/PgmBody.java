package AST;

import java.util.*;

public class PgmBody {
    private ArrayList<Variable> ArrayVar;
    //private ArrayList<Function> ArrayFunc;

    public PgmBody(ArrayList<Variable> ArrayVar) {
        this.ArrayVar = ArrayVar;
        //this.ArrayFunc = ArrayFunc;
    }

    public void genC(PW pw) {
        pw.println("#include <stdio.h>\n");
        pw.println("int main () {");
        pw.add();
        for (Variable v : ArrayVar)
            v.genC(pw);
        
        pw.sub();
        pw.println("}");
    }
}
