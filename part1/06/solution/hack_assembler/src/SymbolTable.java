import java.util.HashMap;
import java.util.Set;

public class SymbolTable {
    private HashMap<String, Integer> table;
    public SymbolTable() {
        initTable();
    }

    private void initTable() {
        table = new HashMap<>();
        for (int i = 0; i < 16; i ++) {
            table.put("R" + i, i);
        }

        table.put("SCREEN", 16384);
        table.put("KBD", 24576);

        String[] ks = {"SP", "LCL", "ARG", "THIS", "THAT"};
        for (int i = 0; i < ks.length; i ++) {
            table.put(ks[i], i);
        }
    }


    private void printTable() {
        Set<String> keys = table.keySet();
        System.out.println("Table size: " + table.size() + "\n");

        for (String key : keys) {
            System.out.println(key + " : " + table.get(key));
        }
    }

    public void add(String key, int val) {
        table.put(key, val);
    }

    public int get(String key) {
        return table.get(key);
    }

    public boolean find(String key) {
        return table.containsKey(key);
    }


    public static void main(String[] args) {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.printTable();
        System.out.println(symbolTable.find("THIS"));
        System.out.println(symbolTable.find("HELLO"));

    }

}
