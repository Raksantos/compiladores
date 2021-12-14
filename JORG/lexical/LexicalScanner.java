package JORG.lexical;

import java.util.Hashtable;

public class LexicalScanner {
    Hashtable<String, TokenCategory> tokenTable = new Hashtable<String, TokenCategory>();

    private int position = 0;
    private int row = 1;
    private int column = 0;

    public LexicalScanner(){
        fillTokenCategories();
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
                        else if(isSimpleQuote(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 15;
                        }
                        else if(isDoubleQuote(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 19;
                        }else if(current == ':'){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 22;
                        }else if(isSpace(current) || isNewLine(current) || isTab(current)){
                            nextChar();
                            nextColumn();
                            state = 0;
                        }
                        else {
                            currentTokenValue = currentTokenValue + current;
                            nextChar();
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }
                        break;
                    case 1:
                        if(isChar(current) || isDigit(current) || isUnderline(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 1;
                        }else{
                            return new Token(currentTokenValue, checkTokenCategories(currentTokenValue), row, column);
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
                            return new Token(currentTokenValue, TokenCategory.CONST_INT, row, column);
                        }
                        break;
                    case 3:
                        if(isDigit(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 4;
                        }
                        else{
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }
                        break;
                    case 4:
                        if(isDigit(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 4;
                        }
                        else{
                            return new Token(currentTokenValue, TokenCategory.CONST_FLOAT, row, column);
                        }
                        break;
                    case 5:
                        return new Token(currentTokenValue, checkTokenCategories(currentTokenValue), row, column);
                    case 6:
                        if(isEqualOrAssignment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 8;
                        }
                        else{
                            return new Token(currentTokenValue, TokenCategory.OP_ATR, row, column);
                        }
                        break;
                    case 7:
                        if(isEqualOrAssignment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 8;
                        }
                        else{
                            return new Token(currentTokenValue, checkTokenCategories(currentTokenValue), row, column);
                        }
                        break;
                    case 8:
                        return new Token(currentTokenValue, checkTokenCategories(currentTokenValue), row, column);
                    case 9:
                        if(isEqualOrAssignment(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 8;
                        }
                        else{
                            return new Token(currentTokenValue, TokenCategory.OP_NOT, row, column);
                        }
                        break;
                    case 10:
                        if(isANDOperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 12;
                        }
                        else{
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }
                        break;
                    case 11:
                        if(isOROperator(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 12;
                        }
                        else{
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }
                        break;
                    case 12:
                        return new Token(currentTokenValue, checkTokenCategories(currentTokenValue), row, column);
                    case 13:
                        return new Token(currentTokenValue, checkTokenCategories(currentTokenValue), row, column);
                    case 14:
                        return null;
                    case 15:
                        if(isChar(current) || isDigit(current) || isSymbol(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 16;
                        }else if(current == '\\'){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 17;
                        }else if(isSimpleQuote(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 18;
                        }else{
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }
                        break;
                    case 16:
                        if(isSimpleQuote(current)){
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 18;
                        }else{
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }
                        break;
                    case 17:
                        if(isNewLine(current)){
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }else{
                            currentTokenValue = getNextTokenValue(currentTokenValue, current);
                            nextChar();
                            state = 16; 
                        }
                        break;
                    case 18:
                        return new Token(currentTokenValue, TokenCategory.CONST_CHAR, row, column);
                    case 19:
                        if(isChar(current) || isDigit(current) || isSymbol(current)) {
                            currentTokenValue = currentTokenValue + current;
                            nextChar();
                            state = 19;
                        }
                        else if(current == '\\'){
                            currentTokenValue = currentTokenValue + current;
                            nextChar();
                            state = 20;
                        }else if(isDoubleQuote(current)){
                            currentTokenValue = currentTokenValue + current;
                            nextChar();
                            state = 21;
                        }else{
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }
                        break;
                    case 20:
                        if(isNewLine(current)){
                            return new Token(currentTokenValue, TokenCategory.BAD_TOKEN, row, column);
                        }else{
                            currentTokenValue = currentTokenValue + current;
                            nextChar();
                            state = 19;
                        }
                        break;
                    case 21:
                        return new Token(currentTokenValue, TokenCategory.CONST_STRING, row, column);
                    case 22:
                        return new Token(currentTokenValue, TokenCategory.OP_CONCAT, row, column);
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

    private void fillTokenCategories(){
        // Reserved Words
        tokenTable.put("function", TokenCategory.PR_FUNCTION);
        tokenTable.put("main", TokenCategory.PR_MAIN);
        tokenTable.put("write", TokenCategory.PR_WRITE);
        tokenTable.put("input", TokenCategory.PR_INPUT);
        tokenTable.put("procedure", TokenCategory.PR_PROCEDURE);
        tokenTable.put("return", TokenCategory.PR_RETURN);
        tokenTable.put("if", TokenCategory.PR_IF);
        tokenTable.put("else", TokenCategory.PR_ELSE);
        tokenTable.put("for", TokenCategory.PR_FOR);
        tokenTable.put("while", TokenCategory.PR_WHILE);
        tokenTable.put("int", TokenCategory.TIPO_INT);
        tokenTable.put("float", TokenCategory.TIPO_FLOAT);
        tokenTable.put("char", TokenCategory.TIPO_CHAR);
        tokenTable.put("string", TokenCategory.TIPO_STRING);
        tokenTable.put("bool", TokenCategory.TIPO_BOOL);
        tokenTable.put("void", TokenCategory.TIPO_VOID);

        // Delimiters
        tokenTable.put("(", TokenCategory.ABRE_PAR);
        tokenTable.put(")", TokenCategory.FECHA_PAR);
        tokenTable.put("{", TokenCategory.ABRE_CHAVE);
        tokenTable.put("}", TokenCategory.FECHA_CHAVE);
        tokenTable.put("[", TokenCategory.ABRE_COL);
        tokenTable.put("]", TokenCategory.FECHA_COL);
        tokenTable.put(";", TokenCategory.TERMINAL);
        tokenTable.put(",", TokenCategory.SEP);
        tokenTable.put("\"", TokenCategory.ASPAS);
        tokenTable.put("", TokenCategory.EOF_TOKEN);

        // Constantes literais

        // Operadores
        tokenTable.put("+", TokenCategory.OP_ADD);
        tokenTable.put("-", TokenCategory.OP_SUB);
        tokenTable.put("*", TokenCategory.OP_MULT);
        tokenTable.put("/", TokenCategory.OP_DIV);
        tokenTable.put("^", TokenCategory.OP_POT);
        tokenTable.put("%", TokenCategory.OP_MOD);
        tokenTable.put("!", TokenCategory.OP_NOT);
        tokenTable.put("|", TokenCategory.OP_OR);
        tokenTable.put("&", TokenCategory.OP_AND);
        tokenTable.put(">", TokenCategory.OP_MAIOR);
        tokenTable.put("<", TokenCategory.OP_MENOR);
        tokenTable.put("==", TokenCategory.OP_IGUAL);
        tokenTable.put(">=", TokenCategory.OP_MAIOR_IG);
        tokenTable.put("<=", TokenCategory.OP_MENOR_IG);
        tokenTable.put("!=", TokenCategory.OP_N_IGUAL);
        tokenTable.put("=", TokenCategory.OP_ATR);
        tokenTable.put(":", TokenCategory.OP_CONCAT);
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

    private boolean isSimpleQuote(char c){
        return c == '\'';
    }

    private boolean isDoubleQuote(char c){
        return c == '\"';
    }

    private boolean isNewLine(char c){
        return c == '\n';
    }

    private boolean isTab(char c){
        return c == '\t';
    }

    private boolean isUnderline(char c){
        return c == '_';
    }

    public boolean isEOF(String line){
        return this.position == line.length();
    }

    private boolean isDelimiter(char c){
        return (c == ',') || (c == ';') || (c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '{') || (c == '}');
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

    public boolean isSymbol(char c){
        return isDelimiter(c) || isArithmeticOperator(c) || isRelationalOperator(c) || isComment(c) || isSpace(c)
                || isANDOperator(c) || isOROperator(c) || isNOTOperator(c) || (c == ':') || (c == '?') || (c == '$')
                || (c == '@') || (c == '~');
    }

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

    private TokenCategory checkTokenCategories(String tokenValue){
        if(tokenTable.containsKey(tokenValue)){
            return tokenTable.get(tokenValue);
        }else{
            return TokenCategory.ID;
        }
    }

    @Override
    public String toString() {
        return tokenTable.toString();
    }
}
