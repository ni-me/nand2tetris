import java.util.HashMap;

public class Code {

    private HashMap<String, String> compMap;
    private HashMap<String, String> jumpMap;

    private  HashMap<Integer, Integer> destMap;

    private int curr;


    public Code() {
        initCompMap();
        initJumpMap();
        initDestMap();
        curr = 16;
    }

    private void initCompMap() {
        compMap = new HashMap<>();
        compMap.put("0", "101010");   compMap.put("1", "111111");   compMap.put("-1", "111010");
        compMap.put("D", "001100");   compMap.put("A", "110000");   compMap.put("M", "110000");
        compMap.put("!D", "001101");  compMap.put("!A", "110001");  compMap.put("!M", "110001");
        compMap.put("-D", "001111");  compMap.put("-A", "110011");  compMap.put("-M", "110011");
        compMap.put("D+1", "011111"); compMap.put("A+1", "110111"); compMap.put("M+1", "110111");
        compMap.put("D-1", "001110"); compMap.put("A-1", "110010"); compMap.put("M-1", "110010");
        compMap.put("D+A", "000010"); compMap.put("D+M", "000010"); compMap.put("D-A", "010011");
        compMap.put("D-M", "010011"); compMap.put("A-D", "000111"); compMap.put("M-D", "000111");
        compMap.put("D&A", "000000"); compMap.put("D&M", "000000"); compMap.put("D|A", "010101");
        compMap.put("D|M", "010101");
    }

    private void initJumpMap() {
        jumpMap = new HashMap<>();

        String[] ks = {null, "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP"};

        for (int i = 0; i < ks.length; i ++) {
            jumpMap.put(ks[i], toBinaryStr(i, 3));
        }
    }


    private void initDestMap() {
        destMap = new HashMap<>();
        for (int i = 0; i < 8; i ++) {
            destMap.put(i, i);
        }
    }

    private void printJumpMap() {
        System.out.print("jumpMap\n");
        print(jumpMap);
    }

    private void print(HashMap<String, String> map) {
        System.out.println("size: " + map.size() + "\n");
        for (String key : map.keySet()) {
            System.out.println(key + " : " + map.get(key));
        }
    }


    public String getAbits(String comp) {
        return comp.contains("M") ? "1" : "0";
    }

    public String getCbits(String comp) {
        return compMap.get(comp);
    }

    public String getDbits(String dest) {

        int num = 0;

        if (dest != null) {
            if (dest.contains("M")) {
                num += 1;
            }

            if (dest.contains("D")) {
                num += 2;
            }

            if (dest.contains("A")) {
                num += 4;
            }
        }

        return toBinaryStr(destMap.get(num), 3);
    }

    public String getJbits(String jump) {
        return jumpMap.get(jump);
    }

    public String getAddress(String symbol, SymbolTable table) {
        String res = null;
        if (isNum(symbol)) {
            res = toBinaryStr(Integer.valueOf(symbol), 15);
        } else if (table.find(symbol)) {
            res = toBinaryStr(table.get(symbol), 15);
        } else {
            table.add(symbol, curr);
            res = toBinaryStr(curr, 15);
            curr += 1;
        }

        return res;
    }

    private boolean isNum(String s) {
        try {
            int num = Integer.valueOf(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String toBinaryStr(int num, int len) {
        String binStr = Integer.toBinaryString(num);
        int l = binStr.length();

        for (int i = 0; i < len - l; i ++) {
            binStr = "0" + binStr;
        }
        return binStr;
    }


    public static void main(String[] args) {
        Code code = new Code();
        System.out.println(code.getAddress("1222", new SymbolTable()));

    }

}
