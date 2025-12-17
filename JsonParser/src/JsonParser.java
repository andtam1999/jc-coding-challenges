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
            if (currentChar == ' ') continue;
            else if (currentChar == '{') tokens.add(new Token(TokenType.CURLY_BRACE_OPEN, '{'));
            else if (currentChar == '}') tokens.add(new Token(TokenType.CURLY_BRACE_CLOSE, '}'));
            else if (currentChar == '[') tokens.add(new Token(TokenType.SQUARE_BRACE_OPEN, '['));
            else if (currentChar == ']') tokens.add(new Token(TokenType.SQUARE_BRACE_CLOSE, ']'));
            else if (currentChar == ',') tokens.add(new Token(TokenType.COMMA, ','));
            else if (currentChar == ':') tokens.add(new Token(TokenType.COLON, ':'));
            else if (currentChar == '\"') {
                String word = "";
                i++;
                while (trimmedString.charAt(i) != '\"' && trimmedString.charAt(i - 1) != '\\') {
                    word += trimmedString.charAt(i);
                    i++;
                }
                tokens.add(new Token(TokenType.STRING, word));
            }
            else if (currentChar == 'n' || currentChar == 'N') {
                if (i + 4 >= trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find null at index " + i);
                }
                if (trimmedString.substring(i, i + 4).equalsIgnoreCase("null")) {
                    tokens.add(new Token(TokenType.NULL, null));
                    i += 4;
                }
            }
            else if (currentChar == 't' || currentChar == 'T') {
                if (i + 4 >= trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find true at index " + i);
                }
                if (trimmedString.substring(i, i + 4).equalsIgnoreCase("true")) {
                    tokens.add(new Token(TokenType.BOOLEAN, true));
                    i += 4;
                }
            }
            else if (currentChar == 'f' || currentChar == 'F') {
                if (i + 5 >= trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find false at index " + i);
                }
                if (trimmedString.substring(i, i + 5).equalsIgnoreCase("false")) {
                    tokens.add(new Token(TokenType.BOOLEAN, false));
                    i += 5;
                }
            }
            else if (Character.isDigit(currentChar) || currentChar == '-') {
                String number = "";
                if (currentChar == '-') {
                    number += '-';
                    i++;
                }
                while (Character.isDigit(trimmedString.charAt(i)) || trimmedString.charAt(i) == '.') {
                    number += trimmedString.charAt(i);
                    i++;
                }
                i--; // decrement index for comma
                tokens.add(new Token(TokenType.NUMBER, Double.parseDouble(number)));
            }
        }
        return tokens;
    }

    // Generates a hashmap from a JSON string by first tokenizing and then parsing
    // By nature of a hashmap, the results are unordered
    public static HashMap<String, Object> parseJson(String jsonString) {
        try {
            List<Token> tokens = tokenize(jsonString);
            if (tokens.getFirst().Type != TokenType.CURLY_BRACE_OPEN ||
                    tokens.getLast().Type != TokenType.CURLY_BRACE_CLOSE) {
                throw new InvalidJsonException("Invalid JSON");
            }
            Iterator<Token> tokenIterator = tokens.iterator();
            return parseObject(tokenIterator);
        } catch (InvalidJsonException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<String, Object> parseObject(Iterator<Token> tokenIterator) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        Token currentToken = tokenIterator.next();
        while (currentToken.Type != TokenType.CURLY_BRACE_CLOSE) {
            if (currentToken.Type == TokenType.STRING && tokenIterator.next().Type == TokenType.COLON) {
                Token valueToken = tokenIterator.next();
                if (valueToken.Type == TokenType.SQUARE_BRACE_OPEN) {
                    jsonMap.put(currentToken.Value.toString(), parseArray(tokenIterator));
                }
                else if (valueToken.Type == TokenType.CURLY_BRACE_OPEN) {
                    jsonMap.put(currentToken.Value.toString(), parseObject(tokenIterator));
                }
                else if (valueToken.Type == TokenType.STRING || valueToken.Type == TokenType.NUMBER ||
                        valueToken.Type == TokenType.BOOLEAN || valueToken.Type == TokenType.NULL) {
                    jsonMap.put(currentToken.Value.toString(), valueToken.Value);
                }
                else {
                    throw new InvalidJsonException("Invalid value for key " + currentToken.Value);
                }
            }
            currentToken = tokenIterator.next();
        }
        return jsonMap;
    }

    private static List<Object> parseArray(Iterator<Token> tokenIterator) {
        List<Object> tokenArray = new ArrayList<>();
        Token currentToken = tokenIterator.next();
        while (currentToken.Type != TokenType.SQUARE_BRACE_CLOSE) {
            tokenArray.add(currentToken.Value);
            currentToken = tokenIterator.next();
        }
        return tokenArray;
    }
}