package fr.upc.mi.bdda.Relations;

public class TypeParam extends Type {
    ETypeParam type;

    public enum ETypeParam{
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
