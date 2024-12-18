package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.DiskManager.PageId;
import fr.upc.mi.bdda.FileAccess.Record;
import fr.upc.mi.bdda.FileAccess.Relation;
import fr.upc.mi.bdda.FileAccess.Type;
import fr.upc.mi.bdda.FileAccess.TypeNonParam;

import java.util.ArrayList;
import java.util.List;

public class PageOrientedJoinOperator implements IRecordIterator{

    private Relation r1;
    private Relation r2;
    private Record nextR1;
    private Record nextR2;
    private int cursorR1;
    private int cursorR2;

    private List<Condition> conds;
    private List<Record> records;
    private BufferManager bm;
    private DataPageHoldRecordIterator dhrpi1;
    private DataPageHoldRecordIterator dhrpi2;
    private PageDirectoryIterator pdi;

    public PageOrientedJoinOperator(Relation r1, Relation r2, List<Condition>conds, BufferManager bm) throws BufferManager.BufferCountExcededException {
        this.r1 = r1;
        this.r2 = r2;
        cursorR1 = cursorR2 = 0;
        this.conds = conds;

        dhrpi1 = dhrpi2 = null;
        pdi = new PageDirectoryIterator(r1,r2,bm);
        this.bm = bm;
    }

    @Override
    public Record getNextRecord() {
        try {

            PageId pid;
            //Debut de POJO.
            while(true) {
                if (dhrpi1 == null && dhrpi2 == null) {

                    pid = pdi.getNextDataPageId();
                    if (pid != null) {
                        dhrpi1 = new DataPageHoldRecordIterator(r1, pid, bm);
                        nextR1 = dhrpi1.getNextRecord();
                        cursorR1++;
                    } else throw new RuntimeException("Erreur Jointure.");

                    pid = pdi.getNextDataPageId();
                    if (pid != null) {
                        dhrpi2 = new DataPageHoldRecordIterator(r2, pid, bm);
                        nextR2 = dhrpi2.getNextRecord();
                        cursorR2++;
                    } else throw new RuntimeException("Erreur Jointure.");

                } else {

                    if ((nextR2 = dhrpi2.getNextRecord()) == null) { //Fin de records d'une page de R2
                        if ((nextR1 = dhrpi1.getNextRecord()) != null) {  //S'il reste des records de la page de R1
                            dhrpi2.reset();
                            nextR2 = dhrpi2.getNextRecord();
                            cursorR2++;
                        } else { //Fin records d'une page de R1
                            if (cursorR2 <= pdi.nbPageR2()) { //S'il reste une page de R2
                                dhrpi1.reset();
                                dhrpi2.close();
                                pid = pdi.getNextDataPageId();
                                dhrpi2 = new DataPageHoldRecordIterator(r2, pid, bm);
                                nextR2 = dhrpi2.getNextRecord();
                                cursorR2++;
                            } else { //Toutes les pages de R2 parcourues
                                dhrpi1.close();
                                pid = pdi.getNextDataPageId();
                                if (pid == null) {
                                    dhrpi2.close();
                                    return null; //Toutes les pages ont été parcourues.
                                }
                                dhrpi1 = new DataPageHoldRecordIterator(r1, pid, bm);
                                nextR1 = dhrpi1.getNextRecord();
                                cursorR1++;
                                pid = pdi.getNextDataPageId();
                                dhrpi2 = new DataPageHoldRecordIterator(r2, pid, bm);
                                nextR2 = dhrpi2.getNextRecord();
                                cursorR2 = 1;
                            }
                        }
                    }
                }

                //Iteration conditions
                boolean isCondTrue = true;
                List<String> values = new ArrayList<>();
                for (Condition cond : conds) {
                    Type typeCol = r1.getColonnes().get(cond.getIndiceCol1()).getTypeCol();
                    String value1 = nextR1.getVal().get(cond.getIndiceCol1());
                    String value2 = nextR2.getVal().get(cond.getIndiceCol2());
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
                    values.addAll(nextR1.getVal());
                    values.addAll(nextR2.getVal());
                    return new Record(values);
                }
            }
        } catch (BufferManager.BufferCountExcededException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void reset() {

    }
}
