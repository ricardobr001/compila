package AST;

import Lexer.*;

public class CompositeExpr extends Expr{
    private Expr esquerda, direita;
    private Symbol operador;

    public CompositeExpr(Expr esquerda, Symbol operador, Expr direita){
        this.esquerda = esquerda;
        this.direita = direita;
        this.operador = operador;
    }

    public void genC(PW pw){
        // pw.printNI("(");    // Sem ident
        esquerda.genC(pw);

        if (this.operador != null){
            pw.printNI(" " + operador.toString() + " ");
        }

        if (this.direita != null){
            direita.genC(pw);
        }
        // pw.printNI(")");
    }

    public void setEsquerda(Expr expression){
        this.esquerda = expression;
    }

    public void setDireita(Expr expression){
        this.direita = expression;
    }

    public void setOperador(Symbol operador){
        this.operador = operador;
    }

    public Expr getEsquerda(){
        return this.esquerda;
    }

    public Expr getDireita(){
        return this.direita;
    }

    public Symbol getOperador(){
        return this.operador;
    }
}
