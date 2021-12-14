package JORG.lexical;

public enum TokenCategory {
    ID(1),

    // Operadores
    OP_ADD(2),
    OP_SUB(3),
    OP_MULT(4),
    OP_DIV(5),
    OP_POT(6),
    OP_MOD(7),
    OP_NOT(8),
    OP_OR(9),
    OP_AND(10),
    OP_MAIOR(11),
    OP_MENOR(12),
    OP_IGUAL(13),
    OP_MAIOR_IG(14),
    OP_MENOR_IG(15),
    OP_N_IGUAL(16),
    OP_ATR(17),
    OP_CONCAT(18),

    // Delimitadores
    ABRE_PAR(19),
    FECHA_PAR(20),
    ABRE_COL(21),
    FECHA_COL(22),
    ABRE_CHAVE(23),
    FECHA_CHAVE(24),
    TERMINAL(25),
    SEP(26),
    ASPAS(27),
    EOF_TOKEN(28),

    // Constantes Literais
    CONST_INT(29),
    CONST_FLOAT(30),
    CONST_CHAR(31),
    CONST_STRING(32),
    CONST_BOOL(33),

    // Palavras Reservadas
    PR_FUNCTION(34),
    PR_PROCEDURE(35),
    PR_MAIN(36),
    PR_RETURN(37),
    PR_INPUT(38),
    PR_WRITE(39),
    PR_IF(40),
    PR_ELSE(41),
    PR_FOR(42),
    PR_WHILE(43),
    TIPO_INT(44),
    TIPO_FLOAT(45),
    TIPO_CHAR(46),
    TIPO_STRING(47),
    TIPO_BOOL(48),
    TIPO_VOID(49),

    // Erros Léxicos
    BAD_TOKEN(50);

    private final int numericCode;

    private TokenCategory(int numericCode) {
        this.numericCode = numericCode;
    }

    public int getNumericCode() {
        return this.numericCode;
    }
}