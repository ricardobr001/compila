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

    public void genC() {
        System.out.println(this.var + " := " + this.valor);
    }

    public String getValor(){
        return this.valor;
    }

    public String getVar(){
        return this.var;
    }
}
