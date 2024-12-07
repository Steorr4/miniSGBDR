package fr.upc.mi.bdda.FileAccess;

import java.io.Serializable;

/**
 * Classe pour représenter un type paramétré dans une table.
 */
public class TypeParam extends Type{
    ETypeParam type;

    /**
     * Enumérations des types paramètrés possibles.
     */
    public enum ETypeParam{
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

    //toString
    @Override
    public String toString() {
        if(type == ETypeParam.CHAR) return "CHAR(" + this.getTaille() + ")";
        return "VARCHAR(" + this.getTaille() + ")";
    }
}
