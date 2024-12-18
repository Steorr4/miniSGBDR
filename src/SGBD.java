import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.BufferManager.CustomBuffer;
import fr.upc.mi.bdda.DataBaseManager.*;
import fr.upc.mi.bdda.DiskManager.DBConfig;
import fr.upc.mi.bdda.DiskManager.DiskManager;
import fr.upc.mi.bdda.DiskManager.PageId;
import fr.upc.mi.bdda.FileAccess.*;
import fr.upc.mi.bdda.FileAccess.Record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * TODO
 */
public class SGBD {
    private DBConfig config;
    private DiskManager dm;
    private BufferManager bm;
    private DBManager dbm;

    private SGBD(DBConfig config) throws IOException {
        this.config = config;
        dm = new DiskManager(config);
        bm = new BufferManager(config, dm);
        dbm = new DBManager(config);

        File dir = new File(config.getDbpath()+"/BinData");
        if(!dir.exists()){
            dir.mkdirs();
        }

        dm.loadState();
        dbm.loadState(dm,bm);
    }

    /**
     * TODO
     */
    private void run(){

        Scanner sc = new Scanner(System.in);
        boolean cond = true;
        String input;
        String[] cmd;

        do{
            System.out.print("> ");
            input = sc.nextLine();
            cmd = input.split(" ");
            switch (cmd[0]){
                case "CREATE" -> processCreateCommand(cmd);
                case "SET" -> processSetCommand(cmd);
                case "DROP" -> processDropCommand(cmd);
                case "LIST" -> processListCommand(cmd);
                case "INSERT" -> processInsertCommand(cmd);
                case "BULKINSERT" -> processBulkinsertCommand(cmd);
                case "SELECT" -> processSelectCommand(cmd);
                case "QUIT" -> {
                    processQuitCommand();
                    cond = false;
                }
                default -> System.out.println("Incorrect command.");
            }
        }while (cond);
    }


    /**
     * TODO
     *
     * @param cmd
     */
    private void processCreateCommand(String[] cmd){
        if(cmd.length < 2){
            System.out.println("Missing argument.");
            return;
        }

        switch (cmd[1]){
            case "DATABASE" -> dbm.createDatabase(cmd[2]);

            case "TABLE" -> {
                if(cmd.length != 4){
                    System.out.println("Missing argument.");
                    return;
                }

                StringTokenizer st = new StringTokenizer(cmd[3],"(),:");
                List<ColInfo> colonnes = new ArrayList<>();
                String colName;
                Type colType = null;

                while (st.hasMoreTokens()){
                    colName = st.nextToken();
                    switch (st.nextToken()){
                        case "INT" -> colType = new TypeNonParam(TypeNonParam.ETypeNonParam.INT);
                        case "REAL" -> colType = new TypeNonParam(TypeNonParam.ETypeNonParam.REAL);
                        case "CHAR" -> colType = new TypeParam(Integer.parseInt(st.nextToken()), TypeParam.ETypeParam.CHAR);
                        case "VARCHAR" -> colType = new TypeParam(Integer.parseInt(st.nextToken()), TypeParam.ETypeParam.VARCHAR);
                    }
                    colonnes.add(new ColInfo(colName, colType));
                }
                try {
                    PageId pidHeaderPage = dm.allocPage();
                    CustomBuffer bufferHP = bm.getPage(pidHeaderPage);
                    bufferHP.putInt(0);
                    bm.freePage(pidHeaderPage,true);
                    dbm.addTableToCurrentDatabase(new Relation(cmd[2], colonnes, pidHeaderPage, dm, bm));

                } catch (IOException | BufferManager.BufferCountExcededException e) {
                    e.printStackTrace();
                }
            }
            default -> System.out.println("Incorrect(s) argument(s).");
        }
    }

    /**
     * TODO
     *
     * @param cmd
     */
    private void processSetCommand(String[] cmd){
        if(cmd.length != 3){
            System.out.println("Missing argument.");
            return;
        }

        switch (cmd[1]){
            case "DATABASE" -> dbm.setCurrentDatabase(cmd[2]);
            default -> System.out.println("Illegal(s) argument(s).");
        }
    }

    /**
     * TODO
     *
     * @param cmd
     */
    private void processDropCommand(String[] cmd){
        if(cmd.length < 2){
            System.out.println("Missing argument.");
            return;
        }

        switch (cmd[1]){
            case "TABLE" -> dbm.removeTableFromCurrentDatabase(cmd[2]);
            case "TABLES" -> dbm.removeTablesFromCurrentDatabase();
            case "DATABASE" -> dbm.removeDatabase(cmd[2]);
            case "DATABASES" -> dbm.removeDatabases();
            default -> System.out.println("Illegal(s) argument(s).");
        }
    }

    /**
     * TODO
     *
     * @param cmd
     */
    private void processListCommand(String[] cmd){
        if(cmd.length < 2){
            System.out.println("Missing argument.");
            return;
        }

        switch (cmd[1]){
            case "DATABASES" -> dbm.listDatabases();
            case "TABLES" -> dbm.listTablesInCurrentDatabase();
            default -> System.out.println("Illegal(s) argument(s).");
        }
    }

    /**
     * TODO
     */
    private void processQuitCommand(){
        bm.flushBuffers();
        dm.saveState();
        dbm.saveState(dm);
    }

    private void processInsertCommand(String[] cmd){

        if(cmd.length < 5){
            System.out.println("Missing argument.");
            return;
        }

        switch(cmd[1]){
            case "INTO" -> {
                Relation relation = dbm.getTableFromCurrentDatabase(cmd[2]);

                switch(cmd[3]){
                    case "VALUES" -> {
                        String[] values = cmd[4].substring(1, cmd[4].length() - 1).split(",\\s*");
                        List<String> l = new ArrayList<>();
                        for (String value : values) {
                            l.add(value.replaceAll("^\"|\"$", ""));
                            // Enlever les guillemets
                         }
                        Record record = new Record(l);

                        try {
                            relation.insertRecord(record);
                        } catch (BufferManager.BufferCountExcededException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    private void processBulkinsertCommand(String[] cmd){
        switch(cmd[1]){
            case "INTO" -> {
                Relation relation = dbm.getTableFromCurrentDatabase(cmd[2]);

                try {

                    File f = new File(cmd[3]);
                    if(!f.exists()) throw new FileNotFoundException("CSV not found.");
                    FileReader filereader = new FileReader(f);

                    CSVReader csvReader = new CSVReader(filereader);
                    String[] nextRecord;

                    while ((nextRecord = csvReader.readNext()) != null) {
                        Record record=new Record(Arrays.asList(nextRecord));
                        relation.insertRecord(record);
                    }

                } catch (BufferManager.BufferCountExcededException | IOException | CsvValidationException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void processSelectCommand(String[] cmd){

        HashMap<String, Relation> relations = new HashMap<>(); // <Alias, Relations>
        String[] tmp;
        int cpt = 3;
        tmp = cmd[cpt+1].split(",");
        relations.put(tmp[0], dbm.getTableFromCurrentDatabase(cmd[cpt]));
        while (cpt<cmd.length) {
            if (tmp.length == 2) {
                String r = tmp[1];
                tmp = cmd[cpt].split(",");
                cpt += 2;
                relations.put(cmd[cpt], dbm.getTableFromCurrentDatabase(r));
            }else{
                break;
            }
        }

        if(relations.size() == 1) {

            String alias = cmd[4];
            Relation r = relations.get(alias);
            int[] indexCol;

            if (cmd[1].equals("*")) {
                indexCol = new int[r.getNbCol()];
                for (int i = 0; i < indexCol.length; i++) {
                    indexCol[i] = i;
                }
            } else {
                String[] strCol = cmd[1].split(",");
                indexCol = new int[strCol.length];

                for (int i = 0; i < indexCol.length; i++) {
                    indexCol[i] = r.getColIndex(strCol[i].replaceAll(alias + "\\.", ""));
                }
            }

            if (cmd.length == 5) {
                RecordPrinter iterator = new RecordPrinter(new ProjectOperator(
                        new SelectOperator(r, null), indexCol));
                iterator.print();

            } else {
                List<Condition> conds = new ArrayList<>();
                int nbcond = (cmd.length - 5) / 2;
                String ope;
                String strCond;
                String[] valCond;

                for (int i = 0; i < nbcond; i++) {
                    strCond = cmd[6 + 2 * i];
                    valCond = strCond.split("(<=|>=|<|>|<>|=)");
                    ope = strCond.substring(valCond[0].length(), strCond.length() - valCond[1].length());

                    if (valCond[0].startsWith(alias)) {

                        if (valCond[1].startsWith(alias)) {
                            conds.add(new Condition(r.getColIndex(valCond[0].replaceAll(alias + "\\.", "")),
                                    ope, r.getColIndex(valCond[1].replaceAll(alias + "\\.", ""))));

                        } else {
                            valCond[1] = valCond[1].replaceAll("^\"|\"$", "");
                            conds.add(new Condition(r.getColIndex(valCond[0].replaceAll(alias + "\\.", "")),
                                    ope, valCond[1]));
                        }
                    } else {
                        valCond[0] = valCond[0].replaceAll("^\"|\"$", "");
                        conds.add(new Condition(valCond[0], ope,
                                r.getColIndex(valCond[1].replaceAll(alias + "\\.", ""))));
                    }
                }

                RecordPrinter iterator = new RecordPrinter(new ProjectOperator(
                        new SelectOperator(r, conds), indexCol));
                iterator.print();

            }
        }else{
            HashMap<String, int[]> indexCol = new HashMap<>();//<Alias, cols>
            for (String alias : relations.keySet()){

                int[] cols = new int[relations.get(alias).getNbCol()];
                for (int i = 0; i < cols.length; i++) {
                    cols[i] = i;
                }
                indexCol.put(alias, cols);

            }
            //TODO
            List<Condition> conds = new ArrayList<>();
            int nbcond = (cmd.length - cpt) / 2;
            String ope;
            String strCond;
            String[] valCond;
            List<String> alias = new ArrayList<>(relations.keySet());

            for(int i = 1; i<=nbcond; i++){
                strCond = cmd[cpt + 2 * i];
                valCond = strCond.split("(<=|>=|<|>|<>|=)");
                ope = strCond.substring(valCond[0].length(), strCond.length() - valCond[1].length());

                String alias1 = valCond[0].split("\\.")[0];
                String alias2 = valCond[1].split("\\.")[0];

                if(alias1.equals(alias.getFirst())){
                    conds.add(new Condition(
                            relations.get(alias1).getColIndex(valCond[0].replaceAll(alias1 + "\\.", "")),
                            ope,
                            relations.get(alias2).getColIndex(valCond[1].replaceAll(alias2 + "\\.", ""))
                    ));
                }else {
                    conds.add(new Condition(
                            relations.get(alias2).getColIndex(valCond[1].replaceAll(alias2 + "\\.", "")),
                            ope,
                            relations.get(alias1).getColIndex(valCond[0].replaceAll(alias1 + "\\.", "")),
                            true
                    ));
                }

            }

            try {
        IRecordIterator iterator = new PageOrientedJoinOperator(
                relations.get(alias.get(0)),
                relations.get(alias.get(1)),
                conds,
                bm
        );
        RecordPrinter printer = new RecordPrinter(iterator);
        printer.print();
    } catch (BufferManager.BufferCountExcededException e) {
        throw new RuntimeException(e);
    }

        }



    }

    /**
     * TODO
     *
     * @param args
     */
    public static void main(String[]args){
        try {

            SGBD sgbd = new SGBD(DBConfig.loadDBConfig(args[0]));
            sgbd.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
