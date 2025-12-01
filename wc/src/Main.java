import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

// https://codingchallenges.fyi/challenges/challenge-wc
// The objective for this challenge is to recreate the wc command from Linux
// This implementation only allows for one option at a time

public class Main {
    public static void main(String[] args) {
        if (args.length == 0 || !args[args.length - 1].endsWith(".txt")) {
            handleWcFromStdin(args);
        }
        else {
            handleWcFromFile(args);
        }
    }

    static void handleWcFromStdin(String[] args) {
        try {
            byte[] stdinBytes = readAllBytes(System.in);
            String stdinString = new String(stdinBytes, StandardCharsets.UTF_8);
            String returnStr = "";

            if (args.length == 0) {
                returnStr += findNumberOfLinesInString(stdinString) + "\t";
                returnStr += findNumberOfWordsInString(stdinString) + "\t";
                returnStr += stdinBytes.length;
            }
            else {
                switch (args[0]) {
                    case "-c":
                        returnStr += stdinBytes.length;
                        break;
                    case "-l":
                        returnStr += findNumberOfLinesInString(stdinString);
                        break;
                    case "-w":
                        returnStr += findNumberOfWordsInString(stdinString);
                        break;
                    case "-m":
                        returnStr += stdinString.length();
                        break;
                    default:
                        System.out.println("Usage: java Main <option>\nOptions: -c -l -w -m");
                        return;
                }
            }
            System.out.println(returnStr);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int numBytesRead;
        while ((numBytesRead = in.read(buffer)) != -1) {
            baos.write(buffer, 0, numBytesRead);
        }
        return baos.toByteArray();
    }

    static long findNumberOfLinesInString(String s) {
        return s.chars().filter(c -> c == '\n').count();
    }

    static long findNumberOfWordsInString(String s) {
        return s.split("\\s+").length;
    }

    static void handleWcFromFile(String[] args) {
        String fileName = args[args.length - 1];

        if (!fileName.endsWith(".txt")) {
            System.out.println("Please enter a valid txt file name.");
            return;
        }

        Path filePath = Paths.get(fileName);
        if (!Files.exists(filePath) || !Files.isReadable(filePath) || !Files.isRegularFile(filePath)) {
            System.out.println("Unable to open the specified file.");
            return;
        }

        String returnStr = "";

        try {
            switch (args[0]) {
                case "-c":
                    returnStr += findNumberOfBytesInFile(filePath);
                    break;
                case "-l":
                    returnStr += findNumberOfLinesInFile(filePath);
                    break;
                case "-w":
                    returnStr += findNumberOfWordsInFile(filePath);
                    break;
                case "-m":
                    returnStr += findNumberOfCharactersInFile(filePath);
                    break;
                default:
                    if (!args[0].equals(fileName)) {
                        System.out.println("Usage: java Main <option> [filename.txt]\nOptions: -c -l -w -m");
                        return;
                    }
                    returnStr += findNumberOfLinesInFile(filePath) + "\t";
                    returnStr += findNumberOfWordsInFile(filePath) + "\t";
                    returnStr += findNumberOfBytesInFile(filePath);
            }
            System.out.println(returnStr + "\t" + fileName);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static long findNumberOfBytesInFile(Path filePath) throws IOException {
        return Files.size(filePath);
    }

    static long findNumberOfLinesInFile(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.count();
        }
    }

    static long findNumberOfWordsInFile(Path filePath) throws IOException {
        long wordCount = 0;
        List<String> lines = Files.readAllLines(filePath);
        for (String line : lines) {
            String[] words = line.trim().split("\\s+");
            if (words.length < 1 || words[0].isEmpty()) {
                continue;
            }
            wordCount += words.length;
        }
        return wordCount;
    }

    static long findNumberOfCharactersInFile(Path filePath) throws IOException {
        return Files.readString(filePath).length();
    }
}