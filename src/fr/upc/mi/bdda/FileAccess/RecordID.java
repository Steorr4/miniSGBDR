package fr.upc.mi.bdda.FileAccess;

//Package
import fr.upc.mi.bdda.DiskManager.PageId;

public class RecordID {
    private PageId pid;
    private int slot;

    public RecordID(PageId pid, int slot) {
        this.pid = pid;
        this.slot = slot;
    }
}
