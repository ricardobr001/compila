package AST;

import java.util.*;

public class PgmBody {
    private ArrayList<Variable> ArrayVar;
    //private ArrayList<Function> ArrayFunc;

    public PgmBody(ArrayList<Variable> ArrayVar) {
        this.ArrayVar = ArrayVar;
        //this.ArrayFunc = ArrayFunc;
    }

    public void genC() {
        for ( Variable v : ArrayVar)
            v.genC();
        //Fazer
    }
}
