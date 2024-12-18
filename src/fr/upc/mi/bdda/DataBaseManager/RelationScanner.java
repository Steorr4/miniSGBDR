package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.FileAccess.Relation;
import fr.upc.mi.bdda.FileAccess.Record;

import java.util.List;

/**
 * Scanner scequentiel de relation.
 * <br/>(Voir TP7-B5 pour comprendre le fonctionnement plus en détail)
 */
public class RelationScanner implements IRecordIterator{

    private boolean isClosed;
    private int cursor;

    private List<Record> records;

    /**
     * Main constructor.
     *
     * @param relation une relation R.
     */
    public RelationScanner(Relation relation){
        isClosed = false;
        cursor=0;
        try {
            records = relation.getAllRecords();
        } catch (BufferManager.BufferCountExcededException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Renvois le prochain tuple de la relation.
     *
     * @return un tuple.
     */
    @Override
    public Record getNextRecord() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        if(cursor >= records.size()){
            return null;
        }
        else {
            return records.get(cursor++);
        }
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
        cursor = 0;
    }
}
