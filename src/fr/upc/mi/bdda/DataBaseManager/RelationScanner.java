package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.FileAccess.Relation;
import fr.upc.mi.bdda.FileAccess.Record;

import java.util.List;

/**
 * TODO
 */
public class RelationScanner implements IRecordIterator{

    private boolean isClosed;
    private int cursor;

    private List<Record> records;

    /**
     * TODO
     *
     * @param relation
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
     * TODO
     *
     * @return
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
     * TODO
     */
    @Override
    public void close() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        isClosed = true;
    }

    /**
     * TODO
     */
    @Override
    public void reset() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        cursor = 0;
    }
}
