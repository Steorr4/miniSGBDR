import java.nio.ByteBuffer;
import java.util.List;

public class Relation {

    private String name;
    private int nbCol;
    private List<ColInfo> colonnes;

    public int writeRecordToBuffer(Record rec, CustomBuffer buff, int pos){

        int total= 4*(nbCol+1); // Reservation de la table de pointage de la relation
        int ptPos = pos+total; // Pointeur de la position courante qui commence apres la talbe de pointage
        ByteBuffer bb = buff.getBb(); // On recupere le buffer pour ecrire dedans

        for(int i = 0; i<nbCol; i++){

            Type typeCol = colonnes.get(i).getTypeCol();

            pos+= 4*i;
            bb.putInt(pos,ptPos);

            if(typeCol instanceof TypeNonParam){
                if(((TypeNonParam) typeCol).getType() == TypeNonParam.ETypeNonParam.INT){
                    bb.putInt(ptPos, Integer.parseInt(rec.getVal().get(i)));
                    total += typeCol.getTaille();

                }
                if(((TypeNonParam) typeCol).getType() == TypeNonParam.ETypeNonParam.REAL){
                    bb.putDouble(ptPos, Double.parseDouble(rec.getVal().get(i)));
                    total += typeCol.getTaille();
                }
                ptPos+=typeCol.getTaille();
            }
            else{
                if (((TypeParam) typeCol).getType() == TypeParam.ETypeParam.CHAR){
                    bb.put(ptPos, rec.getVal().get(i).getBytes());
                    ptPos+=typeCol.getTaille();
                    total+= typeCol.getTaille();
                }
               if(((TypeParam) typeCol).getType() == TypeParam.ETypeParam.VARCHAR){
                   bb.put(ptPos, rec.getVal().get(i).getBytes());
                   ptPos+=rec.getVal().get(i).length();
                   total+=rec.getVal().get(i).length();
                }

            }

        }
        return total;
    }

    public int readFromBuffer(Record rec, CustomBuffer buff, int pos){
        //recupere le buffer du buff
        //byte[] bArray = buff.getBb().array();
        ByteBuffer bb= buff.getBb();
        int total=4*(nbCol+1);
        int p=pos;//position de deplacement
        StringBuilder sb;
        for(int i=0;i<nbCol;i++){
            int ptPos=bb.getInt(p);
            p+=4;
            int ptPosSui=bb.getInt(p);
            total+=(ptPosSui-ptPos);
            Type typeCol = colonnes.get(i).getTypeCol();
            if(typeCol instanceof TypeNonParam){

                rec.getVal().set(i,((Number)(bb.getInt(ptPos))).toString());
            }
            else{
                sb=new StringBuilder();
                for(int c=ptPos;c<ptPosSui;c++){
                    sb.append(bb.getChar(c));
                }
                rec.getVal().set(i,sb.toString());
            }
        }
        return 0;
    }


}
