import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private String curr_command = "";
    private String arg1 = null;
    private int arg2 = -1;
    private int type = -1;

    private final ArrayList<String> content;
    private int curr = -1;

    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public static final int C_GOTO = 4;
    public static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;

    private final String[] types = {"C_ARITHMETIC", "C_PUSH", "C_POP", "C_LABEL",
                                    "C_GOTO", "C_IF", "C_FUNCTION", "C_RETURN", "C_CALL"};
    public Parser(BufferedReader bufferedReader) {
        content = new ArrayList<>();
        try {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasMoreCommands() {
        return content.size() - 1 > curr;
    }


    public void advance() {
        String line;
        do {
            curr += 1;
            line = stripWhiteSpace(content.get(curr));
        } while ((line.equals("")) && hasMoreCommands());

        if (!line.equals("")) {
            curr_command = line;
            String[] parts = splitLine(line, " ");

            if (parts.length == 1) {
                if (!parts[0].equals("return")) {
                    type = C_ARITHMETIC;
                    arg1 = parts[0];
                }
            } else if (parts.length == 3) {
                if (parts[0].equals("push")) {
                    type = C_PUSH;
                } else if (parts[0].equals("pop")) {
                    type = C_POP;
                }
                arg1 = parts[1];
                arg2 = Integer.parseInt(parts[2]);
            }
        }
    }

    private static String[] splitLine(String line, String delimiter) {
        return line.split(delimiter);
    }

    private static String stripWhiteSpace(String str) {
        String trimStr = str.trim();
        String res = trimStr;

        for (int i = 0; i < trimStr.length() - 1; i ++) {
            if (trimStr.charAt(i) == '/' && trimStr.charAt(i + 1) == '/') {
                res = trimStr.substring(0, i);
                break;
            }
        }
        return res;
    }

    public int commandType() {
        return type;
    }

    public String arg1() {
        return arg1;
    }

    public int arg2() {
        if (type != C_FUNCTION && type != C_CALL && type != C_PUSH && type != C_POP) {
            throw new RuntimeException("Can not call arg2() method if instruction is " + types[type] + ".");
        }
        return arg2;
    }

}
