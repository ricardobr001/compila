package AST;

// FALTA TERMINAR
public class SymbolTable{
    private HashTable localVariableTable;
    private HashTable globaVariableTable;

    public SymbolTable(){
        this.localVariableTable = new HashTable();
        this.globaVariableTable = new HashTable();
    }

    // Limpa tabela de variáveis locais
    public void resetLocalVariableTable(){
        this.localVariableTable.clear();
    }
}