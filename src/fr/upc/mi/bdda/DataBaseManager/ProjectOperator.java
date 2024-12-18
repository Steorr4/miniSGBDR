package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;
import fr.upc.mi.bdda.FileAccess.Relation;

import java.util.ArrayList;
import java.util.List;

public class ProjectOperator implements IRecordIterator{

    boolean isClosed;
    private int cursor;
    List<Record> records;
    private IRecordIterator opFils; // Selection

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

    @Override
    public void close() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        isClosed = true;
    }

    @Override
    public void reset() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        cursor = 0;
    }
}
