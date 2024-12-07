package fr.upc.mi.bdda.FileAccess;

/**
 * Classe pour représenter un type paramétré dans une table.
 */
public class TypeParam extends Type {
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
}
