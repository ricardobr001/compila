package Lexer;

import java.util.*;
import Error.*;

public class Lexer {

	// apenas para verificacao lexica
	public static final boolean DEBUGLEXER = true;

    public Lexer( char []input, CompilerError error ) {
        this.input = input;
        // add an end-of-file label to make it easy to do the lexer
        input[input.length - 1] = '\0';
        // number of the current line
        lineNumber = 1;
        tokenPos = 0;
        this.error = error;
    }

    // contains the keywords
    static private Hashtable<String, Symbol> keywordsTable;

    // this code will be executed only once for each program execution
    static {
        keywordsTable = new Hashtable<String, Symbol>();
		
		// Palavras reservadas
		keywordsTable.put("BEGIN", Symbol.BEGIN);
        keywordsTable.put("END", Symbol.END);
		keywordsTable.put("Ident", Symbol.IDENT);
		keywordsTable.put("IntNumber", Symbol.INTLITERAL);
		keywordsTable.put("FloatNumber", Symbol.FLOATLITERAL);
		keywordsTable.put("StringLiteral", Symbol.STRINGLITERAL);
		keywordsTable.put("PROGRAM", Symbol.PROGRAM);
		keywordsTable.put("BEGIN", Symbol.BEGIN);
		keywordsTable.put("FUNCTION", Symbol.FUNCTION);
		keywordsTable.put("READ", Symbol.READ);
		keywordsTable.put("WRITE", Symbol.WRITE);
		keywordsTable.put("IF", Symbol.IF);
		keywordsTable.put("THEN", Symbol.THEN);
		keywordsTable.put("ELSE", Symbol.ELSE);
		keywordsTable.put("ENDIF", Symbol.ENDIF);
		keywordsTable.put("RETURN", Symbol.RETURN);
		keywordsTable.put("FOR", Symbol.FOR);
		keywordsTable.put("ENDFOR", Symbol.ENDFOR);
		keywordsTable.put("FLOAT", Symbol.FLOAT);
		keywordsTable.put("INT", Symbol.INT);
		keywordsTable.put("VOID", Symbol.VOID);
		keywordsTable.put("STRING", Symbol.STRING);

		// Operadores
		keywordsTable.put("+", Symbol.PLUS);
		keywordsTable.put("-", Symbol.MINUS);
		keywordsTable.put("*", Symbol.MULT);
		keywordsTable.put("/", Symbol.DIV);
		keywordsTable.put("=", Symbol.EQUAL);
		keywordsTable.put("<", Symbol.LT);
		keywordsTable.put(">", Symbol.GT);
		keywordsTable.put("(", Symbol.LPAR);
		keywordsTable.put(")", Symbol.RPAR);
		keywordsTable.put(":=", Symbol.ASSIGN);
		keywordsTable.put(",", Symbol.COMMA);
		keywordsTable.put(";", Symbol.SEMICOLON);

		// EOF
		keywordsTable.put("eof", Symbol.EOF);
    }

    public void nextToken() {
    	//Pula espaços e quebra de linhas
		while (input[tokenPos] == ' ' || input[tokenPos] == '\n' || input[tokenPos] == '\t'){
			if (input[tokenPos] == '\n'){
				lineNumber++;
			}
			tokenPos++;
		}

		//verifica se chegou no fim
		if (input[tokenPos] == '\0'){
			token = Symbol.EOF;
			return;
		}

		//Verifica linhas comentadas
		if (input[tokenPos] == '-' && input[tokenPos + 1] == '-'){
			while (input[tokenPos] != '\n'){
				tokenPos++;
			}

			nextToken();
			return;
		}

		//Verifica se é um número
		String aux = new String();
		while(Character.isDigit(input[tokenPos])){
			aux = aux + input[tokenPos];
			tokenPos++;
		}

		// Se a string aux não for vazia
		if (!aux.equals("")){
			// Se for um ponto, e o próximo for um número, temos um float
			if (input[tokenPos] == '.' && Character.isDigit(input[tokenPos + 1])){
				while(Character.isDigit(input[tokenPos])){
					aux = aux + input[tokenPos];
					tokenPos++;
				}
			}

			// Se não temos um int
			else{
				numberValue = Integer.parseInt(aux);

				// Verifica se o número é maior que o valor máximo permitido
				if (numberValue > MaxValueInteger){
					error.signal("Numero maior que 32768");
				}

				token = Symbol.INTLITERAL;
			}
		}

		// Verifica se é string
		else {	
			// Enquanto for letra, anda o token e armazena na string aux
			while(Character.isLetter(input[tokenPos])){
				aux = aux + input[tokenPos];
				tokenPos++;
			}
			//System.out.println("STRING LIDA = " + aux);	
			//System.out.println("TOKEN ="+ input[tokenPos] + input[tokenPos+1]+input[tokenPos+2]);

			// Se o conteudo não for vazio, pode ser uma palavra reservada ou ident
			if (!aux.equals("")){
				// Verificamos se o conteudo está na tabela hash
				Symbol temp = keywordsTable.get(aux);

				// Se estiver na tabela hash
				if (temp != null){
					// O token toma o valor encontrado na tabela
					token = temp;
				}

				// Pode ser um ident
				else {
					// Enquanto for letra ou digito, continua lendo para a string aux
					while (Character.isLetterOrDigit(input[tokenPos])){
						aux = aux + input[tokenPos];
						tokenPos++;
					}

					// Se o ident lido for válido
					if (validIdent(aux)){
						token = Symbol.IDENT;
						stringValue = aux;
					}
				}
			}
			// Pode ser stringliteral ou simbolo, a string aux aqui com certeza sera vazia
			else {
				// Se não for uma aspas, é um simbolo
				if (input[tokenPos] != '\"'){
					// Como temos apenas um simbolo reservado tem dois caracteres, adicionamos o primeiro
					aux = aux + input[tokenPos];
					tokenPos++;

					// Verificando se o simbolo está na tabela hash
					Symbol temp = keywordsTable.get(aux);

					// Se ele estiver na tabela
					if (temp != null){
						token = temp;
					}

					// Se ele não estiver, pode ser o simbolo ASSIGN ":=", adicionamos o próximo simbolo a string aux
					else {
						aux = aux + input[tokenPos];
						tokenPos++;

						// Verificando se o simbolo está na tabela hash
						temp = keywordsTable.get(aux);

						// Se ele estiver na tabela
						if (temp != null){
							token = temp;
						}
					}
				}

				// Caso contrário é stringliteral
				else{
					int aspas = 0;

					// Enquanto não encontrar as duas aspas, popula a string aux
					while (aspas < 2){
						// Se encontrar uma aspas, soma no contador
						if (input[tokenPos] == '\"'){
							aspas++;
						}
						
						aux = aux + input[tokenPos];
						tokenPos++;
					}

					// Valida a string aux
					if (validStringLiteral(aux)){
						token = Symbol.STRINGLITERAL;
					}
				}
			}		
		}

		if (DEBUGLEXER)
			System.out.println(token.toString());

        lastTokenPos = tokenPos - 1;
    }

    // return the line number of the last token got with getToken()
    public int getLineNumber() {
        return lineNumber;
    }

    public String getCurrentLine() {
        int i = lastTokenPos;
        if ( i == 0 )
            i = 1;
        else
            if ( i >= input.length )
                i = input.length;

        StringBuffer line = new StringBuffer();
        // go to the beginning of the line
        while ( i >= 1 && input[i] != '\n' )
            i--;
        if ( input[i] == '\n' )
            i++;
        // go to the end of the line putting it in variable line
        while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
            line.append( input[i] );
            i++;
        }
        return line.toString();
    }

    public String getStringValue() {
        return stringValue;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public char getCharValue() {
        return charValue;
	}

	private boolean containsOnlyNumbersAndDigits(String str){
		// Se a str for vazia ou nula, retorna falso
		if (str == null || str.length() == 0){
			return false;
		}

		for (int i = 0 ; i < str.length() ; i++){
			// Se a string possuir algo que não seja digito ou letra, retorna falso
			if (!Character.isLetterOrDigit(str.charAt(i))){
				return false;
			}
		}

		return true;
	}

	// O IDENT pode ter no máximo tamanho 30, com o '\0', tamanho 31
	private boolean validIdent(String str){
		// Precisa começar com uma letra
		if (Character.isLetter(str.charAt(0))){
			// Se for maior que 30, é um identificador invalido
			if (str.length() > 30){
				error.signal("A variavel precisa ter um tamanho maximo de 29 caracteres");
			}

			// Se tiver algum valor diferente de numeros e digitos, gera um erro
			else if (!containsOnlyNumbersAndDigits(str)){
				error.signal("Nome de variavel invalido, encontrado caractere invalido em \'" + str + "\'");
			}

			token = Symbol.IDENT;
			stringValue = str;
		}
		else{
			error.signal("O nome da variavel precisa começar com uma letra, encontrado caractere invalido em \'" + str + "\'");
		}

		return true;
	}

	// A STRINGLITERAL 80, incluindo o '\0', logo a STRINGLITERAL entre as aspas, tem tamanho 81 no máximo 
	// Pois 80 - 1 = 79
	// 79 + as duas aspas = 81
	private boolean validStringLiteral(String str){
		// Se começar com \", é uma string literal
		if (str.charAt(0) == '\"'){
			// Se for maior que 81, a string literal tem tamanho maior que 80
			if (str.length() > 81){
				error.signal("String maior que 79 caracteres");
			}

			// Se o ultimo caracter não for uma '"'
			else if (str.charAt(str.length() - 1) != '\"'){
				error.signal("A string precisa estar entre aspas duplas");
			}

			return true;
		}

		return false;
	}
	
	// Max ident size and max string size
	private int MaxStringSize = 81;
	private int MaxIdentSize = 30;

    // current token
    public Symbol token;
    private String stringValue;
    private int numberValue;
    private char charValue;

    private int  tokenPos;
    //  input[lastTokenPos] is the last character of the last token
    private int lastTokenPos;
    // program given as input - source code
    private char []input;

    // number of current line. Starts with 1
    private int lineNumber;

    private CompilerError error;
    private static final int MaxValueInteger = 32768;
}
