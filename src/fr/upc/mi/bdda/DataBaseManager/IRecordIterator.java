package fr.upc.mi.bdda.DataBaseManager;

public interface IRecordIterator {

    Record getNextRecord();
    void close();
    void reset();

}
