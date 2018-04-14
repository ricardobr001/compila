# Analisador Léxico
Analisador Léxico para a gramática [LITTLE](https://sites.google.com/site/amitsabne/little-programming-language---grammar).

# Compiler
Métodos concluídos da classe Compiler

### Program
- [x] program
- [x] id
- [ ] pgm_body
    - [x] Variable
    - [ ] Function
- [x] decl
    - [x] Variable<String>
    - [x] Variable<FLOAT>
    - [x] Variable<INT>

### Global String Declaration
- [x] string_decl_list
- [x] string_decl
- [x] str
- [x] str_decl_tail

### Variable Declaration
- [x] var_decl_list
- [x] var_decl
- [x] var_type
- [ ] any_type
- [x] id_list
- [x] id_tail
- [x] var_decl_tail

### Function Paramater List
- [ ] param_decl_list
- [ ] param_decl
- [ ] param_decl_tail

### Function Declarations
- [ ] func_declarations
- [ ] func_decl
- [ ] func_decl_tail
- [ ] func_body

### Statement List
- [ ] stmt_list
- [ ] stmt_tail
- [ ] stmt

### Basic Statements
- [ ] assign_stmt
- [ ] assign_expr
- [ ] read_stmt
- [ ] write_stmt
- [ ] return_stmt

### Expressions
- [ ] expr
- [ ] expr_tail
- [ ] factor
- [ ] factor_tail
- [ ] postfix_expr
- [ ] call_expr
- [ ] expr_list
- [ ] expr_list_tail
- [ ] primary
- [ ] addop
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

- [ ]

# Correções
stmt -> TODAS AS REGRAS ANTERIORES | call_expr ;<br/>
id call_expr -> ( {expr_list} ) `ID REMOVIDO DO call_expr`<br/>
id assign_expr -> := expr `ID REMOVIDO DO assign_expr`<br/>

Ou seja, toda chamada de `call_expr()` e `assign_expr()` precisa ter a chamada do `id()` antes!!
