package AST;

import java.util.*;

public class Program {

    public Program( ArrayList<Variable> arrayVariable, AssignmentStatement as ) {
        this.arrayVariable = arrayVariable;
        this.assignStm = as;
    }

    public void genC() {
        System.out.println("Inicio\n");
        for ( Variable v : arrayVariable )
            v.genC();
          
        assignStm.genC();
        System.out.println("\nFim\n");
    }                             
        
    private ArrayList<Variable> arrayVariable;
    private AssignmentStatement assignStm;
}
