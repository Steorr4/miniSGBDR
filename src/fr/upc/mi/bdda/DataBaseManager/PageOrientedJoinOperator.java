package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.DiskManager.PageId;
import fr.upc.mi.bdda.FileAccess.Record;
import fr.upc.mi.bdda.FileAccess.Relation;
import fr.upc.mi.bdda.FileAccess.Type;
import fr.upc.mi.bdda.FileAccess.TypeNonParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe effectuant les jointures grace a un algorithme de jointure Page-Oriented-Nested-Loop.
 * <br/>(Voir TP8-A pour comprendre le fonctionnement plus en détail)
 */
public class PageOrientedJoinOperator implements IRecordIterator{

    private Relation r1;
    private Relation r2;
    private Record nextR1;
    private Record nextR2;
    private int cursorR1;
    private int cursorR2;
    private boolean isClosed;

    private List<Condition> conds;
    private BufferManager bm;
    private DataPageHoldRecordIterator dhrpi1;
    private DataPageHoldRecordIterator dhrpi2;
    private PageDirectoryIterator pdi;

    /**
     * Main constructor.
     *
     * @param r1 une relation R1.
     * @param r2 une relation R2.
     * @param conds la liste des conditions.
     * @param bm une instance de BufferManager.
     * @throws BufferManager.BufferCountExcededException si le bufferpool est plein.
     */
    public PageOrientedJoinOperator(Relation r1, Relation r2, List<Condition>conds, BufferManager bm) throws BufferManager.BufferCountExcededException {
        this.r1 = r1;
        this.r2 = r2;
        cursorR1 = cursorR2 = 0;
        this.conds = conds;
        isClosed = false;

        dhrpi1 = dhrpi2 = null;
        pdi = new PageDirectoryIterator(r1,r2,bm);
        this.bm = bm;
    }

    /**
     * Renvois la jointure des records correspondant aux conditions.
     *
     * @return un record representant la jointure.
     */
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

                    if ((nextR2 = dhrpi2.getNextRecord()) == null) {
                        if ((nextR1 = dhrpi1.getNextRecord()) == null){
                            dhrpi1.reset();
                            nextR1 = dhrpi1.getNextRecord();
                        }
                        if (cursorR2 < pdi.nbPageR2()) {
                            dhrpi2.close();
                            pid = pdi.getNextDataPageId();
                            dhrpi2 = new DataPageHoldRecordIterator(r2, pid, bm);
                            nextR2 = dhrpi2.getNextRecord();
                            cursorR2++;
                        }else{
                            dhrpi1.close();
                            pid = pdi.getNextDataPageId();
                            if (pid == null) {
                                dhrpi2.close();
                                return null;
                            }
                            dhrpi1 = new DataPageHoldRecordIterator(r1, pid, bm);
                            nextR1 = dhrpi1.getNextRecord();
                            cursorR1++;
                            dhrpi2.close();
                            pid = pdi.getNextDataPageId();
                            dhrpi2 = new DataPageHoldRecordIterator(r2, pid, bm);
                            nextR2 = dhrpi2.getNextRecord();
                            cursorR2 = 1;
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

    /**
     * Ferme l'iterateur.
     */
    @Override
    public void close() {
        if (isClosed) throw new RuntimeException("Iterator closed.");
        isClosed = true;
    }

    /**
     * Ne fait rien lol
     */
    @Override
    public void reset() {}
}
