package Lexer;

public enum Symbol {

      EOF("eof"),
      IDENT("Ident"),
      NUMBER("Number"),
      BEGIN("begin"),
      END("end"),
      PLUS("+"),
      MINUS("-"),
      MULT("*"),
      DIV("/"),
      ASSIGN("="),
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
