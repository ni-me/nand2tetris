import java.io.*;
import java.util.ArrayList;

public class HackAssembler {

    // clear white space (comment, space...)  and label, update symbol table
    private static void firstPass(String line, ArrayList<Instruction> content, SymbolTable symbolTable) {
        Instruction instruction = new Instruction(line);
        if (instruction.getType() != Instruction.NONE) {
            if (instruction.getType() == Instruction.LABEL) {
                String label = new Parser(instruction).getLabel();
                symbolTable.add(label,  content.size());
            } else {
                content.add(instruction);
            }
        }
    }

    // generate machine code
    private static String secondPass(ArrayList<Instruction> instructions, int index, SymbolTable symbolTable, Code code) {
        Instruction instruction = instructions.get(index);
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
        return res;
    }

    public static void main(String[] args) throws IOException {

        ArrayList<Instruction> content = new ArrayList<>();
        SymbolTable st = new SymbolTable();
        Code code = new Code();

        String source = "./resource/RectL.asm";


        try (BufferedReader br = new BufferedReader(new FileReader(source))) {
            String line;
            while ((line = br.readLine()) != null) {
                firstPass(line, content, st);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        String outfilename = source.substring(0, source.length() - 4) + ".hack";
        File outfile = new File(outfilename);
        outfile.createNewFile();

        try (FileWriter writer = new FileWriter(outfile);
             BufferedWriter out = new BufferedWriter(writer)
        ) {
            int size = content.size();
            for (int i = 0; i < size; i++) {
                out.write(secondPass(content, i, st, code) + (i < size - 1 ? "\n" : ""));
            }
            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
