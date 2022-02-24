package JORG.lexical;

public enum TokenCategory {
    // Identificador
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
    OP_SIZE(19),

    // Delimitadores
    ABRE_PAR(20),
    FECHA_PAR(21),
    ABRE_COL(22),
    FECHA_COL(23),
    ABRE_CHAVE(24),
    FECHA_CHAVE(25),
    TERMINAL(26),
    SEP(27),
    ASPAS(28),
    EOF_TOKEN(29),

    // Constantes Literais
    CONST_INT(30),
    CONST_FLOAT(31),
    CONST_CHAR(32),
    CONST_STRING(33),
    CONST_BOOL(34),

    // Palavras Reservadas
    PR_FUNCTION(35),
    PR_MAIN(36),
    PR_RETURN(37),
    PR_INPUT(38),
    PR_WRITE(39),
    PR_WRITELN(40),
    PR_IF(41),
    PR_ELSE(42),
    PR_FOR(43),
    PR_WHILE(44),
    PR_BREAK(45),
    PR_CONST(46),
    TIPO_INT(47),
    TIPO_FLOAT(48),
    TIPO_CHAR(49),
    TIPO_STRING(50),
    TIPO_BOOL(51),
    TIPO_VOID(52),

    // Erros Léxicos
    BAD_TOKEN(53);

    private final int numericCode;

    private TokenCategory(int numericCode) {
        this.numericCode = numericCode;
    }

    public int getNumericCode() {
        return this.numericCode;
    }
}