package AST;

import java.util.*;
import Lexer.*;

public class Variable {
    private String var;
    private Symbol tipo;
    private String valor;

    public Variable(String var, Symbol tipo, String valor) {
        this.var = var;
        this.tipo = tipo;
        this.valor = valor;
    }

    public void setValor(String valor){
        this.valor = valor;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void genC(PW pw) {
        if (tipo == Symbol.STRING){
            pw.println("char " + this.var + "[] = " + this.valor + ";");
        }
        else if (tipo == Symbol.INT){
            pw.println("int " + this.var + ";");
        }
        else {
            pw.println("float " + this.var + ";");
        }
        
        //System.out.println(this.var + " := " + this.valor);
    }

    public String getValor(){
        return this.valor;
    }

    public String getVar(){
        return this.var;
    }
}
