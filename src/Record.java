import java.util.List;

public class Record {
    private RecordID rid;
    private List<String> val;

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
