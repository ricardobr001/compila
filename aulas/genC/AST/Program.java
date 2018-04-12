
package AST;

import java.util.*;

public class Program {

    public Program( ArrayList<Variable> arrayVariable, AssignmentStatement as ) {
        this.arrayVariable = arrayVariable;
        this.assignStm = as;
    }

    public void genC(PW pw) {
        pw.println("#include <stdio.h>\n");
		pw.println("");
        pw.println("int main() {");
		
		pw.add(); 

        if ( arrayVariable != null ) {
             // generate code for the declaration of variables
            for ( Variable v : arrayVariable )
                v.genC(pw);
        }
          
        assignStm.genC(pw);
		pw.println("return 0;");

		pw.sub();
        pw.println("}");
    }                             
        
    private ArrayList<Variable> arrayVariable;
    private AssignmentStatement assignStm;
}
