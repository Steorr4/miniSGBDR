import java.nio.ByteBuffer;
import java.util.List;

public class Relation {

    private String name;
    private int nbCol;
    private List<ColInfo> colonnes;

    public int writeRecordToBuffer(Record rec, CustomBuffer buff, int pos){

        int t;
        int ptPos = 0;
        int total = 4*(nbCol+1);
        ByteBuffer bb = buff.getBb();
        for(int i = 0;i<nbCol;i++){
            t = colonnes.get(i).getTypeCol().getTaille();
            total += t;
            ptPos = pos+(nbCol+1-i)*4+t;
            bb.putInt(ptPos);

            Type typeCol = colonnes.get(i).getTypeCol();
            if (typeCol instanceof TypeNonParam){
                if(((TypeNonParam) typeCol).getType() == TypeNonParam.ETypeNonParam.INT){
                   bb.putInt(pos, Integer.parseInt(rec.getVal().get(i)));
                }
                else{
                    bb.putFloat(pos, Float.parseFloat(rec.getVal().get(i)));
                }
            }else{
                for(int j = 0;j<t;j++ ){
                    bb.putChar(pos+j, rec.getVal().get(i).charAt(j));
                }
            }

        } //TODO: !

        return total;
    }

    public int readFromBuffer(Record rec, CustomBuffer buff, int pos){

        return 0;
    }


}
