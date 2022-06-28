import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class CodeWriter {
    private HashMap<String, String> arithmeticMap;

    // local, argument, this, that
    private HashMap<String, String> LATTSegmentMap;
    // temp, pointer
    private HashMap<String, Integer> TPSegmentMap;

    private HashMap<String, String> EGLCommandMap;
    private final BufferedWriter bufferedWriter;
    private int cnt = 0;

    private String fileName = null;


    public CodeWriter(String path) {
        File file = new File(path.trim());
        fileName = file.getName();
        bufferedWriter = writer(path);

        initEGLCommandMap();
        initArithmeticMap();
        initLATTSegmentMap();
        initTPSegmentMap();
    }

    private BufferedWriter writer(String path) {
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            bw = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bw;
    }

    private void initLATTSegmentMap() {
        LATTSegmentMap = new HashMap<>();
        LATTSegmentMap.put("local", "LCL");
        LATTSegmentMap.put("argument", "ARG");
        LATTSegmentMap.put("this", "THIS");
        LATTSegmentMap.put("that", "THAT");
    }

    private void initTPSegmentMap() {
        TPSegmentMap = new HashMap<>();
        TPSegmentMap.put("temp", 5);
        TPSegmentMap.put("pointer", 3);
    }

    private void initEGLCommandMap() {
        EGLCommandMap = new HashMap<>();
        EGLCommandMap.put("eq", "JEQ");
        EGLCommandMap.put("gt", "JGT");
        EGLCommandMap.put("lt", "JLT");
    }
    private void initArithmeticMap() {
        arithmeticMap = new HashMap<>();

        arithmeticMap.put("add", "+");
        arithmeticMap.put("sub", "-");
        arithmeticMap.put("neg", "-");
        arithmeticMap.put("and", "&");
        arithmeticMap.put("or", "|");
        arithmeticMap.put("not", "!");
    }

    private void EGLCommandProcess(String operation) {
        try {
            String trueLabel = "true_" + cnt;
            String falseLabel = "false_" + cnt;
            String endLabel = "end_" + cnt;
            cnt += 1;

            bufferedWriter.write("D=M-D\n");
            bufferedWriter.write("@" + trueLabel + "\n");
            bufferedWriter.write("D;" + operation + "\n");
            bufferedWriter.write("(" + falseLabel + ")\n");
            bufferedWriter.write("D=0\n");
            bufferedWriter.write("@" + endLabel + "\n");
            bufferedWriter.write("0;JMP\n");
            bufferedWriter.write("(" + trueLabel + ")\n");
            bufferedWriter.write("D=-1\n");
            bufferedWriter.write("(" + endLabel + ")\n");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void writeArithmetic(String command) {
        writeComment(command);

        if (!isEGLCommand(command)) {
            String op = arithmeticMap.get(command);
            pop("D");
            if (isUnary(command)) {
                arithmeticProcess(op + "D");
            } else {
                pop("M");
                arithmeticProcess("M" + op + "D");
            }
            push();
        } else {
            String op = EGLCommandMap.get(command);
            pop("D");
            pop("M");
            EGLCommandProcess(op);
            push();
        }
    }

    // is eq gt lt ?
    private boolean isEGLCommand(String command) {
        return command.equals("eq") || command.equals("gt") || command.equals("lt");
    }

    private boolean isUnary(String command) {
        return command.equals("neg") || command.equals("not");
    }

    // pop --> D or M
    private void pop(String dest) {
        try {
            bufferedWriter.write("@SP\n");
            bufferedWriter.write("AM=M-1\n");
            bufferedWriter.write(dest + "=M\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // process D
    private void arithmeticProcess(String operation) {
        try {
            bufferedWriter.write("D=" + operation + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // D --> push
    private void push() {
        try {
            bufferedWriter.write("@SP\n");
            bufferedWriter.write("A=M\n");
            bufferedWriter.write("M=D\n");
            bufferedWriter.write("@SP\n");
            bufferedWriter.write("M=M+1\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeComment(String command) {
        try {
            bufferedWriter.write("//" + command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String whichCommand(int type) {
        String command = null;
        if (type == Parser.C_POP) {
            command = "pop";
        } else if (type == Parser.C_PUSH) {
            command = "push";
        }

        return command;
    }

    public void writePushPop(int command, String segment, int index) {
        String whole_command = whichCommand(command) + " " + segment + " " + index;
        writeComment(whole_command);
        if (command == Parser.C_PUSH) {
            segment2D(segment, index);
            push();
        } else if (command == Parser.C_POP) {
            pop("D");
            D2Segment(segment, index);
        }
    }

    private boolean isConstantSegment(String segment) {
        return segment.equals("constant");
    }

    private void writeConstant2D(int num) {
        try {
            bufferedWriter.write("@" + num + "\n");
            bufferedWriter.write("D=A\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void segment2D(String segment, int index) {
        if (isConstantSegment(segment)) {   // constant section
            writeConstant2D(index);
        } else {
            if (LATTSegmentMap.containsKey(segment)) {
                writeLATTSegment2D(LATTSegmentMap.get(segment), index);
            } else if (TPSegmentMap.containsKey(segment)) {
                writeTPSegment2D(TPSegmentMap.get(segment), index);
            } else {  // static section
                writeStaticSegment2D(fileName, index);
            }
        }
    }

    private void writeStaticSegment2D(String name, int index) {
        try {
            String label = name.split("\\.")[0] + "." + index;
            bufferedWriter.write("@" + label + "\n");
            bufferedWriter.write("D=M\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeD2StaticSegment(String name, int index) {
        try {
            String label = name.split("\\.")[0] + "." + index;
            bufferedWriter.write("@" + label + "\n");
            bufferedWriter.write("M=D\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeLATTSegment2D(String label, int index) {
        try {
            bufferedWriter.write("@" + label + "\n");
            bufferedWriter.write("D=M\n");
            bufferedWriter.write("@" + index + "\n");
            bufferedWriter.write("A=D+A\n");
            bufferedWriter.write("D=M\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeTPSegment2D(int start, int index) {
        try {
            int pos = start + index;
            bufferedWriter.write("@" + pos + "\n");
            bufferedWriter.write("D=M\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void D2Segment(String segment, int index) {
        if (LATTSegmentMap.containsKey(segment)) {
            writeD2LATTSegment(LATTSegmentMap.get(segment), index);
        } else if (TPSegmentMap.containsKey(segment)) {
            writeD2TPSegment(TPSegmentMap.get(segment), index);
        } else {  // static section
            writeD2StaticSegment(fileName, index);
        }
    }

    private void writeD2LATTSegment(String label, int index) {
        try {
            bufferedWriter.write("@R13\n");
            bufferedWriter.write("M=D\n");

            bufferedWriter.write("@" + label + "\n");
            bufferedWriter.write("D=M\n");
            bufferedWriter.write("@" + index + "\n");
            bufferedWriter.write("D=D+A\n");

            bufferedWriter.write("@R14\n");
            bufferedWriter.write("M=D\n");

            bufferedWriter.write("@R13\n");
            bufferedWriter.write("D=M\n");

            bufferedWriter.write("@R14\n");
            bufferedWriter.write("A=M\n");
            bufferedWriter.write("M=D\n");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeD2TPSegment(int start, int index) {
        try {
            int pos = start + index;
            bufferedWriter.write("@" + pos + "\n");
            bufferedWriter.write("M=D\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void close() {
        try {
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
