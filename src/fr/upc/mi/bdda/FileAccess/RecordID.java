package fr.upc.mi.bdda.FileAccess;

//Package
import fr.upc.mi.bdda.DiskManager.PageId;

/**
 * Classe qui pointe sur la position d'un record.
 */
public class RecordID {
    private PageId pid; // l'ID de sa page
    private int slotIdx; // l'indice de la case ou le record sera rangé

    /**
     * Main contructor TODO (surement à modifier à l'implementation du TD5)
     *
     * @param pid l'ID de sa page.
     * @param slotIdx l'indice de sa case.
     */
    public RecordID(PageId pid, int slotIdx) {
        this.pid = pid;
        this.slotIdx = slotIdx;
    }
}
