public class Parser {
    private String symbol;       // @ symbol or @ value
    private String comp;         // dest = comp ; jump
    private String dest;
    private String jump;

    private String label;

    public Parser(Instruction instruction) {
        symbol = null;
        comp = null;
        dest = null;
        jump = null;
        label = null;

        if (instruction.getType() == Instruction.ATYPE) {
            aTypeParser(instruction.instruction());
        } else if (instruction.getType() == Instruction.CTYPE) {
            cTypeParser(instruction.instruction());
        } else if (instruction.getType() == Instruction.LABEL) {
            labelParser(instruction.instruction());
        }
    }

    private void aTypeParser(String s) {
        symbol = s.substring(1, s.length());
    }

    private void cTypeParser(String s) {
        String[] sub = s.split(";");

        if (sub.length == 2) {
            jump = sub[1];
        }

        String[] tmp = sub[0].split("=");

        if (tmp.length == 2) {
            dest = tmp[0];
            comp = tmp[1];
        } else {
            comp = tmp[0];
        }
    }


    private void labelParser(String s) {
        label = s.substring(1, s.length() - 1);
    }


    public String getSymbol() {
        return symbol;
    }


    public String getComp() {
        return comp;
    }


    public String getDest() {
        return dest;
    }

    public String getJump() {
        return jump;
    }

    public String getLabel() {
        return label;
    }


    public static void main(String[] args) {
        Instruction instruction = new Instruction("M    // something");
        Parser parser = new Parser(instruction);

        System.out.println("symbol: " + parser.getSymbol());
        System.out.println("label: " + parser.getLabel());
        System.out.println("dest: " + parser.getDest());
        System.out.println("comp: " + parser.getComp());
        System.out.println("jump: " + parser.getJump());
    }
}
