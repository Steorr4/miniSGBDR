package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;

/**
 * TODO
 */
public interface IRecordIterator {

    /**
     * TODO
     *
     * @return
     */
    Record getNextRecord();

    /**
     * TODO
     */
    void close();

    /**
     * TODO
     */
    void reset();

}
