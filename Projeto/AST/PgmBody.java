package AST;

import java.util.ArrayList;

public class PgmBody {
    private ArrayList<Variable> arrayVar;
    private ArrayList<Function> arrayFunc;

    public PgmBody(ArrayList<Variable> arrayVar, ArrayList<Function> arrayFunc) {
        this.arrayVar = arrayVar;
        this.arrayFunc = arrayFunc;
    }

    public void genC(PW pw) {
        pw.println("#include <stdio.h>\n");
        //pw.println("int main () {");
        //pw.add();

        // Se não tiver variaveis globais e não tiver nenhuma função declarada
        if (arrayVar.isEmpty() && arrayFunc.contains(null)){
            pw.println("int main(){}");
        }

        // Se tiver variaveis globais e não tiver nenhuma função declarada
        else if (!arrayFunc.isEmpty() && arrayFunc.contains(null)){
            for (Variable v : arrayVar){
                v.genC(pw);
            }
            
            pw.println("int main(){}");
        }

        // Pode ter variaveis globais e funções declaradas
        else {
            for (Variable v : arrayVar){
                v.genC(pw);
            }
    
            for (Function f : arrayFunc){
                f.genC(pw);
            }
        }       
        
        //pw.sub();
        //pw.println("}");
    }
}
