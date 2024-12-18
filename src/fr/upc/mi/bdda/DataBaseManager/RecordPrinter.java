package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;

/**
 * Printeur d'iterateur.
 * <br/>(Voir TP7-B6 pour comprendre le fonctionnement plus en détail)
 */
public class RecordPrinter implements IRecordIterator{

    private boolean isClosed;
    private int totalRec;
    IRecordIterator iterator;

    /**
     * Main constructor.
     *
     * @param iterator l'iterateur a afficher.
     */
    public RecordPrinter(IRecordIterator iterator) {
        isClosed = false;
        totalRec = 0;
        this.iterator = iterator;
    }

    /**
     * Recupere le prochain record retourné par l'iterateur.
     *
     * @return un record
     */
    @Override
    public Record getNextRecord() {
        return iterator.getNextRecord();
    }

    /**
     * Ferme l'iterateur.
     */
    @Override
    public void close() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        isClosed = true;
    }

    /**
     * Remet le curseur à 0.
     */
    @Override
    public void reset() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        totalRec = 0;
    }

    /**
     * Affiche les tuples de renvoyés par l'itérateur.
     */
    public void print(){
        this.reset();
        StringBuilder sb;
        Record next;
        while ((next=iterator.getNextRecord())!=null){
            sb  = new StringBuilder();
            for(String values : next.getVal()){
                sb.append(values).append(" ; ");
            }
            sb.deleteCharAt(sb.length()-1).deleteCharAt(sb.length()-1);
            System.out.println(sb);
            totalRec++;
        }
        System.out.println("Total records = "+totalRec);
    }
}
