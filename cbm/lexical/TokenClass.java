package cbm.lexical;

public enum TokenClass{
    ID,

    // Operadores
    OP_ADD,
    OP_SUB,
    OP_MULT,
    OP_DIV,
    OP_POT,
    OP_MOD,
    OP_NOT,
    OP_OR,
    OP_AND,
    OP_MAIOR,
    OP_MENOR,
    OP_IGUAL,
    OP_MAIOR_IG,
    OP_MENOR_IG,
    OP_N_IGUAL,
    OP_ATR,

    // Delimitadores
    ABRE_PAR,
    FECHA_PAR,
    ABRE_COL,
    FECHA_COL,
    ABRE_CHAVE,
    FECHA_CHAVE,
    TERMINAL,
    SEP,
    ASPAS,
    EOF_TOKEN,

    // Constantes Literais
    CONST_INT,
    CONST_FLOAT,
    CONST_CHAR,
    CONST_STRING,
    CONST_BOOL,

    // Palavras Reservadas
    PR_FUNCTION,
    PR_PROCEDURE,
    PR_MAIN,
    PR_RETURN,
    PR_PRINT,
    PR_INPUT,
    PR_WRITE,
    PR_IF,
    PR_ELSE,
    PR_FOR,
    PR_WHILE,
    TIPO_INT,
    TIPO_FLOAT,
    TIPO_CHAR,
    TIPO_STRING,
    TIPO_BOOL,
    TIPO_VOID,

    // Erros Léxicos
    BAD_TOKEN
}