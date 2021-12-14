package JORG.lexical;

public class Token {
    private final String tokenValue;
    private final TokenCategory tokenCategory;
    private final int tokenRow;
    private final int tokenColumn;
    
    public Token(String tokenValue, TokenCategory tokenCategory, int tokenRow, int tokenColumn){
        this.tokenValue = tokenValue;
        this.tokenCategory = tokenCategory;
        this.tokenRow = tokenRow;
        this.tokenColumn = tokenColumn;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public TokenCategory getTokenCategory() {
        return tokenCategory;
    }

    public int getTokenRow() {
        return tokenRow;
    }

    public int getTokenColumn() {
        return tokenColumn;
    }

    @Override
    public String toString() {
        String format = "              [%04d, %04d] (%04d, %20s) {%s}";
        return String.format(format, tokenRow, tokenColumn, tokenCategory.getNumericCode(), tokenCategory.toString(), tokenValue);
    }
}
