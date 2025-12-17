public class Token {
    private final TokenType Type;
    private final Object Value;

    public Token(TokenType type, Object value) {
        Type = type;
        Value = value;
    }

    public TokenType getType() { return Type; }
    public Object getValue() { return Value; }
}
