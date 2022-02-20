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
        if(typeCheck(currentTokenCategory) || currentTokenCategory == TokenCategory.ABRE_CHAVE ||
        currentTokenCategory == TokenCategory.PR_FUNCTION){
            //Declaração simples 
            printProduction("S", "Decl S");
            Decl();
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
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'array', 'function', 'id', 'eof'");
        }
    }

    public void Decl(){
        if(typeCheck(currentTokenCategory)){
            printProduction("Decl", "DeclVar");
            DeclVar();

        }else if(currentTokenCategory == TokenCategory.ABRE_COL){
            printProduction("Decl", "DeclArr");
            DeclArr();
        }else if(currentTokenCategory == TokenCategory.PR_FUNCTION){
            printProduction("Decl", "DeclFunc");
            DeclFunc();
        }else{
            tokenExpected("'int', 'float', 'bool', 'char', 'string', 'array', 'function'");
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
        Ebl();
    }

    public void Tb(){
        printProduction("Tb", "Fb Tbl");
        Fb();
        Tbl();
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
        if (currentTokenCategory == TokenCategory.CONST_BOOL || currentTokenCategory == TokenCategory.CONST_INT 
        || currentTokenCategory == TokenCategory.CONST_CHAR || currentTokenCategory == TokenCategory.CONST_FLOAT 
        || currentTokenCategory == TokenCategory.CONST_STRING) {
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

    public void DeclVar(){
        if(typeCheck(currentTokenCategory)) {
            printProduction("DeclVar", "Type VarOp ';'");
            Type();
            VarOp();

            if(currentTokenCategory == TokenCategory.TERMINAL) {
                System.out.println(currentToken.toString());
                getNextToken();
            }
            else {
                tokenExpected("';'");
            }
        }
        else {
            tokenExpected("'int', 'float', 'bool', 'char', 'string'");
        }
    }

    public void VarOp(){

    }

    public void DeclArr(){
        if(currentTokenCategory == TokenCategory.ABRE_COL) {

            getNextToken();

            if(currentTokenCategory == TokenCategory.ID || currentTokenCategory == TokenCategory.CONST_INT){

                getNextToken();

                if(currentTokenCategory == TokenCategory.FECHA_COL){
                    printProduction("DeclArr", " Type ArrOp [id] ';'");
                    System.out.println(currentToken.toString());
                    getNextToken();
        
                    Type();

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

    public void DeclFunc(){
        
    }

    public void Type() {
        if (currentTokenCategory == TokenCategory.TIPO_BOOL) {
            printProduction("Type", "'bool'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_INT) {
            printProduction("Type", "'int'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_FLOAT) {
            printProduction("Type", "'float'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_CHAR) {
            printProduction("Type", "'char'");
            System.out.println(currentToken.toString());
            getNextToken();
        }
        else if (currentTokenCategory == TokenCategory.TIPO_STRING) {
            printProduction("Type", "'string'");
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

    public boolean typeCheck(TokenCategory currentTokenClass) {
        if (currentTokenClass == TokenCategory.TIPO_BOOL || currentTokenClass == TokenCategory.TIPO_INT 
        || currentTokenClass == TokenCategory.TIPO_CHAR || currentTokenClass == TokenCategory.TIPO_FLOAT 
        || currentTokenClass == TokenCategory.TIPO_STRING) {
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
