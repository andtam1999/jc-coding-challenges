import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        String example = "{}";
        HashMap<String, Object> parsed = JsonParser.parseJson(example);
    }
}