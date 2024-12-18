package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;
import fr.upc.mi.bdda.FileAccess.Relation;

import java.util.ArrayList;
import java.util.List;

/**
 * Iterateur de projection.
 * <br/>(Voir TP7-B3 pour comprendre le fonctionnement plus en détail)
 */
public class ProjectOperator implements IRecordIterator{

    boolean isClosed;
    private int cursor;
    List<Record> records;
    private IRecordIterator opFils; // Selection

    /**
     * Main constructor
     *
     * @param select operateurs de selection.
     * @param cols indices de colonnes a projeter.
     */
    public ProjectOperator(SelectOperator select, int[]cols) {
        isClosed = false;
        cursor = 0;
        records = new ArrayList<>();
        opFils = select;

        Record next;
        List<String> val;
        while ((next=opFils.getNextRecord()) != null){
            val = new ArrayList<>();
            for(int i : cols){
                val.add(next.getVal().get(i));
            }
            records.add(new Record(val));
        }
    }

    /**
     * Renvois le prochain record correspondant a la projection.
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
     * Remet le cursor a 0.
     */
    @Override
    public void reset() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        cursor = 0;
    }
}
