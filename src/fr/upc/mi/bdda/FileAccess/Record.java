package fr.upc.mi.bdda.FileAccess;

//JAVA Imports
import java.util.List;

/**
 * Classe qui représente un tuple d'une relation.
 */
public class Record {
    private RecordID rid; // ID du tuple
    private List<String> val; // Valeur du tuple

    /**
     * Main constructor.
     *
     * @param rid l'ID du tuple.
     * @param val la valeur du tuple.
     */
    public Record(RecordID rid, List<String> val) {
        this.rid = rid;
        this.val = val;
    }

    //Getters & Setters
    public RecordID getRid() {
        return rid;
    }
    public void setRid(RecordID rid) {
        this.rid = rid;
    }
    public List<String> getVal() {
        return val;
    }
    public void setVal(List<String> val) {
        this.val = val;
    }

}
