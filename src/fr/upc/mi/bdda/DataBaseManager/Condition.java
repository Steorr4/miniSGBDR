package fr.upc.mi.bdda.DataBaseManager;

/**
 * Classe permettant de gerer les conditions d'une requte sql de type SELECT.
 * <br/>(Voir TP7-B1 pour comprendre le fonctionnement plus en détail)
 */
public class Condition {
    private String op;
    private String val;
    private int indiceCol1;
    private int indiceCol2;

    /**
     * Constructor.
     *
     * @param val valeur condition.
     * @param op opérateur.
     * @param indiceCol1 indice de la colonne d'une relation R.
     */
    public Condition(String val, String op, int indiceCol1) {
        this.val = val;
        this.op = op;
        this.indiceCol1 = indiceCol1;
    }

    /**
     * Constructor.
     *
     * @param indiceCol1 indice de la colonne d'une relation R.
     * @param op opérateur.
     * @param val valeur condition.
     */
    public Condition(int indiceCol1, String op, String val) {
        this.val = val;
        this.indiceCol1 = indiceCol1;
        this.op = invertOpe(op);
    }

    /**
     * Constructor.
     *
     * @param indiceCol1 indice de la colonne d'une relation R1.
     * @param op opérateur.
     * @param indiceCol2 indice de la colonne d'une relation R2.
     */
    public Condition(int indiceCol1, String op, int indiceCol2) {
        this.indiceCol1 = indiceCol1;
        this.op = op;
        this.indiceCol2 = indiceCol2;
    }

    /**
     * Constructor.
     *
     * @param indiceCol1 indice de la colonne d'une relation R1.
     * @param op opérateur.
     * @param indiceCol2 indice de la colonne d'une relation R2.
     * @param invert booleen pour inverser l'ordre d'une expression.
     */
    public Condition(int indiceCol1, String op, int indiceCol2, boolean invert){
        this.indiceCol1 = indiceCol1;
        if(invert){
            this.op = invertOpe(op);
        }else {
            this.op = op;
        }
        this.indiceCol2 = indiceCol2;
    }

    /**
     * Permet d'inverser l'ordre d'une expression.
     *
     * @param op opérateur.
     * @return
     */
    private String invertOpe(String op){
        switch (op){
            case "<" -> {
                return ">";
            }
            case "<=" -> {
                return ">=";
            }
            case ">" -> {
                return "<";
            }
            case ">=" -> {
                return "<=";
            }
            default -> {
                return op;
            }
        }
    }

    //Getters & Setters
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
