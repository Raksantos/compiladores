package JORG.parser;

import JORG.lexical.LexicalScanner;
import JORG.lexical.Token;
import JORG.lexical.TokenCategory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private LexicalScanner lexicalScanner;
    private Token currentToken = null;
    private TokenCategory currentTokenCategory = null;
    private String line, lineRow;
    private Scanner scanner;

    public Parser(LexicalScanner lexicalScanner) {
        this.lexicalScanner = lexicalScanner;
    }

    public void readNextLine() {
        if (scanner.hasNextLine()) {
            lexicalScanner.nextRow();

            line = scanner.nextLine();
            lineRow = "%4d  ";

            System.out.printf(String.format(lineRow, lexicalScanner.getRow()));
            System.out.println(line);

            line += "\n";

            lexicalScanner.restartPos();
            getNextToken();

        } else {
            lexicalScanner.nextRow();
            lineRow = "%4d  ";
            System.out.printf(String.format(lineRow, lexicalScanner.getRow()));
            System.out.println();
            currentToken = new Token("EOF", TokenCategory.EOF_TOKEN, lexicalScanner.getRow(),
                    lexicalScanner.getColumn());

            currentTokenCategory = currentToken.getTokenCategory();
        }
    }

    public void getNextToken() {
        lexicalScanner.nextColumn(); // updating the current column

        if (lexicalScanner.isEOF(line) || line.charAt(lexicalScanner.getPosition()) != '\n') {
            currentToken = lexicalScanner.nextToken(line);
            if (currentToken == null) {
                readNextLine();
            }

            currentTokenCategory = currentToken.getTokenCategory();
        } else {
            readNextLine();
        }
    }

    public void parser(String filePath) {
        try {
            File file = new File(filePath);
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        line = scanner.nextLine();
        lineRow = "%4d  ";
        System.out.printf(String.format(lineRow, lexicalScanner.getRow()));
        System.out.println(line);

        line += '\n';
        lexicalScanner.restartPos();

        getNextToken();

        S();

        scanner.close();
    }

    // Inicializa leitura do c??digo e verifica se ?? uma vari??vel, array ou fun????o
    private void S() {
        if (typeCheck(currentTokenCategory) || constCheck(currentTokenCategory) ||
                currentTokenCategory == TokenCategory.PR_FUNCTION) {
            if (typeCheck(currentTokenCategory) || constCheck(currentTokenCategory)) {
                printProduction("S", "DecVar");
                DecVar();
            } else if (currentTokenCategory == TokenCategory.PR_FUNCTION) {
                printProduction("S", "DecFunc");
                DecFunc();
            } else {
                tokenExpected("'int', 'float', 'bool', 'char', 'string', 'const', 'function'");
            }

            S();
        } else if (currentTokenCategory == TokenCategory.EOF_TOKEN) {
            // Fim de arquivo
            printProduction("S", "??");
            System.out.println(currentToken.toString());
        } else {
            // Tipagem ou declara????o esperada
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'const', 'id', 'eof'");
        }
    }

    public void Atrib() {
        if (currentTokenCategory == TokenCategory.OP_ATR) {
            printProduction("Atrib", "'=' EconcOuListArr");
            System.out.println(currentToken.toString());
            getNextToken();
            EconcOuListArr();
        } else {
            printProduction("Atrib", "??");
        }
    }

    public void EconcOuListArr() {
        if (checkEconcFirst(currentTokenCategory)) {
            printProduction("EconcOuListArr", "Econc");
            Econc();
        } else if (currentTokenCategory == TokenCategory.ABRE_COL) {
            printProduction("EconcOuListArr", "ListArr");
            System.out.println(currentToken.toString());
            getNextToken();
            ListArr();

            if (currentTokenCategory == TokenCategory.FECHA_COL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("']'");
            }
        } else {
            tokenExpected("'['");
        }
    }

    public void ListArr() {
        printProduction("ListArr", "Econc ListArrX");
        Econc();
        ListArrX();
    }

    public void ListArrX() {
        if (currentTokenCategory == TokenCategory.SEP) {
            printProduction("ListArrX", "',' ListArr");
            System.out.println(currentToken.toString());
            getNextToken();
            ListArr();
        } else {
            printProduction("ListArrX", "??");
        }
    }

    private boolean constCheck(TokenCategory currentTokenCategory) {

        if (currentTokenCategory == TokenCategory.PR_CONST) {
            return true;
        }
        return false;
    }

    public void DecVar() {
        if (typeCheck(currentTokenCategory)) {
            printProduction("DecVar", "Tipo ListId ';'");
            Tipo();
            ListId();

            if (currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("';'");
            }
        } else if (constCheck(currentTokenCategory)) {
            printProduction("DecVar", "'const' Tipo ListId ';'");

            System.out.println(currentToken.toString());

            getNextToken();
            Tipo();
            ListId();

            if (currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("';'");
            }
        } else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'const'");
        }
    }

    public void ListId() {
        printProduction("ListId", "AtribOuId ListIdX");
        AtribOuId();
        ListIdX();
    }

    public void ListIdX() {
        if (currentTokenCategory == TokenCategory.SEP) {
            printProduction("ListIdX", "',' ListId");
            System.out.println(currentToken.toString());
            getNextToken();
            ListId();
        } else {
            printProduction("ListIdX", "??");
        }
    }

    public void AtribOuId() {
        if (currentTokenCategory == TokenCategory.ID) {
            printProduction("AribOuId", "'id' ArrId Atrib");
            System.out.println(currentToken.toString());
            getNextToken();
            ArrId();
            Atrib();
        } else {
            tokenExpected("'id'");
        }
    }

    public void DecFunc() {
        if (currentTokenCategory == TokenCategory.PR_FUNCTION) {
            printProduction("DecFunc", "'function' TipoFunc NomeFunc '(' ParamsDec ')' Bloco");
            System.out.println(currentToken.toString());
            getNextToken();

            TipoFunc();
            NomeFunc();

            if (currentTokenCategory == TokenCategory.ABRE_PAR) {
                System.out.println(currentToken.toString());
                getNextToken();

                ParamsDec();

                if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    Bloco();
                } else {
                    tokenExpected("')'");
                }
            } else {
                tokenExpected("'('");
            }
        } else {
            tokenExpected("'function'");
        }
    }

    public void ParamsDec() {
        if (typeCheck(currentTokenCategory)) {
            printProduction("ParamsDec", "Tipo 'id' Arr ParamsDecX");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenCategory == TokenCategory.ID) {
                System.out.println(currentToken.toString());
                getNextToken();
                Arr();
                ParamsDecX();
            } else {
                tokenExpected("'id'");
            }
        } else {
            printProduction("ParamsDec", "??");
        }
    }

    public void ParamsDecX() {
        if (currentTokenCategory == TokenCategory.SEP) {
            printProduction("ParamsDecX", "',' ParamsDec");
            System.out.println(currentToken.toString());

            getNextToken();

            ParamsDec();
        } else {
            printProduction("ParamsDecX", "??");
        }
    }

    public void TipoFunc() {
        if (typeCheck(currentTokenCategory)) {
            printProduction("TipoFunc", "Tipo");
            Tipo();
        } else if (currentTokenCategory == TokenCategory.TIPO_VOID) {
            printProduction("TipoFunc", "'void'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'void'");
        }
    }

    public void NomeFunc() {
        if (currentTokenCategory == TokenCategory.PR_MAIN) {
            printProduction("NomeFunc", "'main'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.ID) {
            printProduction("NomeFunc", "'id'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else {
            tokenExpected("'main', 'id'");
        }
    }

    public void Bloco() {
        if (currentTokenCategory == TokenCategory.ABRE_CHAVE) {
            printProduction("Bloco", "'{' Sentencas '}'");
            System.out.println(currentToken.toString());
            getNextToken();
            Sentencas();

            if (currentTokenCategory == TokenCategory.FECHA_CHAVE) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("'}'");
            }
        } else {
            tokenExpected("'{'");
        }
    }

    public void Sentencas() {
        if (typeCheck(currentTokenCategory)) {
            printProduction("Sentencas", "DecVar Sentencas");
            DecVar();
            Sentencas();
        } else if (currentTokenCategory == TokenCategory.ID) {
            printProduction("Sentencas", "ChamadaFuncOuAtrib Sentencas");
            System.out.println(currentToken.toString());
            getNextToken();
            ChamadaFuncOuAtrib();
            Sentencas();
        } else if (currentTokenCategory.getNumericCode() > 36 && currentTokenCategory.getNumericCode() < 46
                && currentTokenCategory.getNumericCode() != 42) {
            printProduction("Sentencas", "Cmd Sentencas");
            Cmd();
            Sentencas();
        } else {
            printProduction("Sentencas", "??");
        }
    }

    public void Cmd() {
        if (currentTokenCategory == TokenCategory.PR_INPUT) {
            printProduction("Cmd", "input '(' LId ')' ';'");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenCategory == TokenCategory.ABRE_PAR) {
                System.out.println(currentToken.toString());
                getNextToken();
                LId();

                if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    if (currentTokenCategory == TokenCategory.TERMINAL) {
                        System.out.println(currentToken.toString());
                        getNextToken();
                    } else {
                        tokenExpected("';'");
                    }
                } else {
                    tokenExpected("')'");
                }
            } else {
                tokenExpected("'('");
            }

        } else if (currentTokenCategory == TokenCategory.PR_WRITE) {
            printProduction("Cmd", "'write' '(' ParamsChamada ')' ';'");
            System.out.println(currentToken.toString());

            getNextToken();

            if (currentTokenCategory == TokenCategory.ABRE_PAR) {
                System.out.println(currentToken.toString());

                getNextToken();

                ParamsChamada();

                if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    if (currentTokenCategory == TokenCategory.TERMINAL) {
                        System.out.println(currentToken.toString());
                        getNextToken();
                    } else {
                        tokenExpected("';'");
                    }
                }
            }

        } else if (currentTokenCategory == TokenCategory.PR_WRITELN) {
            printProduction("Cmd", "'writeln' '(' ParamsChamada ')' ';'");
            System.out.println(currentToken.toString());

            getNextToken();

            if (currentTokenCategory == TokenCategory.ABRE_PAR) {
                System.out.println(currentToken.toString());

                getNextToken();

                ParamsChamada();

                if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                    System.out.println(currentToken.toString());

                    getNextToken();

                    if (currentTokenCategory == TokenCategory.TERMINAL) {
                        System.out.println(currentToken.toString());
                        getNextToken();
                    } else {
                        tokenExpected("';'");
                    }
                }
            }
        } else if (currentTokenCategory == TokenCategory.PR_IF) {
            printProduction("Cmd", "'if' '(' Ebool ')' Bloco Else");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenCategory == TokenCategory.ABRE_PAR) {
                System.out.println(currentToken.toString());
                getNextToken();

                Ebool();

                if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                    System.out.println(currentToken.toString());
                    getNextToken();

                    Bloco();
                    Else();
                } else {
                    tokenExpected("')'");
                }
            } else {
                tokenExpected("'('");
            }
        } else if (currentTokenCategory == TokenCategory.PR_WHILE) {
            printProduction("Cmd", "'while' '(' Ebool ')' Bloco");
            System.out.println(currentToken.toString());
            getNextToken();
            if (currentTokenCategory == TokenCategory.ABRE_PAR) {
                System.out.println(currentToken.toString());
                getNextToken();
                Ebool();

                if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                    Bloco();
                } else {
                    tokenExpected("')'");
                }
            } else {
                tokenExpected("'('");
            }
        } else if (currentTokenCategory == TokenCategory.PR_FOR) {
            printProduction("Cmd", "'for' '(' Int ',' Int ',' Int Incr ')' Bloco");

            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenCategory == TokenCategory.ABRE_PAR) {
                System.out.println(currentToken.toString());

                getNextToken();
                Int();

                if (currentTokenCategory == TokenCategory.SEP) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                    Int();

                    if (currentTokenCategory == TokenCategory.SEP) {
                        System.out.println(currentToken.toString());
                        getNextToken();
                        Int();

                        if (currentTokenCategory == TokenCategory.SEP) {
                            System.out.println(currentToken.toString());

                            getNextToken();

                            Incr();

                            if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                                System.out.println(currentToken.toString());
                                getNextToken();
                                Bloco();
                            } else {
                                tokenExpected("')'");
                            }
                        }

                    } else {
                        tokenExpected("','");
                    }
                } else {
                    tokenExpected("','");
                }
            } else {
                tokenExpected("'('");
            }
        } else if (currentTokenCategory == TokenCategory.PR_RETURN) {
            printProduction("Sentencas", "'return' Return ';' Sentencas();");
            System.out.println(currentToken.toString());
            getNextToken();
            Return();

            if (currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
                Sentencas();
            } else {
                tokenExpected("';'");
            }
        } else if (currentTokenCategory == TokenCategory.PR_BREAK) {
            printProduction("Cmd", "'break' ';'");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("';'");
            }
        }
    }

    public void Return() {
        if (currentTokenCategory == TokenCategory.PR_RETURN) {
            printProduction("Return", "'return' Ret ';'");
            System.out.println(currentToken.toString());
            getNextToken();
            Ret();

            if (currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("';'");
            }
        }
    }

    public void Ret() {
        if (checkEconcFirst(currentTokenCategory)) {
            printProduction("Ret", "Econc");
            Econc();
        } else {
            printProduction("Ret", "??");
        }
    }

    public void Int() {
        if (currentTokenCategory == TokenCategory.ID) {
            printProduction("Int", "'id'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.TIPO_INT) {
            printProduction("Int", "'int' 'id'");
            System.out.println(currentToken.toString());

            getNextToken();

            if (currentTokenCategory == TokenCategory.ID) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("'id'");
            }
        } else if (currentTokenCategory == TokenCategory.CONST_INT) {
            printProduction("Int", "'CONST_INT'");

            System.out.println(currentToken.toString());
            getNextToken();
        }
    }

    public void Incr() {
        if (currentTokenCategory == TokenCategory.SEP) {
            printProduction("Incr", "',' Int");
            System.out.println(currentToken.toString());
            getNextToken();

            Int();
        } else {
            printProduction("Incr", "??");
            getNextToken();
        }
    }

    public void LId() {
        printProduction("LId", "Id LIdX");
        Id();
        LIdX();
    }

    public void Else() {
        if (currentTokenCategory == TokenCategory.PR_ELSE) {
            System.out.println(currentToken.toString());
            getNextToken();
            Bloco();
        } else {
            printProduction("Else", "??");
        }
    }

    public void Id() {
        printProduction("Id", "'id' ArrId");
        System.out.println(currentToken.toString());
        getNextToken();
        ArrId();
    }

    public void LIdX() {
        if (currentTokenCategory == TokenCategory.SEP) {
            printProduction("LIdX", "',' LId");
            System.out.println(currentToken.toString());
            getNextToken();
            LId();
        } else {
            printProduction("LIdX", "??");
        }
    }

    public void Arr() {
        if (currentTokenCategory == TokenCategory.ABRE_COL) {
            printProduction("Arr", "'[' ']'");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenCategory == TokenCategory.FECHA_COL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("']'");
            }
        } else {
            printProduction("Arr", "??");
        }
    }

    public void ArrId() {
        if (currentTokenCategory == TokenCategory.ABRE_COL) {
            printProduction("ArrId", "Earit");
            System.out.println(currentToken.toString());
            getNextToken();
            Earit();
            if (currentTokenCategory == TokenCategory.FECHA_COL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("']'");
            }
        } else {
            printProduction("ArrId", "??");
        }
    }

    public void Earit() {
        printProduction("Earit", "Tarit EaritX");
        Tarit();
        EaritX();
    }

    public void Tarit() {
        printProduction("Tarit", "Parit TaritX");
        Parit();
        TaritX();
    }

    public void TaritX() {
        if (currentTokenCategory == TokenCategory.OP_MULT || currentTokenCategory == TokenCategory.OP_DIV) {
            printProduction("TaritX", "OpMult Parit TaritX");
            OpMult();
            Parit();
            TaritX();
        } else {
            printProduction("TaritX", "??");
        }
    }

    public void OpMult() {
        if (currentTokenCategory == TokenCategory.OP_MULT) {
            printProduction("OpMult", "OP_MULT");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.OP_DIV) {
            printProduction("OpMult", "OP_DIV");
            System.out.println(currentToken.toString());
            getNextToken();
        }
    }

    public void Parit() {
        printProduction("Parit", "Farit ParitX");
        Farit();
        ParitX();
    }

    public void ParitX() {
        if (currentTokenCategory == TokenCategory.OP_POT || currentTokenCategory == TokenCategory.OP_MOD) {
            printProduction("ParitX", "OpPot Parit");
            OpPot();
            Parit();
        } else {
            printProduction("ParitX", "??");
        }
    }

    public void OpPot() {
        if (currentTokenCategory == TokenCategory.OP_POT) {
            printProduction("OpPot", "'**'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.OP_MOD) {
            printProduction("OpPot", "'%'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
    }

    public void Farit() {
        if (currentTokenCategory == TokenCategory.OP_ADD || currentTokenCategory == TokenCategory.OP_SUB) {
            printProduction("Farit", "OpArit FaritX");
            OpArit();
            FaritX();
        } else {
            printProduction("Farit", "FaritX");
            FaritX();
        }
    }

    public void FaritX() {

        if (currentTokenCategory == TokenCategory.ID) {
            printProduction("FaritX", "IdOuFunc");

            IdOuFunc();
        } else if ((currentTokenCategory.getNumericCode() >= 30 && currentTokenCategory.getNumericCode() <= 34)) {
            printProduction("FaritX", "CteLiteral");

            CteLiteral();

        } else if (currentTokenCategory == TokenCategory.ABRE_PAR) {
            printProduction("FaritX", "'(' Econc ')'");
            System.out.println(currentToken.toString());
            getNextToken();
            Econc();

            if (currentTokenCategory == TokenCategory.FECHA_CHAVE) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("')'");
            }
        } else if (currentTokenCategory == TokenCategory.OP_SIZE) {
            printProduction("FaritX", "'size' 'id'");
            System.out.println(currentToken.toString());
            getNextToken();

            if (currentTokenCategory == TokenCategory.ID) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("'id'");
            }
        }
    }

    public void Econc() {
        printProduction("Econc", "Ebool EconcX");
        Ebool();
        EconcX();
    }

    public void EconcX() {
        if (currentTokenCategory == TokenCategory.OP_CONCAT) {
            printProduction("EconcX", "'OP_CONCAT' Ebool EconcX");
            System.out.println(currentToken.toString());
            getNextToken();
            Ebool();
            EconcX();
        } else {
            printProduction("EconcX", "??");
        }
    }

    public void Ebool() {
        printProduction("Ebool", "Tbool EboolX");
        Tbool();
        EboolX();
    }

    public void EboolX() {
        if (currentTokenCategory == TokenCategory.OP_OR) {
            printProduction("EboolX", "'OP_OR' Tbool EboolX");
            System.out.println(currentToken.toString());
            getNextToken();
            Tbool();
            EboolX();
        } else {
            printProduction("EboolX", "??");
        }
    }

    public void Tbool() {
        printProduction("Tbool", "Fbool TboolX");
        Fbool();
        TboolX();
    }

    public void TboolX() {
        if (currentTokenCategory == TokenCategory.OP_AND) {
            printProduction("TboolX", "'OP_AND' Fbool TboolX");
            System.out.println(currentToken.toString());
            getNextToken();
            Fbool();
            TboolX();
        } else {
            printProduction("TboolX", "??");
        }
    }

    public void Fbool() {
        if (currentTokenCategory == TokenCategory.OP_NOT) {
            printProduction("Fbool", "'OP_NOT' Trelac");
            System.out.println(currentToken.toString());
            getNextToken();
            Trelac();
        } else {
            printProduction("Fbool", "Trelac");
            Trelac();
        }
    }

    public void Trelac() {
        printProduction("Trleac", "Earit TrelacX");
        Earit();
        TrelacX();
    }

    public void TrelacX() {
        if (currentTokenCategory.getNumericCode() >= 11 && currentTokenCategory.getNumericCode() <= 16) {
            printProduction("TrelacX", "OpRelac Earit TrelacX");
            OpRelac();
            Earit();
            TrelacX();
        } else {
            printProduction("TrelacX", "??");
        }
    }

    public void OpRelac() {
        if (currentTokenCategory == TokenCategory.OP_MAIOR) {
            printProduction("OpRelac", "'OP_MAIOR'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.OP_MAIOR_IG) {
            printProduction("OpRelac", "'OP_MAIOR_IG'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.OP_MENOR) {
            printProduction("OpRelac", "'OP_MENOR'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.OP_MENOR_IG) {
            printProduction("OpRelac", "'OP_MENOR_IG'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.OP_IGUAL) {
            printProduction("OpRelac", "'OP_IGUAL'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.OP_N_IGUAL) {
            printProduction("OpRelac", "'OP_N_IGUAL'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else {
            tokenExpected("'OP_MAIOR', 'OP_MAIOR_IG', 'OP_MENOR', 'OP_MENOR_IG', 'OP_IGUAL', 'OP_N_IGUAL'");
        }
    }

    public void CteLiteral() {
        if (currentTokenCategory == TokenCategory.CONST_INT) {
            printProduction("CteLiteral", "'CONST_INT'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.CONST_FLOAT) {
            printProduction("CteLiteral", "'CONST_FLOAT'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.CONST_BOOL) {
            printProduction("CteLiteral", "'CONST_BOOL'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.CONST_CHAR) {
            printProduction("CteLiteral", "'CONST_CHAR'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else if (currentTokenCategory == TokenCategory.CONST_STRING) {
            printProduction("CteLiteral", "'CONST_STRING'");
            System.out.println(currentToken.toString());
            getNextToken();
        } else {
            tokenExpected("'CONST_INT', 'CONST_FLOAT', 'CONST_BOOL', 'CONST_CHAR', 'CONST_STRING'");
        }
    }

    public void IdOuFunc() {
        if (currentTokenCategory == TokenCategory.ID) {
            printProduction("IdOuFunc", "'id' ChamadaFuncOrArr");
            System.out.println(currentToken.toString());
            getNextToken();
            ChamadaFuncOrArr();
        }
    }

    public void ChamadaFuncOuAtrib() {
        if (currentTokenCategory == TokenCategory.ABRE_PAR) {
            printProduction("ChamadaFuncOrAtrib", "ChamadaFunc");
            System.out.println(currentToken.toString());
            ChamadaFunc();
        } else if (currentTokenCategory == TokenCategory.OP_ATR || currentTokenCategory == TokenCategory.ABRE_COL) {
            printProduction("ChamadaFuncOrAtrib", "ExpAtrib");
            ExpAtrib();
        }
    }

    public void ExpAtrib() {
        printProduction("ExpAtrib", "ArrId Atrib");
        ArrId();
        Atrib();
        if (currentTokenCategory == TokenCategory.TERMINAL) {
            System.out.println(currentToken.toString());
            getNextToken();
        } else {
            tokenExpected("';'");
        }
    }

    public void ChamadaFunc() {
        if (currentTokenCategory == TokenCategory.ABRE_PAR) {
            printProduction("ChamadaFunc", "'(' ParamsChamada ')' ';'");
            System.out.println(currentToken.toString());
            getNextToken();
            ParamsChamada();

            if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                System.out.println(currentToken.toString());
                getNextToken();
                if (currentTokenCategory == TokenCategory.TERMINAL) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                } else {
                    tokenExpected("';'");
                }
            } else {
                tokenExpected("')'");
            }
        } else {
            tokenExpected("'('");
        }
    }

    public void ChamadaFuncOrArr() {
        if (currentTokenCategory == TokenCategory.ABRE_PAR) {
            printProduction("ChamadaFuncOrArr", "'(' ParamsChamada ')'");
            System.out.println(currentToken.toString());
            getNextToken();
            ParamsChamada();

            if (currentTokenCategory == TokenCategory.FECHA_PAR) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("')'");
            }
        } else if (currentTokenCategory == TokenCategory.ABRE_COL) {
            printProduction("ChamadaFuncOrArr", "'[' Earit ']'");
            System.out.println(currentToken.toString());
            getNextToken();
            Earit();
            if (currentTokenCategory == TokenCategory.FECHA_COL) {
                System.out.println(currentToken.toString());
                getNextToken();
            } else {
                tokenExpected("']'");
            }
        } else {
            printProduction("ChamadaFunOrArr", "??");
        }
    }

    public void ParamsChamada() {
        if (checkEconcFirst(currentTokenCategory)) {
            printProduction("ParamsChamada", "Econc ParamsChamadaX");
            Econc();
            ParamsChamadaX();
        } else {
            printProduction("ParamsChamada", "??");
        }
    }

    public void ParamsChamadaX() {
        if (currentTokenCategory == TokenCategory.SEP) {
            printProduction("ParamsChamadaX", "',' ParamsChamada");
            System.out.println(currentToken.toString());
            getNextToken();

            ParamsChamada();
        } else {
            printProduction("ParamsChamadaX", "??");
        }
    }

    public void OpArit() {
        if (currentTokenCategory == TokenCategory.OP_ADD) {
            printProduction("OpArit", "'OP_ADD'");
            System.out.println(currentToken.toString());
        } else if (currentTokenCategory == TokenCategory.OP_SUB) {
            printProduction("OpArit", "'OP_SUB'");
            System.out.println(currentToken.toString());
        }

        getNextToken();
    }

    public void EaritX() {
        if (currentTokenCategory == TokenCategory.OP_ADD || currentTokenCategory == TokenCategory.OP_SUB) {
            printProduction("EaritX", "OpArit Tarit EaritX");
            OpArit();
            Tarit();
            EaritX();
        } else {
            printProduction("EaritX", "??");
        }
    }

    public void Tipo() {
        if (currentTokenCategory == TokenCategory.TIPO_BOOL) {
            printProduction("Tipo", "'bool'");
            System.out.println(currentToken.toString());
        } else if (currentTokenCategory == TokenCategory.TIPO_INT) {
            printProduction("Tipo", "'int'");
            System.out.println(currentToken.toString());
        } else if (currentTokenCategory == TokenCategory.TIPO_FLOAT) {
            printProduction("Tipo", "'float'");
            System.out.println(currentToken.toString());
        } else if (currentTokenCategory == TokenCategory.TIPO_CHAR) {
            printProduction("Tipo", "'char'");
            System.out.println(currentToken.toString());
        } else if (currentTokenCategory == TokenCategory.TIPO_STRING) {
            printProduction("Tipo", "'string'");
            System.out.println(currentToken.toString());
        } else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }

        getNextToken();
    }

    public void printProduction(String leftProduction, String rightProduction) {
        String format = "%10s%s = %s";
        System.out.println(String.format(format, "", leftProduction, rightProduction));
    }

    private boolean typeCheck(TokenCategory currentTokenCategory) {
        if (currentTokenCategory == TokenCategory.TIPO_BOOL || currentTokenCategory == TokenCategory.TIPO_INT
                || currentTokenCategory == TokenCategory.TIPO_CHAR || currentTokenCategory == TokenCategory.TIPO_FLOAT
                || currentTokenCategory == TokenCategory.TIPO_STRING) {
            return true;
        }
        return false;
    }

    private boolean checkEconcFirst(TokenCategory currentTokenCategory) {
        if ((checkLiteralCte(currentTokenCategory)) || (currentTokenCategory == TokenCategory.ID)
                || (currentTokenCategory == TokenCategory.OP_ADD) || (currentTokenCategory == TokenCategory.OP_SUB)
                || (currentTokenCategory == TokenCategory.ABRE_PAR)) {
            return true;
        }

        return false;
    }

    private boolean checkLiteralCte(TokenCategory currentTokenCategory) {
        return currentTokenCategory.getNumericCode() >= 30 && currentTokenCategory.getNumericCode() <= 34;
    }

    public void tokenExpected(String tokens) {
        currentToken.toString();
        System.out.println();
        String tokenPosition = String.format("[%04d, %04d]", currentToken.getTokenRow(), currentToken.getTokenColumn());
        System.err.println("Error: Expected " + tokens + " at " + tokenPosition);
        System.exit(1);
    }

}