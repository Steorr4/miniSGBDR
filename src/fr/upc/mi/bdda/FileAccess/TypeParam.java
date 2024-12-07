package fr.upc.mi.bdda.FileAccess;

import java.io.Serializable;

/**
 * Classe pour représenter un type paramétré dans une table.
 */
public class TypeParam extends Type implements Serializable {
    ETypeParam type;

    /**
     * Enumérations des types paramètrés possibles.
     */
    public enum ETypeParam implements Serializable{
        CHAR,
        VARCHAR
    }

    /**
     * Main constructor.
     *
     * @param type le type paramètré.
     */
    public TypeParam(int taille, ETypeParam type) {
        super(taille);
        this.type = type;
    }

    //Getter
    public ETypeParam getType() {
        return type;
    }
}
