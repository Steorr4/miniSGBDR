package fr.upc.mi.bdda.FileAccess;

import java.io.Serializable;

/**
 * Classe pour représenter un type non paramétré dans une table.
 */
public class TypeNonParam extends Type implements Serializable {
    ETypeNonParam type;

    /**
     * Enumérations des types non paramètrés possibles.
     */
    public enum ETypeNonParam implements Serializable{
        INT,
        REAL;
    }

    /**
     * Main constructor.
     *
     * @param type le type non paramètré.
     */
    public TypeNonParam(ETypeNonParam type) {
        super(4);
        this.type = type;
    }

    //Getter
    public ETypeNonParam getType() {
        return type;
    }

    //toString
    @Override
    public String toString() {
        if(type == ETypeNonParam.INT) return "INT";
        return "FLOAT";
    }
}
