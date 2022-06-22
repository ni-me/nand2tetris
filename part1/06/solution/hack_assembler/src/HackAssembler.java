import java.io.*;
import java.util.ArrayList;

public class HackAssembler {

    // clear white space (comment, space...)  and label, update symbol table
    private static ArrayList<Instruction> firstPass(String[] lines, SymbolTable symbolTable) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        for (String line : lines) {
            Instruction instruction = new Instruction(line);
            if (instruction.getType() != Instruction.NONE) {
                if (instruction.getType() == Instruction.LABEL) {
                    String label = new Parser(instruction).getLabel();
                    symbolTable.add(label, instructions.size());
                } else {
                    instructions.add(instruction);
                }
            }
        }
        return instructions;
    }

    // generate machine code
    private static String[] secondPass(ArrayList<Instruction> instructions, SymbolTable symbolTable, Code code) {
        int size = instructions.size();
        String[] codeStrings = new String[size];

        for (int i = 0; i < size; i ++) {
            Instruction instruction = instructions.get(i);
            Parser parser = new Parser(instruction);
            String res = null;

            if (instruction.getType() == Instruction.ATYPE) {
                res = "0";
                res += code.getAddress(parser.getSymbol(), symbolTable);
            } else if (instruction.getType() == Instruction.CTYPE) {
                res = "111";
                String a = code.getAbits(parser.getComp());
                String c = code.getCbits(parser.getComp());
                String d = code.getDbits(parser.getDest());
                String j = code.getJbits(parser.getJump());
                res = res + a + c + d + j;
            }
            codeStrings[i] = res;
        }
        return codeStrings;
    }

    private static String[] read(String filepath) {
        File file = new File(filepath);
        Long length = file.length();
        byte[] content = new byte[length.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(content);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] fileContenArry = new String(content).split("\r\n");
        return fileContenArry;
    }

    public static void write(String filepath, String[] content) {
        try {
            File file = new File(filepath);
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(writer);

            int size = content.length;
            for (int i = 0; i < size; i++) {
                if (content[i] != null)
                    out.write(content[i] + (i < size - 1 ? "\n" : ""));
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable();
        Code code = new Code();
        String sourcePath = null;

        try {
            sourcePath = args[0];
        } catch (Exception e) {
            System.out.println("Please type source file path, for example: java HackAssembler ./source.asm");
        }

        String objectPath = sourcePath.substring(0, sourcePath.length() - 4) + ".hack";

        String[] sourceFileContent = read(sourcePath);

        ArrayList<Instruction> instructions = firstPass(sourceFileContent, symbolTable);
        String[] codeStrings = secondPass(instructions, symbolTable, code);

        write(objectPath, codeStrings);
    }
}
