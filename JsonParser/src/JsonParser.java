import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class JsonParser {
    // Generates a list of tokens from a JSON string by iterating through the string
    // Tokens consist of a type and value
    private static List<Token> tokenize(String jsonString) throws InvalidJsonException {
        if (jsonString == null) throw new InvalidJsonException("Null string");
        String trimmedString = jsonString.trim();
        if (trimmedString.isEmpty()) throw new InvalidJsonException("Empty string");

        List<Token> tokens = new ArrayList<>();

        for (int i = 0; i < trimmedString.length(); i++) {
            char currentChar = trimmedString.charAt(i);
            if (Character.isWhitespace(currentChar)) continue;
            else if (currentChar == '{') tokens.add(new Token(TokenType.CURLY_BRACE_OPEN, '{'));
            else if (currentChar == '}') tokens.add(new Token(TokenType.CURLY_BRACE_CLOSE, '}'));
            else if (currentChar == '[') tokens.add(new Token(TokenType.SQUARE_BRACE_OPEN, '['));
            else if (currentChar == ']') tokens.add(new Token(TokenType.SQUARE_BRACE_CLOSE, ']'));
            else if (currentChar == ',') tokens.add(new Token(TokenType.COMMA, ','));
            else if (currentChar == ':') tokens.add(new Token(TokenType.COLON, ':'));
            else if (currentChar == '\"') {
                StringBuilder word = new StringBuilder();
                i++;
                while (!(trimmedString.charAt(i) == '"' && trimmedString.charAt(i - 1) != '\\')) {
                    word.append(trimmedString.charAt(i));
                    i++;
                }
                tokens.add(new Token(TokenType.STRING, word.toString()));
            }
            else if (currentChar == 'n' || currentChar == 'N') {
                if (i + 4 > trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find null at index " + i);
                }
                if (trimmedString.substring(i, i + 4).equalsIgnoreCase("null")) {
                    tokens.add(new Token(TokenType.NULL, null));
                    i += 3;
                }
            }
            else if (currentChar == 't' || currentChar == 'T') {
                if (i + 4 > trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find true at index " + i);
                }
                if (trimmedString.substring(i, i + 4).equalsIgnoreCase("true")) {
                    tokens.add(new Token(TokenType.BOOLEAN, true));
                    i += 3;
                }
            }
            else if (currentChar == 'f' || currentChar == 'F') {
                if (i + 5 > trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find false at index " + i);
                }
                if (trimmedString.substring(i, i + 5).equalsIgnoreCase("false")) {
                    tokens.add(new Token(TokenType.BOOLEAN, false));
                    i += 4;
                }
            }
            // currently does not handle exponents (ex. 1e+5)
            else if (Character.isDigit(currentChar) || currentChar == '-') {
                StringBuilder number = new StringBuilder();
                if (currentChar == '-') {
                    number.append('-');
                    i++;
                }
                while (Character.isDigit(trimmedString.charAt(i)) || trimmedString.charAt(i) == '.') {
                    number.append(trimmedString.charAt(i));
                    i++;
                }
                i--; // decrement index for comma
                try {
                    tokens.add(new Token(TokenType.NUMBER, Double.parseDouble(number.toString())));
                } catch (NumberFormatException e) {
                    throw new InvalidJsonException("Invalid number near index " + i);
                }
            }
        }
        return tokens;
    }

    // Generates a hashmap from a JSON string by first tokenizing and then parsing
    // By nature of a hashmap, the results are unordered
    public static HashMap<String, Object> parseJson(String jsonString) {
        try {
            List<Token> tokens = tokenize(jsonString);
            if (tokens.getFirst().getType() != TokenType.CURLY_BRACE_OPEN ||
                    tokens.getLast().getType() != TokenType.CURLY_BRACE_CLOSE) {
                throw new InvalidJsonException("Invalid JSON");
            }
            Iterator<Token> tokenIterator = tokens.iterator();
            return parseObject(tokenIterator);
        } catch (InvalidJsonException e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: add checks for commas to ensure valid JSON
    private static HashMap<String, Object> parseObject(Iterator<Token> tokenIterator) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        Token currentToken = tokenIterator.next();
        while (currentToken.getType() != TokenType.CURLY_BRACE_CLOSE) {
            if (currentToken.getType() == TokenType.STRING && tokenIterator.next().getType() == TokenType.COLON) {
                Token valueToken = tokenIterator.next();
                if (valueToken.getType() == TokenType.SQUARE_BRACE_OPEN) {
                    jsonMap.put(currentToken.getValue().toString(), parseArray(tokenIterator));
                }
                else if (valueToken.getType() == TokenType.CURLY_BRACE_OPEN) {
                    jsonMap.put(currentToken.getValue().toString(), parseObject(tokenIterator));
                }
                else if (valueToken.getType() == TokenType.STRING || valueToken.getType() == TokenType.NUMBER ||
                        valueToken.getType() == TokenType.BOOLEAN || valueToken.getType() == TokenType.NULL) {
                    jsonMap.put(currentToken.getValue().toString(), valueToken.getValue());
                }
                else {
                    throw new InvalidJsonException("Invalid value for key " + currentToken.getValue());
                }
            }
            currentToken = tokenIterator.next();
        }
        return jsonMap;
    }

    private static List<Object> parseArray(Iterator<Token> tokenIterator) {
        List<Object> tokenArray = new ArrayList<>();
        Token currentToken = tokenIterator.next();
        while (currentToken.getType() != TokenType.SQUARE_BRACE_CLOSE) {
            if (currentToken.getType() != TokenType.COMMA) {
                tokenArray.add(currentToken.getValue());
            }
            currentToken = tokenIterator.next();
        }
        return tokenArray;
    }
}