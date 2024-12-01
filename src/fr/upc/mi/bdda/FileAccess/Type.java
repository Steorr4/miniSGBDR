package fr.upc.mi.bdda.FileAccess;

/**
 * Classe générale pour représenter un type dans une table
 */
public abstract class Type {
    int taille; // Taille en Byte

    /**
     * Main constructor.
     *
     * @param taille la taille du type en Byte.
     */
    public Type(int taille) {
        this.taille = taille;
    }

    //Getters
    public int getTaille() {
        return taille;
    }
}
