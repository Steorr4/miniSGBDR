package fr.upc.mi.bdda.FileAccess;

//Package
import fr.upc.mi.bdda.BufferManager.*;
import fr.upc.mi.bdda.DiskManager.*;

//JAVA Imports
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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
                    ptPos+=(typeCol.getTaille() );
                    total+= typeCol.getTaille() ;
                }
               if(((TypeParam) typeCol).getType() == TypeParam.ETypeParam.VARCHAR){
                   bb.put(ptPos, rec.getVal().get(i).getBytes());
                   ptPos+=(rec.getVal().get(i).length() );
                   total+=((rec.getVal().get(i).length()));
                }

            }

        }
        return total;
    }

    //TODO: Ca read mal les int
    public int readFromBuffer(Record rec, CustomBuffer buff, int pos) {
        ByteBuffer bb = buff.getBb();
        int total = 4 * (nbCol + 1);
        int p = pos; // position de déplacement

        for (int i = 0; i < nbCol; i++) {
            int ptPos = bb.getInt(p);
            p += 4;
            Type typeCol = colonnes.get(i).getTypeCol();

            if (typeCol instanceof TypeNonParam) {
                if (((TypeNonParam) typeCol).getType().equals(TypeNonParam.ETypeNonParam.INT)) {
                    rec.getVal().set(i, Integer.toString(bb.getInt(ptPos)));
                    total += typeCol.getTaille();
                    System.out.println(((Number) (bb.getInt(ptPos))).toString());
                } else {
                    rec.getVal().set(i, Double.toString(bb.getDouble(ptPos)));
                    total += typeCol.getTaille();
                }
            } else {
                int length = (typeCol instanceof TypeParam && ((TypeParam) typeCol).getType().equals(TypeParam.ETypeParam.VARCHAR))
                        ? rec.getVal().get(i).length()
                        : typeCol.getTaille();

                byte[] bytes = new byte[length];
                bb.position(ptPos);
                bb.get(bytes, 0, length);
                String value = new String(bytes, StandardCharsets.UTF_8);
                rec.getVal().set(i, value);
                total += length;
                System.out.println(value);
            }
        }
        return total;
    }

    //TD5 :
    // TODO : PAS LE DROIT DE FAIRE ACCES AU DiskManager READPAGE/WRITEPAGE
    public void addDataPage(){
        
    }

}
