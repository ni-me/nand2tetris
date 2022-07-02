import java.io.*;
import java.util.ArrayList;

public class VMTranslator {

    private static String getTargetPath(String path) {
        File file = new File(path);
        String name = file.getName();
        return path + name + ".asm";
    }

    private static ArrayList<String> getFilePaths(String path, String extended) {
        File file = new File(path);
        ArrayList<String> filePaths = new ArrayList<>();
        String[] fileNames = file.list();

        for (String fileName : fileNames) {
            if (fileName.endsWith(extended)) {
                filePaths.add(path + fileName);
            }
        }

        return filePaths;
    }


    public static void main(String[] args) {

        try {
            String sourceDirPath = args[0];
            // String sourceDirPath = "./resource/StaticsTest/";
            String targetFilePath = getTargetPath(sourceDirPath);
            ArrayList<String> paths = getFilePaths(sourceDirPath, ".vm");

            CodeWriter codeWriter = new CodeWriter(targetFilePath);
            codeWriter.writeInit();

            for (String path : paths) {
                System.out.println(path);
                Parser parser = new Parser(path);
                codeWriter.setCurrentFileName(path);

                while (parser.hasMoreCommands()) {
                    parser.advance();

                    int type = parser.commandType();

                    if (type == Parser.C_ARITHMETIC) {
                        codeWriter.writeArithmetic(parser.arg1());
                    } else if (type == Parser.C_PUSH || type == Parser.C_POP) {
                        codeWriter.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                    } else if (type == Parser.C_CALL) {
                        codeWriter.writeCall(parser.arg1(), parser.arg2());
                    } else if (type == Parser.C_RETURN) {
                        codeWriter.writeReturn();
                    } else if (type == Parser.C_FUNCTION) {
                        codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    } else if (type == Parser.C_GOTO) {
                        codeWriter.writeGoto(parser.arg1());
                    } else if (type == Parser.C_IF) {
                        codeWriter.writeIf(parser.arg1());
                    } else if (type == Parser.C_LABEL) {
                        codeWriter.writeLabel(parser.arg1());
                    }
                }
            }
            codeWriter.close();
        } catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Please enter source file path. For example: java VMTranslator source.vm\n");
            e.printStackTrace();
        }

    }

}
