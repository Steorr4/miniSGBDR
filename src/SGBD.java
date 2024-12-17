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
        dbm.saveState();
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
                            l.add(value.replaceAll("^\"|\"$", "")); // Enlever les guillemets
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
        if (cmd[1].equals("*")) {
            Relation r = dbm.getTableFromCurrentDatabase(cmd[3]);
            String alias = cmd[4];

            if(cmd.length == 5){
                RecordPrinter iterator = new RecordPrinter(new RelationScanner(r));
                iterator.print();

            }else {
                List<Condition> conds = new ArrayList<>();
                int nbcond = (cmd.length-6)/2;
                String ope;
                String strCond;
                String[] valCond;

                for(int i = 0; i<nbcond; i++){
                    strCond = cmd[7+2*i];
                    valCond = strCond.split("(<=|>=|<|>|<>|=)");
                    ope = strCond.substring(valCond[0].length(),strCond.length()-valCond[1].length());

                    if(valCond[0].startsWith(alias)){

                        if(valCond[1].startsWith(alias)) {
                            conds.add(new Condition(r.getColIndex(valCond[0].replaceAll(alias+"\\.","")),
                                    ope, r.getColIndex(valCond[1].replaceAll(alias+"\\.",""))));

                        }else{
                            conds.add(new Condition(r.getColIndex(valCond[0].replaceAll(alias+"\\.","")),
                                    ope, valCond[1]));
                        }
                    }else{
                        conds.add(new Condition(valCond[0],ope,
                                r.getColIndex(valCond[1].replaceAll(alias+"\\.",""))));
                    }
                }

                RecordPrinter iterator = new RecordPrinter(new SelectOperator(r, conds));
                iterator.print();

            }


        }else {
            //TODO
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
