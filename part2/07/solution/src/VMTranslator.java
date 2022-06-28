import java.io.*;

public class VMTranslator {
    private static BufferedReader read(String path) {
        BufferedReader br = null;
        try {
            FileReader fileReader = new FileReader(path);
            br = new BufferedReader(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return br;
    }

    public static void main(String[] args) {
        try {
            String sourcePath = args[0];
            String objectPath = sourcePath.substring(0, sourcePath.length() - 3) + ".asm";

            BufferedReader reader = read(sourcePath);

            Parser parser = new Parser(reader);
            CodeWriter codeWriter = new CodeWriter(objectPath);

            while (parser.hasMoreCommands()) {
                parser.advance();

                int type = parser.commandType();

                if (type == Parser.C_ARITHMETIC) {
                    codeWriter.writeArithmetic(parser.arg1());
                } else if (type == Parser.C_PUSH || type == Parser.C_POP) {
                    codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                }
            }
            codeWriter.close();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please enter source file path. For example: java VMTranslator source.vm\n");
            e.printStackTrace();
        }
    }
}
