# Analisador Léxico
Analisador Léxico para a gramática [LITTLE](https://sites.google.com/site/amitsabne/little-programming-language---grammar).

# Compiler
Métodos concluídos da classe Compiler

### Program
- [x] program
- [x] id
- [x] pgm_body
- [x] decl

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
- [x] func_body

### Statement List
- [x] stmt_list
- [x] stmt_tail
- [x] stmt

### Basic Statements
- [x] assign_stmt
- [x] assign_expr
- [x] read_stmt
- [x] write_stmt
- [x] return_stmt

### Expressions
- [x] expr
- [x] expr_tail
- [x] factor
- [x] factor_tail
- [x] postfix_expr
- [x] call_expr
- [x] expr_list
- [x] expr_list_tail
- [x] primary
- [x] addop
- [x] mulop

### Complex Statements and Condition
- [x] if_stmt
- [x] else_part
- [x] cond
- [x] compop
- [x] for_stmt

# Lexer
Métodos concluídos da classe Lexer

- [x] nextToken

# Correções
stmt -> TODAS AS REGRAS ANTERIORES | call_expr ;<br/>
id call_expr -> ( {expr_list} ) `ID REMOVIDO DO call_expr`<br/>
id assign_expr -> := expr `ID REMOVIDO DO assign_expr`<br/>

Ou seja, toda chamada de `call_expr()` e `assign_expr()` precisa ter a chamada do `id()` antes!!
