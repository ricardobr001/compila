# Analisador Léxico
Analisador Léxico para a gramática [LITTLE](https://sites.google.com/site/amitsabne/little-programming-language---grammar).

### Compiler 
Métodos concluídos da classe Compiler

* Program
- [x] program
- [x] id
- [x] pgm_body
- [x] decl

* Global String Declaration
- [x] string_decl_list
- [x] string_decl
- [x] str **TERMINAR**
- [x] str_decl_tail **DUVIDA**

* Variable Declaration
- [x] var_decl_list **DUVIDA**
- [x] var_decl **DUVIDA**
- [x] var_type
- [x] any_type **DUVIDA**
- [x] id_list
- [x] id_tail **DUVIDA**
- [x] var_decl_tail

* Function Paramater List
- [x] param_decl_list
- [x] param_decl
- [x] param_decl_tail **DUVIDA**

* Function Declarations
- [x] func_declarations **DUVIDA**
- [x] func_decl **DUVIDA**
- [x] func_decl_tail **DUVIDA**
- [x] func_body

* Statement List
- [ ] stmt_list
- [ ] stmt_tail
- [ ] stmt

* Basic Statements
- [ ] assign_stmt
- [ ] assign_expr
- [ ] read_stmt
- [ ] write_stmt
- [ ] return_stmt

* Expressions
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

* Complex Statements and Condition
- [ ] if_stmt
- [ ] else_part
- [ ] cond
- [ ] compop
- [ ] for_stmt

### Lexer
Métodos concluídos da classe Lexer

- [ ] nextToken

### Correções
stmt -> TODAS AS REGRAS ANTERIORES | call_expr ;