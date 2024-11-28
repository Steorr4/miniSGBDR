package fr.upc.mi.bdda.Relations;

//Package
import fr.upc.mi.bdda.BufferManager.*;
import fr.upc.mi.bdda.DiskManager.*;

//JAVA Imports
import java.nio.ByteBuffer;
import java.util.List;

public class Relation {

    private String name;
    private int nbCol;
    private List<ColInfo> colonnes;
    private PageId headerPageID;
    private BufferManager bm;
    private DiskManager dm;

    public Relation(String name, int nbCol, List<ColInfo> colonnes, PageId headerPageID, BufferManager bm, DiskManager dm) {
        this.name = name;
        this.nbCol = nbCol;
        this.colonnes = colonnes;
        this.headerPageID = headerPageID;
        this.bm = bm;
        this.dm = dm;
    }

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
                    ptPos+=(typeCol.getTaille() *2);
                    total+= typeCol.getTaille() *2;
                }
               if(((TypeParam) typeCol).getType() == TypeParam.ETypeParam.VARCHAR){
                   bb.put(ptPos, rec.getVal().get(i).getBytes());
                   ptPos+=(rec.getVal().get(i).length() *2);
                   total+=((rec.getVal().get(i).length())*2);
                }

            }

        }
        return total;
    }

    //TODO: Y'a 7aja dayi
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
                if(((TypeNonParam)typeCol).getType().equals(TypeNonParam.ETypeNonParam.INT) ){
                    rec.getVal().set(i, ((Number) (bb.getInt(ptPos))).toString());
                }
                else{
                    rec.getVal().set(i, ((Number) (bb.getDouble(ptPos))).toString());
                }
            }
            else{
                sb=new StringBuilder();
                for(int c=ptPos;c<ptPosSui;c++){
                    sb.append(bb.getChar(c));
                }
                System.out.println(sb);
                rec.getVal().set(i, sb.toString());
                System.out.println(sb);
            }

        }
        return total;
    }

    //TD5 :
    // TODO : PAS LE DROIT DE FAIRE ACCES AU DiskManager READPAGE/WRITEPAGE
    public void addDataPage(){
        
    }

}
