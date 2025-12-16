import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        // simple demonstration
        String example = "{}";
        HashMap<String, Object> parsed = JsonParser.parse(example);
    }
}