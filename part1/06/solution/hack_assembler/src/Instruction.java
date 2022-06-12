import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Instruction {
    private final char type;  // A, C, L (label) or N (none)
    private final String instruction;

    public static final char ATYPE = 'A';
    public static final char CTYPE = 'C';
    public static final char LABEL = 'L';
    public static final char NONE = 'N';

    public Instruction(String s) {
        instruction = clear(s);

        if (instruction.equals("")) {
            type = NONE;
        } else if (instruction.charAt(0) == '@') {
            type = ATYPE;
        } else if (instruction.charAt(0) == '(') {
            type = LABEL;
        } else {
            type = CTYPE;
        }
    }


    private String clear(String s) {
        String res = "";
        String withoutSpace = s.replaceAll(" ", "");
        if (!withoutSpace.equals("")) {
            int i = findComment(withoutSpace);
            res += withoutSpace.substring(0, i);
        }

        return res;
    }

    private int findComment(String s) {
        for (int i = 0; i < s.length() - 1; i ++) {
            if (s.charAt(i) == '/' && s.charAt(i + 1) == '/') {
                return i;
            }
        }
        return s.length();
    }


    public String instruction() {
        return instruction;
    }

    public char getType() {
        return type;
    }

    @Override
    public String toString() {
        return instruction + "( Type: " + type + " )";
    }

    public static void main(String[] args) {
        String source = "./src/add.asm";

        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = br.readLine()) != null) {
                Instruction tmp = new Instruction(line);
                System.out.println(tmp + "       " + tmp.getType());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
