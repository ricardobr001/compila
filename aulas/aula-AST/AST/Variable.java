package AST;

public class Variable {

    public Variable( String name ) {
        this.name = name;
    }
    
    public String getName() { return name; }
    
    public void genC() {
        System.out.println( "Variable " + name);
    }
    
    private String name;
}
