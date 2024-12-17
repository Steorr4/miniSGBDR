package fr.upc.mi.bdda.DataBaseManager;

public class Condition {
    private String op;
    private String val;
    private int indiceCol1;
    private int indiceCol2;

    public Condition(String val, String op, int indiceCol1) {
        this.val = val;
        this.op = op;
        this.indiceCol1 = indiceCol1;
    }

    public Condition(int indiceCol1, String op, String val) {
        this.val = val;
        this.indiceCol1 = indiceCol1;
        switch (op){
            case "<" -> this.op=">";
            case "<=" -> this.op=">=";
            case ">" -> this.op="<";
            case ">=" -> this.op="<=";
            default -> this.op = op;
        }
    }

    public Condition(int indiceCol1, String op, int indiceCol2) {
        this.indiceCol1 = indiceCol1;
        this.op = op;
        this.indiceCol2 = indiceCol2;
    }

    public String getOp() {
        return op;
    }

    public String getVal() {
        return val;
    }

    public int getIndiceCol1() {
        return indiceCol1;
    }

    public int getIndiceCol2() {
        return indiceCol2;
    }
}
