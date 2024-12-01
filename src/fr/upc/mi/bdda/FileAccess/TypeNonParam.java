package fr.upc.mi.bdda.FileAccess;

/**
 * Classe pour représenter un type non paramétré dans une table.
 */
public class TypeNonParam extends Type {
    ETypeNonParam type;

    /**
     * Enumérations des types non paramètrés possibles.
     */
    public static enum ETypeNonParam{
        INT,
        REAL
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
}
