public class Token {
    public TokenType Type;
    public Object Value;

    public Token(TokenType type, Object value) {
        Type = type;
        Value = value;
    }
}
