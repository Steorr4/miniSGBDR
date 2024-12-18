package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.BufferManager.CustomBuffer;
import fr.upc.mi.bdda.DiskManager.PageId;
import fr.upc.mi.bdda.FileAccess.Relation;

import java.util.ArrayList;
import java.util.List;

public class PageDirectoryIterator{
    private int cursor1;
    private int cursor2;
    private List<PageId> pid1;
    private List<PageId> pid2;

    public PageDirectoryIterator(Relation r1, Relation r2, BufferManager bm) throws BufferManager.BufferCountExcededException {
        pid1 = new ArrayList<>();
        pid2 = new ArrayList<>();
        cursor1 = cursor2 = 0;

        CustomBuffer hp1Buffer = bm.getPage(r1.getHeaderPageID());
        int nbPage1 = hp1Buffer.getInt(0);
        for(int i = 0; i<nbPage1; i++){
            pid1.add(new PageId(hp1Buffer.getInt(i*12+4),hp1Buffer.getInt(i*12+8)));
        }
        bm.freePage(r1.getHeaderPageID(),false);

        CustomBuffer hp2Buffer = bm.getPage(r2.getHeaderPageID());
        int nbPage2 = hp2Buffer.getInt(0);
        for(int i = 0; i<nbPage2; i++){
            pid2.add(new PageId(hp2Buffer.getInt(i*12+4),hp2Buffer.getInt(i*12+8)));
        }
        bm.freePage(r2.getHeaderPageID(),false);
    }

    public PageId getNextDataPageId() {

        if (cursor1 == 0) {
            return pid1.get(cursor1++);
        } else if (cursor2 < pid2.size()) {
            return pid2.get(cursor2++);
        } else {
            if (cursor1 < pid1.size()){
                cursor2 = 0;
                return pid1.get(cursor1++);
            }else{
                return null;
            }
        }
    }

    public int nbPageR1(){
        return pid1.size();
    }

    public int nbPageR2(){
        return pid2.size();
    }

}
