package AST;

public class Variable {

    public Variable( String name ) {
        this.name = name;
    }
    
    public String getName() { return name; }
    
    public void genC(PW pw) {
        pw.println( "int " + name + ";" );
    }
    
    private String name;
}
