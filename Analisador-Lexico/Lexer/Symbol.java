package Lexer;

public enum Symbol {

    EOF("eof"),
    IDENTIFIER("Ident"),
    NUMBER("Number"),
    BEGIN("BEGIN"),
    PROGRAM("PROGRAM"),
    END("END"),
    STRING("STRING"),
    INT("INT"),
    FLOAT("FLOAT"),
    FUNCTION("FUNCTION"),
    OBRACK('('),
    CBRACK(')'),
    QUOTA("\""),
    PLUS("+"),
    MINUS("-"),
    MULT("*"),
    DIV("/"),
    ASSIGN(":="),
    COMMA(","),
    SEMICOLON(";");

    Symbol(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }

    private String name;
}
