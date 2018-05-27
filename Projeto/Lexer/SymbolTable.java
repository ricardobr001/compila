package Lexer;

import java.util.Hashtable;

public class SymbolTable{
    private Hashtable localVariableTable;
    private Hashtable globalVariableTable;
    private Hashtable functions;

    public SymbolTable(){
        this.localVariableTable = new Hashtable();
        this.globalVariableTable = new Hashtable();
        this.functions = new Hashtable();
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

    public void putFunc(String chave, Object valor){
        this.functions.put(chave, valor);
    }

    public Object returnFunc(String chave){
        return this.functions.get(chave);
    }
}
