import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonParser {
    private static List<Token> tokenize(String jsonString) throws InvalidJsonException {
        if (jsonString == null) throw new InvalidJsonException("Null string");
        String trimmedString = jsonString.trim();
        if (trimmedString.isEmpty()) throw new InvalidJsonException("Empty string");

        ArrayList<Token> tokens = new ArrayList<>();

        for (int i = 0; i < trimmedString.length(); i++) {
            char currentChar = trimmedString.charAt(i);
            if (currentChar == ' ') continue;
            if (currentChar == '{') tokens.add(new Token(TokenType.CURLY_BRACE_OPEN, '{'));
            if (currentChar == '}') tokens.add(new Token(TokenType.CURLY_BRACE_CLOSE, '}'));
            if (currentChar == '[') tokens.add(new Token(TokenType.SQUARE_BRACE_OPEN, '['));
            if (currentChar == ']') tokens.add(new Token(TokenType.SQUARE_BRACE_CLOSE, ']'));
            if (currentChar == ',') tokens.add(new Token(TokenType.COMMA, ','));
            if (currentChar == ':') tokens.add(new Token(TokenType.COLON, ':'));
            if (currentChar == '\"') {
                String word = "";
                i++;
                while (trimmedString.charAt(i) != '\"' && trimmedString.charAt(i - 1) != '\\') {
                    word += trimmedString.charAt(i);
                    i++;
                }
                tokens.add(new Token(TokenType.STRING, word));
            }
            if (currentChar == 'n' || currentChar == 'N') {
                if (i + 4 > trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find null at index " + i);
                }
                if (trimmedString.substring(i, i + 4).equalsIgnoreCase("null")) {
                    tokens.add(new Token(TokenType.NULL, null));
                    i += 4;
                }
            }
            if (currentChar == 't' || currentChar == 'T') {
                if (i + 4 > trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find true at index " + i);
                }
                if (trimmedString.substring(i, i + 4).equalsIgnoreCase("true")) {
                    tokens.add(new Token(TokenType.BOOLEAN, true));
                    i += 4;
                }
            }
            if (currentChar == 'f' || currentChar == 'F') {
                if (i + 5 > trimmedString.length()) {
                    throw new InvalidJsonException("Attempted to find false at index " + i);
                }
                if (trimmedString.substring(i, i + 5).equalsIgnoreCase("false")) {
                    tokens.add(new Token(TokenType.BOOLEAN, false));
                    i += 5;
                }
            }
            if (Character.isDigit(currentChar) || currentChar == '-') {
                String number = "";
                if (currentChar == '-') {
                    number += '-';
                    i++;
                }
                while (!Character.isDigit(trimmedString.charAt(i)) || trimmedString.charAt(i) != '.') {
                    number += trimmedString.charAt(i);
                    i++;
                }
                i--; // decrement index for comma
                tokens.add(new Token(TokenType.NUMBER, Double.parseDouble(number)));
            }
        }
        return tokens;
    }

    public static HashMap<String, Object> parse(String jsonString) {
        HashMap<String, Object> jsonMap = new HashMap<>();
        try {
            List<Token> tokens = tokenize(jsonString);
            if (tokens.getFirst().Type != TokenType.CURLY_BRACE_OPEN ||
                    tokens.getLast().Type != TokenType.CURLY_BRACE_CLOSE) {
                throw new InvalidJsonException("Invalid JSON");
            }
            // exclude first and last curly brace
            for (int i = 1; i < tokens.size() - 1; i++) {
                if (tokens.get(i).Type == TokenType.STRING && tokens.get(i + 1).Type == TokenType.COLON) {

                }
            }
        } catch (InvalidJsonException e) {
            e.printStackTrace();
        }
        return jsonMap;
    }

}