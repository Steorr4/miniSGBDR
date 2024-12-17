package fr.upc.mi.bdda.FileAccess;

//Package
import fr.upc.mi.bdda.BufferManager.*;
import fr.upc.mi.bdda.DiskManager.*;

//JAVA Imports
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour représenter une table (relation) et les diverses opérations pouvant y être aplliqués
 * (Rajout/Supression/Consultation des tuples).
 * </br>(Voir TP4-A pour comprendre le fonctionnement plus en détail)
 */
public class Relation implements Serializable {

    private String name; // Nom de la table
    private int nbCol; // Nombre de colonnes de la table
    private List<ColInfo> colonnes; // Liste des colonnes de la table
    private PageId headerPageID; // Pointeur vers la HeaderPage

    private transient DiskManager dm;
    private transient BufferManager bm;

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
                        buff.putBytes(ptPos, rec.getVal().get(i).getBytes(StandardCharsets.UTF_8));
                        ptPos += rec.getVal().get(i).length();
                        total += rec.getVal().get(i).length();
                }
            }
        }
        buff.putInt(pos+4*nbCol, ptPos);
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

                        listVal.add(i,Integer.toString(buff.getInt(ptPos)));
                        total+=4;
                        break;

                    case REAL:
                        ptPos = buff.getInt(pos);
                        pos+=4;

                        listVal.add(i,Float.toString(buff.getFloat(ptPos)));
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

                listVal.add(i,new String(b, StandardCharsets.UTF_8));
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

            PageId pid = dm.allocPage(); //Alloc new page pid
            CustomBuffer buffer = bm.getPage(headerPageID);
            int indice = buffer.getInt(0);

            buffer.putInt(indice*12+4, pid.getFileIdx());
            buffer.putInt(indice*12+8, pid.getPageIdx());
            buffer.putInt(indice*12+12, bm.getConfig().getPagesize()-8);
            buffer.putInt(0,indice+1);

            bm.freePage(headerPageID, true);

            buffer = bm.getPage(pid);
            buffer.putInt(bm.getConfig().getPagesize()-4,0);
            buffer.putInt(bm.getConfig().getPagesize()-8,0);
            bm.freePage(pid,true);

        } catch (IOException | BufferManager.BufferCountExcededException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO
     *
     * @param sizeRecord
     * @return
     * @throws BufferManager.BufferCountExcededException
     */
    private PageId getFreeDataPage(int sizeRecord) throws BufferManager.BufferCountExcededException {
        CustomBuffer buffer = bm.getPage(headerPageID);

        int indice = buffer.getInt(0);

        if(indice == 0){
            addDataPage();
            indice = buffer.getInt(0);
        }

        for(int i=0; i<indice; i++){
            int freeSpace = bm.getConfig().getPagesize() - buffer.getInt(bm.getConfig().getPagesize()-4) -
                    (buffer.getInt(bm.getConfig().getPagesize()-8)+1)*8; // recup nb octets libres.
            if (freeSpace >= sizeRecord+8) {
                buffer.putInt((i+1) * 12,freeSpace-sizeRecord);
                PageId pid = new PageId(buffer.getInt(i*12+4), buffer.getInt(i*12+8));
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

        int debRec = buffer.getInt(bm.getConfig().getPagesize()-4); //nb deb espace libre

        int taille = writeRecordToBuffer(record, buffer, debRec); //taille apres du record
        buffer.putInt(bm.getConfig().getPagesize()-4, debRec+taille); //MaJ pos debEspaceLibre
        int nbSlot = buffer.getInt(bm.getConfig().getPagesize()-8); //nb entrée de slot

        buffer.putInt(bm.getConfig().getPagesize()-(nbSlot+2)*8, debRec); //ajout deb record
        buffer.putInt(bm.getConfig().getPagesize()-(nbSlot+2)*8+4,taille); //ajout taille rec

        nbSlot++;
        buffer.putInt(bm.getConfig().getPagesize()-8,nbSlot);

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
        int debRec;

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
        //TODO bizarre
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
            if(pid == null){
                throw new RuntimeException("Failed to allocate page");
            }
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

    public String getName() {
        return name;
    }

    public List<ColInfo> getColonnes() {
        return colonnes;
    }

    public int getNbCol() {
        return nbCol;
    }

    public void setDm(DiskManager dm) {
        this.dm = dm;
    }

    public void setBm(BufferManager bm) {
        this.bm = bm;
    }

    public int getColIndex(String nomCol){
        int cpt = 0;
        for (ColInfo col : colonnes){
            if(col.getNomCol().equals(nomCol)) return cpt;
            cpt++;
        }
        throw new RuntimeException("Colonne non existante.");
    }
}
