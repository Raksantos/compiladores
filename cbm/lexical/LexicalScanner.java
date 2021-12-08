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
        //Reserved Words
        tokenTable.put("function", TokenClass.PR_FUNCTION);
        tokenTable.put("main", TokenClass.PR_MAIN);
        tokenTable.put("write", TokenClass.PR_WRITE);

        //Delimiters
        tokenTable.put("(", TokenClass.ABRE_PAR);
        tokenTable.put(")", TokenClass.FECHA_PAR);
        tokenTable.put("{", TokenClass.ABRE_CHAVE);
        tokenTable.put("}", TokenClass.FECHA_CHAVE);
        tokenTable.put("\"", TokenClass.FECHA_CHAVE);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'); //
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

    private boolean isNOTOperator(char c) {
        return (c == '!');
    }

    private boolean isANDOperator(char c) {
        return (c == '&');
    }

    private boolean isOROperator(char c) {
        return (c == '|');
    }

    private char getCurrentChar(String line) { 
        return line.charAt(this.position); 
    }

    private void nextChar() { 
        this.position++; 
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

    private boolean isSymbol(char c) {
        return (c == '>') || (c == '<') || (c == '=') || (c == '!') || (c == '&') || (c == '|') || (c == '+')
                || (c == '-') || (c == '^') || (c == '*') || (c == '/') || (c == '%') || (c == ' ') || (c == ';') || (c == '.') || (c == ',') || (c == ':') || (c == '?') || (c == '_') || (c == '@') || (c == '#') || (c == '$') || (c == '(') || (c == ')') || (c == '[') || (c == ']') || (c == '{') || (c == '}');
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
