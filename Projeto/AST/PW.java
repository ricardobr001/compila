package AST;

import java.lang.System;
import java.io.PrintWriter;

public class PW {
    int currentIndent = 0;
    static public final int green = 0, java = 1;
    int mode = green; 
    public int step = 4;
    public PrintWriter out;
    static final private String space = "                                                                                                        ";

    public PW(PrintWriter out) {
        this.out = out;
    }

    public void add() {
        currentIndent += step;
    }

    public void sub() {
        currentIndent -= step;
    }

    public void set(int ident) {
        currentIndent = ident;
    }

    public void print(String s) {
        out.print(space.substring(0, currentIndent));
        out.print(s);
    }

    public void println(String s) {
        out.print(space.substring(0, currentIndent));
        out.println(s);
    }

    public void printI(){
        out.print(space.substring(0, currentIndent));
    }

    public void printNI(String s){
        out.print(s);
    }

    public void printBL(){
        out.println("");
    }
}