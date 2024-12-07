package fr.upc.mi.bdda.FileAccess;

//Package
import fr.upc.mi.bdda.BufferManager.*;
import fr.upc.mi.bdda.DiskManager.*;

//JAVA Imports
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

        //ByteBuffer bb = buff.getBb(); // On récupère le buffer pour ecrire dedans

        int total= 4*(nbCol+1); // Reservation de la table de pointage de la relation
        int ptPos = pos+total; // Pointeur de la position courante qui commence apres la talbe de pointage

        for(int i = 0; i<nbCol; i++){

            buff.putInt(pos+4*i, ptPos);
            Type type = colonnes.get(i).getTypeCol();

            if(type instanceof TypeNonParam){
                switch (((TypeNonParam) type).getType()){

                    case INT:
                        buff.putInt(ptPos, Integer.parseInt(rec.getVal().get(i)));
                        total += type.getTaille();
                        break;

                    case REAL:
                        buff.putFloat(ptPos, Float.parseFloat(rec.getVal().get(i)));
                        total += type.getTaille();
                        break;
                }
                ptPos += 4;

            }else {
                switch (((TypeParam) type).getType()){
                    case CHAR:
                        StringBuilder sb = new StringBuilder(rec.getVal().get(i));
                        for(int j = rec.getVal().get(i).length(); j< type.getTaille();j++) sb.append(" ");

                        buff.putBytes(ptPos, sb.toString().getBytes(StandardCharsets.UTF_8));

                        total += type.getTaille();
                        ptPos += type.getTaille();
                        break;

                    case VARCHAR:
                        buff.putBytes(ptPos, rec.getVal().get(i).getBytes());
                        ptPos += rec.getVal().get(i).length();
                        total += rec.getVal().get(i).length();
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

        int total= 4*(nbCol+1);
        int ptPos;
        List<String> listVal = rec.getVal();

        for(int i = 0; i<nbCol; i++){

            Type type = colonnes.get(i).getTypeCol();

            if(type instanceof TypeNonParam){
                switch (((TypeNonParam) type).getType()){
                    case INT:
                        ptPos = buff.getInt(pos);
                        pos+=4;

                        listVal.set(i,Integer.toString(buff.getInt(ptPos)));
                        total+=4;
                        break;

                    case REAL:
                        ptPos = buff.getInt(pos);
                        pos+=4;

                        listVal.set(i,Float.toString(buff.getFloat(ptPos)));
                        total+=4;
                        break;
                }
            }else {
                ptPos = buff.getInt(pos);
                pos+=4;

                int length = buff.getInt(pos)-ptPos;
                byte[] b = new byte[length];
                buff.setPos(ptPos);
                buff.getBytes(b,0,length);

                listVal.set(i,new String(b, StandardCharsets.UTF_8));
                total+=length;
            }

        }
        return total;
    }

    //TD5 :
    /**
     * TODO
     */
    private void addDataPage(){
        //TODO : besoin de creer une nouvelle header page si implementation du chainage
        try {

            PageId pid = dm.AllocPage();
            CustomBuffer buffer = bm.getPage(headerPageID);
            int indice = buffer.getInt(0);

            buffer.putInt(indice*12+4, pid.getFileIdx());
            buffer.putInt(indice*12+8, pid.getPageIdx());
            buffer.putInt(bm.getConfig().getPagesize());

            buffer.putInt(indice+1);

            bm.freePage(headerPageID, true);

        } catch (IOException | BufferManager.BufferCountExcededException e) {
            throw new RuntimeException(e);
        }
    }

    private PageId getFreeDataPage(int sizeRecord) throws BufferManager.BufferCountExcededException {
        CustomBuffer buffer = bm.getPage(headerPageID);

        int indice = buffer.getInt(0);

        for(int i=0; i<indice; i++){
            PageId pid = new PageId(buffer.getInt(indice*12+4), buffer.getInt(indice*12+8));

            if(buffer.getInt(indice*12+12)>=sizeRecord){
                buffer.setPos(0);
                bm.freePage(headerPageID, false);
                return pid;
            }
        }
        buffer.setPos(0);
        bm.freePage(headerPageID, false);
        return null;
    }

    /**
     * TODO
     *
     * @param record
     * @param pid
     * @return
     * @throws BufferManager.BufferCountExcededException
     */
    private RecordID writeRecordToDataPage(Record record, PageId pid) throws BufferManager.BufferCountExcededException {

        CustomBuffer buffer = bm.getPage(pid);

        int debRec = buffer.getInt(bm.getConfig().getPagesize()-4);

        int taille = writeRecordToBuffer(record, buffer, debRec);
        buffer.putInt(bm.getConfig().getPagesize()-4, debRec+taille);
        int nbSlot = buffer.getInt(bm.getConfig().getPagesize()-8);

        buffer.putInt(bm.getConfig().getPagesize()-(nbSlot+1)*8, debRec);
        buffer.putInt(taille);

        buffer.putInt(bm.getConfig().getPagesize()-8,++nbSlot);

        record.setRid(new RecordID(pid,nbSlot));

        buffer.setPos(0);
        bm.freePage(pid,true);
        return record.getRid();

    }

    /**
     * TODO
     *
     * @param pid
     * @return
     * @throws BufferManager.BufferCountExcededException
     */
    private List<Record> getRecordsInDataPage(PageId pid) throws BufferManager.BufferCountExcededException {

        CustomBuffer buffer = bm.getPage(pid);

        int nbRecords = buffer.getInt(bm.getConfig().getPagesize() - 8);
        List<Record> recordList = new ArrayList<>(nbRecords);
        int debRec, lenghtRec;

        for (int i = 2; i <= nbRecords+1; i++){

            debRec = buffer.getInt(bm.getConfig().getPagesize()-8*i);
            Record rec = new Record(new ArrayList<>(nbCol));
            readFromBuffer(rec, buffer, debRec);
            recordList.add(rec);

        }

        buffer.setPos(0);
        bm.freePage(pid,false);
        return recordList;
    }

    /**
     * TODO
     *
     * @return
     * @throws BufferManager.BufferCountExcededException
     */
    private List<PageId> getDataPages() throws BufferManager.BufferCountExcededException {

        CustomBuffer buffer = bm.getPage(headerPageID);
        int nbPage =  buffer.getInt(0);
        List<PageId> pids = new ArrayList<>(nbPage);

        for(int i = 0; i < nbPage; i++){

            PageId pid = new PageId(buffer.getInt(i*12+4), buffer.getInt(i*12+8));
            pids.add(pid);

        }
        buffer.setPos(0);
        bm.freePage(headerPageID, false);
        return pids;
    }

    /**
     * TODO
     *
     * @param rec
     * @return
     * @throws BufferManager.BufferCountExcededException
     */
    public RecordID insertRecord(Record rec) throws BufferManager.BufferCountExcededException {

        int tailleRec = 4*(nbCol+1);
        for (int i = 0; i < nbCol; i++){
            Type type = colonnes.get(i).getTypeCol();
            if (type instanceof TypeNonParam){
                tailleRec += 4;
            }else{
                tailleRec += rec.getVal().get(i).length();
            }
        }

        PageId pid = getFreeDataPage(tailleRec);
        if (pid == null){
            addDataPage();
            pid = getFreeDataPage(tailleRec);
        }

        return writeRecordToDataPage(rec, pid);
    }

    /**
     * TODO
     *
     * @return
     * @throws BufferManager.BufferCountExcededException
     */
    public List<Record> getAllRecords() throws BufferManager.BufferCountExcededException {
        List<PageId> dataPages = getDataPages();
        List<Record> records = new ArrayList<>();

        for(PageId pid : dataPages){
            List<Record> recPage = getRecordsInDataPage(pid);
            records.addAll(recPage);
        }
        return records;
    }

}
