package fr.upc.mi.bdda.FileAccess;

//Package
import fr.upc.mi.bdda.BufferManager.*;
import fr.upc.mi.bdda.DiskManager.*;

//JAVA Imports
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Classe pour représenter une table (relation) et les diverses opérations pouvant y être aplliqués
 * (Rajout/Supression/Consultation des tuples).
 * </br>(Voir TP4-A pour comprendre le fonctionnement plus en détail)
 */
public class Relation {

    private String name; // Nom de la table
    private int nbCol; // Nombre de colonnes de la table
    private List<ColInfo> colonnes; // Liste des colonnes de la table
    private PageId headerPageID; // Pointeur vers la HeaderPage

    private final DiskManager dm;
    private final BufferManager bm;

    /**
     * Main constructor.
     *
     * @param name le nom de la table.
     * @param colonnes la liste des colonnes dans la table.
     * @param headerPageID l'identifiant de la HeaderPage de la relation.
     * @param dm l'instance du DiskManager.
     * @param bm l'instance du BufferManager.
     */
    public Relation(String name, List<ColInfo> colonnes,
                    PageId headerPageID,DiskManager dm, BufferManager bm) {

        this.name = name;
        this.colonnes = colonnes;
        this.nbCol = colonnes.size();
        this.headerPageID = headerPageID;
        this.dm = dm;
        this.bm = bm;
    }

    /**
     * Permet d'écrire un tuple dans un Buffer à partir d'une certaine position.
     * Un offset_directory est également inscrit au début de l'espace alloué au tuple afin de pointer
     * sur les différentes valeurs des colonnes dans le cas où les types sont de taille variable.
     *
     * @param rec le tuple à enregistrer.
     * @param buff le Buffer dans lequel écrire.
     * @param pos la position relative dans le buffer.
     * @return la taille totale occupée par le tuple (offset_directory compris).
     */
    public int writeRecordToBuffer(Record rec, CustomBuffer buff, int pos){

        ByteBuffer bb = buff.getBb(); // On récupère le buffer pour ecrire dedans

        int total= 4*(nbCol+1); // Reservation de la table de pointage de la relation
        int ptPos = pos+total; // Pointeur de la position courante qui commence apres la talbe de pointage

        for(int i = 0; i<nbCol; i++){

            bb.putInt(pos+4*i,ptPos);
            Type type = colonnes.get(i).getTypeCol();

            if(type instanceof TypeNonParam){
                switch (((TypeNonParam) type).getType()){

                    case INT:
                        bb.putInt(ptPos, Integer.parseInt(rec.getVal().get(i)));
                        total += type.getTaille();
                        break;

                    case REAL:
                        bb.putDouble(ptPos, Double.parseDouble(rec.getVal().get(i)));
                        total += type.getTaille();
                        break;
                }
                ptPos += 4;

            }else {
                switch (((TypeParam) type).getType()){
                    case CHAR:
                        StringBuilder sb = new StringBuilder(rec.getVal().get(i));
                        for(int j = rec.getVal().get(i).length(); j< type.getTaille();j++) sb.append(" ");

                        bb.put(ptPos, sb.toString().getBytes());

                        total += type.getTaille();
                        ptPos += type.getTaille();
                        break;

                    case VARCHAR:
                        bb.put(ptPos, rec.getVal().get(i).getBytes());
                        ptPos+=rec.getVal().get(i).length();
                        total+=rec.getVal().get(i).length();
                }
            }
        }
        return total;
    }

    /**
     * Permet de lire un tuple sur le Buffer et de convertir en Record.
     *
     * @param rec un record dans les valeurs sont vides.
     * @param buff le Buffer contenant le tuple.
     * @param pos la position du tuple dans le buffer.
     * @return la taille totale occupée par le tuple (offset_directory compris).
     */
    public int readFromBuffer(Record rec, CustomBuffer buff, int pos) {

        ByteBuffer bb = buff.getBb();

        int total= 4*(nbCol+1);
        int ptPos;
        List<String> listVal = rec.getVal();

        for(int i = 0; i<nbCol; i++){

            Type type = colonnes.get(i).getTypeCol();

            if(type instanceof TypeNonParam){
                switch (((TypeNonParam) type).getType()){
                    case INT:
                        ptPos = bb.getInt(pos);
                        pos+=4;

                        listVal.set(i,Integer.toString(bb.getInt(ptPos)));
                        total+=4;
                        break;

                    case REAL:
                        ptPos = bb.getInt(pos);
                        pos+=4;

                        listVal.set(i,Double.toString(bb.getDouble(ptPos)));
                        total+=4;
                        break;
                }
            }else {
                ptPos = bb.getInt(pos);
                pos+=4;

                int length = bb.getInt(pos)-ptPos;
                byte[] b = new byte[length];
                bb.position(ptPos);
                bb.get(b,0,length);

                listVal.set(i,new String(b, StandardCharsets.UTF_8));
                total+=length;
            }

        }
        return total;
    }

    //TD5 :
    // TODO : PAS LE DROIT DE FAIRE ACCES AU DiskManager READPAGE/WRITEPAGE
    public void addDataPage(){
        
    }

}
