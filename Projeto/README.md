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
- [x] func_body
    - [x] decl
    - [x] stmt_list

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
    - [x] primary
    - [x] call_expr
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
- [x] for_stmt `Problema de sobrescrita no cabeçalho do for caso tenha mais de uma condição, atribuição no inicio e fim (WHILE)`

# Lexer
Métodos concluídos da classe Lexer

- [x] nextToken

# AST
Objetos criados no pacote AST

- [x] Variable
- [x] PgmBody
    - [x] Function `atributo PgmBody`
        - [x] FunctionBody `atributo Function`
    - [x] Statement
        - [x] AssignStatement `extends Statement`
        - [x] ReadStatement `extends Statement`
        - [x] WriteStatement `extends Statement`
        - [x] IfStatement `extends Statement`
        - [x] ElseStatement `extends Statement`
        - [x] ReturnStatement `extends Statement`
        - [x] ForStatement `extends Statement`
- [x] Expr `abstract | atributo Statement`
    - [x] CompositeExpr `extends Expr`
    - [x] VariableExpr `extends Expr | atributo CompositeExpr`
    - [x] IntNumberExpr `extends Expr | atributo CompositeExpr`
    - [x] FloatNumberExpr `extends Expr | atributo CompositeExpr`
    - [x] ParenthesesExpr `extends Expr | atributo CompositeExpr`
- [x] IfBody `atributo IfStatement | ElseStatement`
- [x] ForBody `atributo ForStatement`
- [x] PW `PrintWriter`

# Análise Semântica
- [x] Classe da tabela Hash de nomes de variáveis globais e locais `Falta terminar`
- [x] Classe da tabela Hash de nomes de função
- [x] Verificar se a variavel já foi declarada
- [x] Verificar se o valor atribuído respeita o tipo da variável
- [x] Operações aritmética apenas com números
- [x] Condição retorna verdadeiro ou falso

# Correções
stmt -> `TODAS AS REGRAS ANTERIORES | call_expr ;`<br/>
id call_expr -> ( {expr_list} ) `ID REMOVIDO DO call_expr`<br/>
id assign_expr -> := expr `ID REMOVIDO DO assign_expr`<br/>

stmt -> call_stmt<br/> `Já atendido pelas mudanças acima`
call_stmt -> call_expr ;<br/> `Já atendido pelas mudanças acima`

Ou seja, toda chamada de `call_expr()` e `assign_expr()` precisa ter a chamada do `id()` antes!!

# Observações
`STRING` deve ser tratado como constante, não pode ser lido na função `READ`<br/>
Obrigatóriamente o programa deve ter a função `int main()` sem parâmetros