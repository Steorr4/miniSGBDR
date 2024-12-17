package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;

public class RecordPrinter implements IRecordIterator{

    private boolean isClosed;
    private int totalRec;
    IRecordIterator iterator;


    public RecordPrinter(IRecordIterator iterator) {
        isClosed = false;
        totalRec = 0;
        this.iterator = iterator;
    }

    @Override
    public Record getNextRecord() {
        return iterator.getNextRecord();
    }

    @Override
    public void close() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        isClosed = true;
    }

    @Override
    public void reset() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        totalRec = 0;
    }

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
