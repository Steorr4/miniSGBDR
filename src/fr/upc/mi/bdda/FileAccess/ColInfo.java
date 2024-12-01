package fr.upc.mi.bdda.FileAccess;

public class ColInfo {
    private String nomCol;
    private Type typeCol;

    public ColInfo(String nomCol,Type typeCol){
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
