package fr.upc.mi.bdda.FileAccess;

import java.io.Serializable;

/**
 * Classe générale pour représenter un type dans une table
 */
public abstract class Type implements Serializable {
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
