package AST;

import java.util.ArrayList;

import Lexer.*;

public class Function{
    private Symbol tipoRetorno;
    private String nomeFuncao;
    private ArrayList<Variable> parametros;
    private FunctionBody corpo;

    public Function(Symbol tipoRetorno, String nomeFuncao, ArrayList<Variable> parametros){
        this.tipoRetorno = tipoRetorno;
        this.nomeFuncao = nomeFuncao;
        this.parametros = parametros;
    }

    public void genC(PW pw){
        if (tipoRetorno == Symbol.INT){
            pw.print("int " + this.nomeFuncao + "(");   // Com ident antes da string
        }
        else if (tipoRetorno == Symbol.FLOAT){
            pw.print("float " + this.nomeFuncao + "(");
        }
        else if (tipoRetorno == Symbol.VOID){
            pw.print("void " + this.nomeFuncao + "(");
        }
       
        for (int i = 0 ; i < parametros.size() ; i++){
            if (i + 1 == parametros.size()){
                if (parametros.get(i).getTipo() == Symbol.INT){
                    pw.print("int " + parametros.get(i).getVar());
                }
                else {
                    pw.print("float " + parametros.get(i).getVar());
                }
            }
            else{
                if (parametros.get(i).getTipo() == Symbol.INT){
                    pw.print("int " + parametros.get(i).getVar() + ", ");
                }
                else {
                    pw.print("float " + parametros.get(i).getVar() + ", ");
                }
            }
        }
        pw.println(")");
        pw.println("{");

        // Incrementa a tabulação
        pw.add();

        // Imprime o corpo da função
        corpo.genC(pw);

        // Decrementa a tabulação e fecha a função
        pw.sub();
        pw.println("}");
        
        
    }

    public void setParametros(ArrayList<Variable> var){
        this.parametros = var;
    }

    public void setCorpo(FunctionBody corpo){
        this.corpo = corpo;
    }

    public String getNome(){
        return this.nomeFuncao;
    }

    public Symbol getTipo(){
        return this.tipoRetorno;
    }

    public ArrayList<Variable> getParametros(){
        return this.parametros;
    }
}