# Analisador Léxico
Analisador Léxico para a gramática [LITTLE](https://sites.google.com/site/amitsabne/little-programming-language---grammar).

# Compiler
Métodos concluídos da classe Compiler

### Program
- [x] program
- [x] id
- [x] pgm_body
    - [x] Variable
    - [x] Function
- [x] decl
    - [x] Variable `String`
    - [x] Variable `FLOAT`
    - [x] Variable `INT`

### Global String Declaration
- [x] string_decl_list
- [x] string_decl
- [x] str
- [x] str_decl_tail

### Variable Declaration
- [x] var_decl_list
- [x] var_decl
- [x] var_type
- [x] any_type
- [x] id_list
- [x] id_tail
- [x] var_decl_tail

### Function Paramater List
- [x] param_decl_list
- [x] param_decl
- [x] param_decl_tail

### Function Declarations
- [x] func_declarations
- [x] func_decl
- [x] func_decl_tail
- [ ] func_body
    - [x] decl
    - [ ] stmt_list

### Statement List
- [x] stmt_list `Terminar stmt`
- [x] stmt_tail
- [ ] stmt 

### Basic Statements
- [x] assign_stmt
- [x] assign_expr
- [ ] read_stmt
- [ ] write_stmt
- [ ] return_stmt

### Expressions
- [x] expr
- [x] expr_tail
- [ ] factor
- [ ] factor_tail
- [ ] postfix_expr
    - [ ] primary
    - [x] call_expr
- [x] call_expr
- [x] expr_list
- [x] expr_list_tail
- [ ] primary
- [x] addop
- [ ] mulop

### Complex Statements and Condition
- [ ] if_stmt
- [ ] else_part
- [ ] cond
- [ ] compop
- [ ] for_stmt

# Lexer
Métodos concluídos da classe Lexer

- [x] nextToken **Ajustes necessários para recuperar o stringValue no compiler para utilizar no genC() dos objetos da AST**

# AST
Objetos criados no pacote AST

- [x] Variable
- [x] PgmBody
    - [x] Function `atributo /\`
        - [x] FunctionBody `atributo /\`
    - [x] Statement
        - [x] AssignStatement `extends /\`
- [x] Expr `abstract | atributo Statement`
    - [x] CompositeExpr `extends`
    - [x] VariableExpr `extends`
    - [x] IntNumberExpr `extends`
    

# Correções
stmt -> TODAS AS REGRAS ANTERIORES | call_expr ;<br/>
id call_expr -> ( {expr_list} ) `ID REMOVIDO DO call_expr`<br/>
id assign_expr -> := expr `ID REMOVIDO DO assign_expr`<br/>

Ou seja, toda chamada de `call_expr()` e `assign_expr()` precisa ter a chamada do `id()` antes!!
