package Lexer;

import java.util.Hashtable;
import AST.Variable;
import AST.Function;

public class SymbolTable{
    private Hashtable<String, Object> localVariableTable;
    private Hashtable<String, Object> globalVariableTable;
    private Hashtable<String, Object> functions;

    public SymbolTable(){
        this.localVariableTable = new Hashtable<String, Object>();
        this.globalVariableTable = new Hashtable<String, Object>();
        this.functions = new Hashtable<String, Object>();
    }

    // Limpa tabela de variáveis locais
    public void resetLocal(){
        this.localVariableTable.clear();
    }

    public Object returnLocal(String chave){
        return this.localVariableTable.get(chave);
    }

    public Object returnGlobal(String chave){
        return this.globalVariableTable.get(chave);
    }

    public void putLocal(String chave, Object valor){
        this.localVariableTable.put(chave, valor);
    }

    public void putGlobal(String chave, Object valor){
        this.globalVariableTable.put(chave, valor);
    }
}