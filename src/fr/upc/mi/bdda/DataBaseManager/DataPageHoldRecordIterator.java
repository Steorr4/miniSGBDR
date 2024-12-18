package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.BufferManager.CustomBuffer;
import fr.upc.mi.bdda.DiskManager.PageId;
import fr.upc.mi.bdda.FileAccess.Record;
import fr.upc.mi.bdda.FileAccess.Relation;

import java.util.ArrayList;

public class DataPageHoldRecordIterator implements IRecordIterator{
    private int cursor;
    private boolean isClosed;
    private Relation r;
    private CustomBuffer buffer;
    private BufferManager bm;
    private int nbSlot;

    public DataPageHoldRecordIterator(Relation r, PageId pid, BufferManager bm) throws BufferManager.BufferCountExcededException {
        this.r = r;
        this.bm = bm;
        isClosed = false;

        buffer = bm.getPage(pid);
        nbSlot = buffer.getInt(bm.getConfig().getPagesize()-8);
        cursor = 0;
    }

    @Override
    public Record getNextRecord() {
        if(cursor < nbSlot){
            int debRec = buffer.getInt(bm.getConfig().getPagesize()-8*(cursor+2));
            cursor++;
            Record rec = new Record(new ArrayList<>(r.getNbCol()));
            r.readFromBuffer(rec, buffer, debRec);
            return rec;
        }else{
            return null;
        }
    }

    @Override
    public void close() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        isClosed = true;
        bm.freePage(buffer.getPid(),false);
    }

    @Override
    public void reset() {
        cursor = 0;
    }

}
