public class TypeNonParam extends Type{
    ETypeNonParam type;

    public enum ETypeNonParam{
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
