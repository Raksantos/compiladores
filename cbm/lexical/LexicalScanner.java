package cbm.lexical;

import java.util.Hashtable;

public class LexicalScanner {
    Hashtable<String, TokenClass> tokenTable = new Hashtable<String, TokenClass>();

    private int position = 0;
    private int row = 1;
    private int column = 0;

    public LexicalScanner(){
        fillTokenClasses();
    }

    public Token nextToken(String line){
        char current;
        int state = 0;
        String currentTokenValue = "";

        try{
            while(true){
                if(isEOF(line)) return null;
                current = getCurrentChar(line);
                switch(state){
                    case 0:
                        if(isChar(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 1;
                        }
                        else if (isDigit(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 2;
                        }
                        else if(isArithmeticOperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 5;
                        }
                        else if(isEqualOrAssignment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 6;
                        }
                        else if(isRelationalOperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 7;
                        }
                        else if(isNOTOperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 9;
                        }
                        else if(isANDOperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 10;
                        }
                        else if(isOROperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 11;
                        }
                        else if(isDelimiter(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 13;
                        }
                        else if(isComment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 14;
                        }
                        else if(isSpace(current) || isNewLine(current)){
                            nextChar();
                            nextColumn();
                            state = 0;
                        }
                        else {
                            currentTokenValue = currentTokenValue + current;
                            nextChar();
                            return new Token(currentTokenValue, TokenClass.BAD_TOKEN, row, column);
                        }
                        break;
                    case 1:
                        if(isChar(current) || isDigit(current) || isUnderline(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 1;
                        }else{
                            return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                        }
                        break;
                    case 2:
                        if(isDigit(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 2;
                        }
                        else if(isDot(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 3;
                        }
                        else{
                            return new Token(currentTokenValue, TokenClass.CONST_INT, row, column);
                        }
                        break;
                    case 3:
                        if(isDigit(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 4;
                        }
                        else{
                            return new Token(currentTokenValue, TokenClass.BAD_TOKEN, row, column);
                        }
                        break;
                    case 4:
                        if(isDigit(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 4;
                        }
                        else{
                            return new Token(currentTokenValue, TokenClass.CONST_FLOAT, row, column);
                        }
                        break;
                    case 5:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 6:
                        if(isEqualOrAssignment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 8;
                        }
                        else{
                            return new Token(currentTokenValue, TokenClass.OP_ATR, row, column);
                        }
                        break;
                    case 7:
                        if(isEqualOrAssignment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 8;
                        }
                        else{
                            return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                        }
                        break;
                    case 8:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 9:
                        if(isEqualOrAssignment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 8;
                        }
                        else{
                            return new Token(currentTokenValue, TokenClass.OP_NOT, row, column);
                        }
                        break;
                    case 10:
                        if(isANDOperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 12;
                        }
                        else{
                            return new Token(currentTokenValue, TokenClass.BAD_TOKEN, row, column);
                        }
                        break;
                    case 11:
                        if(isOROperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 12;
                        }
                        else{
                            return new Token(currentTokenValue, TokenClass.BAD_TOKEN, row, column);
                        }
                        break;
                    case 12:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 13:
                        return new Token(currentTokenValue, checkTokenClasses(currentTokenValue), row, column);
                    case 14:
                        return null;
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private String getNextTokenValue(String currentTokenValue, char currentChar){
        return currentTokenValue + currentChar;
    }

    private void fillTokenClasses(){
        // Reserved Words
        tokenTable.put("function", TokenClass.PR_FUNCTION);
        tokenTable.put("main", TokenClass.PR_MAIN);
        tokenTable.put("write", TokenClass.PR_WRITE);
        tokenTable.put("procedure", TokenClass.PR_PROCEDURE);
        tokenTable.put("return", TokenClass.PR_RETURN);
        tokenTable.put("print", TokenClass.PR_PRINT);
        tokenTable.put("input", TokenClass.PR_INPUT);
        tokenTable.put("if", TokenClass.PR_IF);
        tokenTable.put("else", TokenClass.PR_ELSE);
        tokenTable.put("for", TokenClass.PR_FOR);
        tokenTable.put("while", TokenClass.PR_WHILE);
        tokenTable.put("int", TokenClass.TIPO_INT);
        tokenTable.put("float", TokenClass.TIPO_FLOAT);
        tokenTable.put("char", TokenClass.TIPO_CHAR);
        tokenTable.put("string", TokenClass.TIPO_STRING);
        tokenTable.put("bool", TokenClass.TIPO_BOOL);
        tokenTable.put("void", TokenClass.TIPO_VOID);

        // Delimiters
        tokenTable.put("(", TokenClass.ABRE_PAR);
        tokenTable.put(")", TokenClass.FECHA_PAR);
        tokenTable.put("{", TokenClass.ABRE_CHAVE);
        tokenTable.put("}", TokenClass.FECHA_CHAVE);
        tokenTable.put("[", TokenClass.ABRE_COL);
        tokenTable.put("]", TokenClass.FECHA_COL);
        tokenTable.put(";", TokenClass.TERMINAL);
        tokenTable.put(",", TokenClass.SEP);
        tokenTable.put("\"", TokenClass.ASPAS);
        tokenTable.put("#", TokenClass.EOF_TOKEN);

        // Constantes literais

        // Operadores
        tokenTable.put("+", TokenClass.OP_ADD);
        tokenTable.put("-", TokenClass.OP_SUB);
        tokenTable.put("*", TokenClass.OP_MULT);
        tokenTable.put("/", TokenClass.OP_DIV);
        tokenTable.put("^", TokenClass.OP_POT);
        tokenTable.put("%", TokenClass.OP_MOD);
        tokenTable.put("!", TokenClass.OP_NOT);
        tokenTable.put("|", TokenClass.OP_OR);
        tokenTable.put("&", TokenClass.OP_AND);
        tokenTable.put(">", TokenClass.OP_MAIOR);
        tokenTable.put("<", TokenClass.OP_MENOR);
        tokenTable.put("==", TokenClass.OP_IGUAL);
        tokenTable.put(">=", TokenClass.OP_MAIOR_IG);
        tokenTable.put("<=", TokenClass.OP_MENOR_IG);
        tokenTable.put("!=", TokenClass.OP_N_IGUAL);
        tokenTable.put("=", TokenClass.OP_ATR);
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private boolean isComment(char c){
        return c == '#';
    }

    private boolean isChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); 
    }

    private boolean isSpace(char c){
        return c == ' ';
    }

    private boolean isNewLine(char c){
        return c == '\n';
    }

    private boolean isUnderline(char c){
        return c == '_';
    }

    public boolean isEOF(String line){
        return this.position == line.length();
    }

    private boolean isDelimiter(char c){
        return (c == ',') || (c == ';') || (c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '{') || (c == '}') || (c == '"');
    }

    private boolean isArithmeticOperator(char c){
        return (c == '+') || (c == '-') || (c == '*') || (c == '/') || (c == '^') || (c == '%');
    }

    private boolean isRelationalOperator(char c){
        return (c == '<') || (c == '>');
    }

    private boolean isNOTOperator(char c){
        return (c == '!');
    }

    private boolean isANDOperator(char c){
        return (c == '&');
    }

    private boolean isOROperator(char c){
        return (c == '|');
    }

    private boolean isDot(char c) {
        return c == '.';
    }

    private boolean isEqualOrAssignment(char c) {
        return c == '=';
    }

    private char getCurrentChar(String line) { 
        return line.charAt(this.position); 
    }

    private void nextChar() { 
        this.position++; 
    }

    // public boolean isSymbol(char c){

    // }

    public int getRow(){
        return this.row;
    }

    public int getColumn(){
        return this.column;
    }

    public void restartPos(){
        this.position = 0;
    }

    public void nextColumn(){
        this.column = this.position;    
    }

    public void nextRow(){
        this.row++;
    }

    private TokenClass checkTokenClasses(String tokenValue){
        if(tokenTable.containsKey(tokenValue)){
            return tokenTable.get(tokenValue);
        }else{
            return TokenClass.ID;
        }
    }

    @Override
    public String toString() {
        return tokenTable.toString();
    }
}
