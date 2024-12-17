package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;

public interface IRecordIterator {

    Record getNextRecord();
    void close();
    void reset();

}
