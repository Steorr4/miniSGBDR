package fr.upc.mi.bdda.FileAccess;

public class TypeNonParam extends Type {
    ETypeNonParam type;

    public static enum ETypeNonParam{
        INT,
        REAL
    }

    public TypeNonParam(ETypeNonParam type) {
        super(4);
        this.type = type;
    }

    //Getter
    public ETypeNonParam getType() {
        return type;
    }
}
