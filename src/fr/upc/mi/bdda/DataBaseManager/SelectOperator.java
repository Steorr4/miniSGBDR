package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.FileAccess.Record;
import fr.upc.mi.bdda.FileAccess.Relation;
import fr.upc.mi.bdda.FileAccess.Type;
import fr.upc.mi.bdda.FileAccess.TypeNonParam;

import java.util.ArrayList;
import java.util.List;

public class SelectOperator implements IRecordIterator{

    private boolean isClosed;
    private int cursor;

    Relation relation;
    List<Record> records;
    List<Condition> conds;

    private IRecordIterator opFils; // RelationScanner

    public SelectOperator(Relation r, List<Condition> conds) {
        isClosed = false;
        cursor = 0;
        records = new ArrayList<>();
        relation = r;
        this.conds = conds;
        opFils = new RelationScanner(r);
    }

    @Override
    public Record getNextRecord() {
        if (isClosed) throw new RuntimeException("Iterator closed.");

        if (cursor < records.size()) {
            return records.get(cursor++);
        }

        boolean isCondTrue;
        Record next;

        while ((next = opFils.getNextRecord()) != null) {
            isCondTrue = true;

            if(conds == null){
                records.add(next);
                cursor++;
                return next;
            }

            for (Condition cond : conds) {
                Type typeCol = relation.getColonnes().get(cond.getIndiceCol1()).getTypeCol();
                String value1 = next.getVal().get(cond.getIndiceCol1());
                String value2;
                if(cond.getVal()==null){
                    value2 = next.getVal().get(cond.getIndiceCol2());
                }else{
                    value2 = cond.getVal();
                }

                switch (cond.getOp()) {
                    case "=" -> {
                        if (typeCol instanceof TypeNonParam) {
                            if (Integer.parseInt(value1) != Integer.parseInt(value2))
                                isCondTrue = false;
                        } else {
                            if (!value1.equals(value2))
                                isCondTrue = false;
                        }
                    }
                    case "<" -> {
                        if (typeCol instanceof TypeNonParam) {
                            if (Integer.parseInt(value1) >= Integer.parseInt(value2))
                                isCondTrue = false;
                        } else {
                            if (value1.compareTo(value2) < 0)
                                isCondTrue = false;
                        }
                    }
                    case ">" -> {
                        if (typeCol instanceof TypeNonParam) {
                            if (Integer.parseInt(value1) <= Integer.parseInt(value2))
                                isCondTrue = false;
                        } else {
                            if (value1.compareTo(value2) > 0)
                                isCondTrue = false;
                        }
                    }
                    case "<=" -> {
                        if (typeCol instanceof TypeNonParam) {
                            if (Integer.parseInt(value1) > Integer.parseInt(value2))
                                isCondTrue = false;
                        } else {
                            if (value1.compareTo(value2) <= 0)
                                isCondTrue = false;
                        }
                    }
                    case ">=" -> {
                        if (typeCol instanceof TypeNonParam) {
                            if (Integer.parseInt(value1) < Integer.parseInt(value2))
                                isCondTrue = false;
                        } else {
                            if (value1.compareTo(value2) >= 0)
                                isCondTrue = false;
                        }
                    }
                    case "<>" -> {
                        if (typeCol instanceof TypeNonParam) {
                            if (Integer.parseInt(value1) == Integer.parseInt(value2))
                                isCondTrue = false;
                        } else {
                            if (value1.equals(value2))
                                isCondTrue = false;
                        }
                    }
                }

                if (!isCondTrue) break;
            }

            if (isCondTrue) {
                records.add(next);
                cursor++;
                return next;
            }
        }
        return null;
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
