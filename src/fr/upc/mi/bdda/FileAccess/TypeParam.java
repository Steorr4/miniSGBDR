package fr.upc.mi.bdda.FileAccess;

public class TypeParam extends Type {
    ETypeParam type;

    public static enum ETypeParam{
        CHAR,
        VARCHAR
    }

    public TypeParam(int taille, ETypeParam type) {
        super(taille);
        this.type = type;
    }

    //Getter
    public ETypeParam getType() {
        return type;
    }
}
