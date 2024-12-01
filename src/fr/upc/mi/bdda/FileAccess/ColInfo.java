package fr.upc.mi.bdda.FileAccess;

/**
 * Classe représentant le nom et le type d'une colonne dans une relation.
 */
public class ColInfo {
    private String nomCol;
    private Type typeCol;

    /**
     * Main constructor.
     *
     * @param nomCol le nom de la Colonne.
     * @param typeCol le type de la Colonne.
     */
    public ColInfo(String nomCol, Type typeCol){
        this.nomCol=nomCol;
        this.typeCol=typeCol;
    }

    //Getters & Setters
    public String getNomCol() {
        return nomCol;
    }
    public void setNomCol(String nomCol) {
        this.nomCol = nomCol;
    }
    public Type getTypeCol() {
        return typeCol;
    }
    public void setTypeCol(Type typeCol) {
        this.typeCol = typeCol;
    }

}
