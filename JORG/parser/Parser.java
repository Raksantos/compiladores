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

    public Parser (LexicalScanner lexicalScanner){
        this.lexicalScanner = lexicalScanner;
    }
    
    public void readNextLine(){
        if(scanner.hasNextLine()){
            lexicalScanner.nextRow();
            
            line = scanner.nextLine();
            lineRow = "%04d  ";
            
            System.out.printf(String.format(lineRow, lexicalScanner.getRow()));
            System.out.println(line);
            
            line += "\n";

            lexicalScanner.restartPos();
            getNextToken();

        }else{
            lineRow = "%04d  ";
            
            System.out.printf(String.format(lineRow, lexicalScanner.getRow()));
            currentToken = new Token("EOF", TokenCategory.EOF_TOKEN, lexicalScanner.getRow(), lexicalScanner.getColumn());

            currentTokenCategory = currentToken.getTokenCategory();
        }
    }

    public void getNextToken(){
        lexicalScanner.nextColumn(); // updating the current column

        if(lexicalScanner.isEOF(line) || line.charAt(lexicalScanner.getPosition()) != '\n' ) {
            currentToken = lexicalScanner.nextToken(line); 
            if(currentToken == null) { 
                readNextLine(); 
            }
            
            currentTokenCategory = currentToken.getTokenCategory();
        }else {
            readNextLine();
        }
    }

    public void parser(String filePath){
        try{
            File file = new File(filePath);
            scanner = new Scanner(file);
        } catch(FileNotFoundException e){
            e.printStackTrace();
        }

        line = scanner.nextLine();
        lineRow = "%04d  ";
        System.out.printf(String.format(lineRow, lexicalScanner.getRow()));
        System.out.println(line);

        line += '\n';
        lexicalScanner.restartPos();

        getNextToken();
        
        S();

        scanner.close();
    }

    //Inicializa leitura do código e verifica se é uma variável, array ou função
    private void S(){
        if(typeCheck(currentTokenCategory) || constCheck(currentTokenCategory) || currentTokenCategory == TokenCategory.ABRE_CHAVE ||
        currentTokenCategory == TokenCategory.PR_FUNCTION){
            //Declaração simples 
            printProduction("S", "Decl S");
            Dec();
            S();
        }else if(currentTokenCategory == TokenCategory.ID){
            //Declaração com atribuição
            printProduction("S", "Atrib S");
            Atrib();
            S();
        }else if(currentTokenCategory == TokenCategory.EOF_TOKEN){
            //Fim de arquivo
            printProduction("S", "'eof'");
            System.out.println(currentToken.toString());
        }else{
            //Tipagem ou declaração esperada
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'const', 'id', 'eof'");
        }
    }

    public void Dec(){
        if(typeCheck(currentTokenCategory) || constCheck(currentTokenCategory)){
            printProduction("Dec", "DecVar");
            DecVar();
        }else if(currentTokenCategory == TokenCategory.PR_FUNCTION){
            printProduction("Dec", "DecFunc");
            DecFunc();
        }else{
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'const', 'function'");
        }
    }

    public void Atrib(){
        if(currentTokenCategory == TokenCategory.ID){
            printProduction("Atrib", "'id' IndiceOp '=' Expr ';'");
            System.out.println(currentToken.toString());
            getNextToken();
            IndiceOp();

            if(currentTokenCategory == TokenCategory.OP_ATR) {
                System.out.println(currentToken.toString());
                getNextToken();
                Expr();

                if(currentTokenCategory == TokenCategory.TERMINAL) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                }
                else {
                    tokenExpected("';'");
                }
            }
            else {
                tokenExpected("'='");
            }
        }
        else {
            tokenExpected("'id'");
        }
    }

    public void Expr() {
        if(faCheck(currentTokenCategory) || currentTokenCategory == TokenCategory.OP_NOT) {
            printProduction("Expr", "Eb Exprl");
            Eb();
            Exprl();
        }else { // checking here to avoid unnecessary checks later
            tokenExpected("'!', '(', '-', 'id', 'constInt', 'constFloat', 'constChar', 'constString', 'constBool'");
        }
    }

    public boolean faCheck(TokenCategory currentTokenCategory) {
        if(constCheck(currentTokenCategory) || currentTokenCategory == TokenCategory.ABRE_PAR 
        || currentTokenCategory == TokenCategory.OP_SUB || currentTokenCategory == TokenCategory.ID) {
            return true;
        }
        return false;
    }
    
    public void Eb(){
        printProduction("Eb", "Tb Ebl");
        Tb();
        //Ebl();
    }

    public void Tb(){
        printProduction("Tb", "Fb Tbl");
        //Fb();
        //Tbl();
    }

    public void Exprl(){
        if(currentTokenCategory == TokenCategory.OP_CONCAT) {
            printProduction("Exprl", "'optConcat' Eb Exprl");
            System.out.println(currentToken.toString());
            getNextToken();
            Eb();
            Exprl();
        }
        else {
            printProduction("Exprl", "'épsilon'");
        }
    }

    public boolean constCheck(TokenCategory currentTokenCategory) {

        if(currentTokenCategory == TokenCategory.PR_CONST){
                return true;
        }
        return false;
    }

    public void IndiceOp(){
        if(currentTokenCategory == TokenCategory.ABRE_COL) {
            printProduction("IndiceOp", "'[' Expr ']'");
            System.out.println(currentToken.toString());
            getNextToken();
            Expr();

            if(currentTokenCategory == TokenCategory.FECHA_COL) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("']'");
            }
        }
        else {
            printProduction("IndiceOp", "'épsilon'");
        }
    }

    public void DecVar(){
        if(typeCheck(currentTokenCategory)) {
            printProduction("DecVar", "Tipo ListId ';'");
            Tipo();
            ListId();

            if(currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("';'");
            }
        }else if(constCheck(currentTokenCategory)){
            printProduction("DecVar", "'const' Tipo ListId ';'");

            getNextToken();
            Tipo();
            ListId();

            if(currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("';'");
            }
        }else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }
    }

    public void ListId(){
        if(currentTokenCategory == TokenCategory.ID){
            printProduction("ListId", "'id' CommaOp");
            System.out.println(currentToken.toString());

            getNextToken();
            
            if(currentTokenCategory == TokenCategory.SEP) {
                printProduction("CommaOp", "',' VarOp");
                System.out.println(currentToken.toString());
                getNextToken();
                ListId();
            }
            else {
                printProduction("CommaOp", "'épsilon'");
            }
        }else {
            tokenExpected("'id'");
        }
    } 

    public void DecArr(){
        if(currentTokenCategory == TokenCategory.ABRE_COL) {

            getNextToken();

            if(currentTokenCategory == TokenCategory.ID || currentTokenCategory == TokenCategory.CONST_INT){

                getNextToken();

                if(currentTokenCategory == TokenCategory.FECHA_COL){
                    printProduction("DecArr", " Tipo ArrOp [id] ';'");
                    System.out.println(currentToken.toString());
                    getNextToken();
        
                    Tipo();

                    if(currentTokenCategory == TokenCategory.TERMINAL) {
                        System.out.println(currentToken.toString());
                        getNextToken();
                    }else {
                        tokenExpected("';'");
                    }
                }else{
                     tokenExpected("']'");
                    }
                }else{
                    tokenExpected("'id' ou 'const_int'");
                }
            }else {
                tokenExpected("'['");
            }
        }

    public void DecFunc(){
        if(currentTokenCategory== TokenCategory.PR_FUNCTION) {
            printProduction("DecFunc", "'function' TipoFunc NomeFunc '(' ParamsDec ')' Bloco");
            System.out.println(currentToken.toString());
            getNextToken();

            TipoFunc();
            NomeFunc();

            if(currentTokenCategory == TokenCategory.ABRE_PAR) {
                printProduction("ParamsDec", "'(' ParamOpOrNoParam ')'");
                System.out.println(currentToken.toString());
                getNextToken();
    
                ParamOpOrNoParam();
    
                if(currentTokenCategory == TokenCategory.FECHA_PAR) {
                    System.out.println(currentToken.toString());
                    getNextToken();
                }
                else {
                    tokenExpected("')'");
                }
            }
            else {
                tokenExpected("'('");
            }

            Bloco();
        }
        else {
            tokenExpected("'function'");
        }
    }

    public void TipoFunc(){
        if(typeCheck(currentTokenCategory)) {
            printProduction("TipoFunc", "Tipo");
            Tipo();
        }
        else if(currentTokenCategory == TokenCategory.TIPO_VOID) {
            printProduction("TypeOrVoid", "'void'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'void'");
        }
    }

    public void NomeFunc(){
        if(currentTokenCategory == TokenCategory.PR_MAIN) {
            printProduction("NomeFunc", "'main'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if(currentTokenCategory == TokenCategory.ID) {
            printProduction("NomeFunc", "'id'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'main', 'id'");
        }
    }

    public void ParamOpOrNoParam() {
        if(typeCheck(currentTokenCategory)) {
            printProduction("ParamOpOrNoParam", "ParamOp");
            ParamOp();
        }
        else {
            printProduction("ParamOrNoParam", "'épsilon'");
        }
    }

    public void ParamOp() {
        if(typeCheck(currentTokenCategory)) {
            printProduction("ParamOp", "DeclVarOp");
            DeclVarOp();
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'array'");
        }
    }

    public void DeclVarOp() {
        if(typeCheck(currentTokenCategory)) {
            printProduction("DeclVarOp", "Type 'id' ParamCommaOp");
            Tipo();

            if(currentTokenCategory == TokenCategory.ID) {
                System.out.println(currentToken.toString());
                getNextToken();

                if(currentTokenCategory == TokenCategory.SEP) {
                    printProduction("ParamCommaOp", "',' ParamOp");
                    System.out.println(currentToken.toString());
                    getNextToken();
                    ParamOp();
                }
                else {
                    printProduction("ParamCommaOp", "'épsilon'");
                }
            }
            else {
                tokenExpected("'id'");
            }
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }
    }

    public void Bloco() {
        if(currentTokenCategory == TokenCategory.ABRE_CHAVE) {
            printProduction("Bloco", "'{' Sentences '}'");
            System.out.println(currentToken.toString());
            getNextToken();
            Sentences();

            if(currentTokenCategory == TokenCategory.FECHA_CHAVE) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("'}'");
            }
        }
        else {
            tokenExpected("'{'");
        }
    }

    public void Sentences(){
        
    }

    public void Tipo() {
        if (currentTokenCategory == TokenCategory.TIPO_BOOL) {
            printProduction("Tipo", "'bool'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_INT) {
            printProduction("Tipo", "'int'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_FLOAT) {
            printProduction("Tipo", "'float'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_CHAR) {
            printProduction("Tipo", "'char'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_STRING) {
            printProduction("Tipo", "'string'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }
    }

    public void printProduction(String leftProduction, String rightProduction){
        String format = "%10s%s = %s";
        System.out.println(String.format(format, "", leftProduction, rightProduction));
    }

    public boolean typeCheck(TokenCategory currentTokenCategory) {
        if (currentTokenCategory == TokenCategory.TIPO_BOOL || currentTokenCategory == TokenCategory.TIPO_INT 
        || currentTokenCategory == TokenCategory.TIPO_CHAR || currentTokenCategory == TokenCategory.TIPO_FLOAT 
        || currentTokenCategory == TokenCategory.TIPO_STRING) {
            return true;
        }
        return false;
    }

    public void tokenExpected(String tokens) {
        currentToken.toString();
        System.out.println();
        String tokenPosition = String.format("[%04d, %04d]", currentToken.getTokenRow(), currentToken.getTokenColumn());
        System.err.println("Error: Expected " + tokens + " at " + tokenPosition);
        System.exit(1);
    }

}
