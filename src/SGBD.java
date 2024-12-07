import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.DataBaseManager.DBManager;
import fr.upc.mi.bdda.DiskManager.DBConfig;
import fr.upc.mi.bdda.DiskManager.DiskManager;

import java.io.IOException;
import java.util.Scanner;

public class SGBD {
    private DBConfig config;
    private DiskManager dm;
    private BufferManager bm;
    private DBManager dbm;

    private SGBD(DBConfig config) {
        this.config = config;
        dm = new DiskManager(config);
        bm = new BufferManager(config, dm);
        dbm = new DBManager(config);

        dm.loadState();
        dbm.loadState();
    }

    private void run(){

        Scanner sc = new Scanner(System.in);
        boolean cond = true;
        String input;
        String[] cmd;

        do{
            System.out.println("?");
            input = sc.nextLine();
            cmd = input.split(" ");
            switch (cmd[0]){
                case "CREATE" -> processCreateCommand(cmd);
                case "SET" -> processSetCommand(cmd);
                case "DROP" -> processDropCommand(cmd);
                case "LIST" -> processListCommand(cmd);
                case "QUIT" ->  {
                    processQuitCommand();
                    cond = false;
                }
                default -> System.out.println("Incorrect command.");
            }
        }while (cond);
    }


    private void processCreateCommand(String[] cmd){
        System.out.println(cmd.length); //DEBUG
        if(cmd.length < 2){
            System.out.println("Missing argument.");
            return;
        }

        switch (cmd[1]){
            case "DATABASE" -> dbm.createDatabase(cmd[2]);

            case "TABLE" -> {
                if(cmd.length != 3){
                    System.out.println("Missing argument.");
                    return;
                }

                String[] arg = cmd[2].split("(,)");
                for(String s : arg){
                    //TODO
                }
            }
            default -> System.out.println("Incorrect(s) argument(s).");
        }
    }

    private void processSetCommand(String[] cmd){
        if(cmd.length != 3){
            System.out.println("Missing argument.");
            return;
        }

        switch (cmd[1]){
            case "DATABASE" -> dbm.setCurrentDatabase(cmd[2]);
            default -> System.out.println("Incorrect(s) argument(s).");
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
            default -> System.out.println("Incorrect(s) argument(s).");
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
            case "DATABASE" -> dbm.listDatabases();
            case "TABLES" -> dbm.listTablesInCurrentDatabase();
            default -> System.out.println("Incorrect(s) argument(s).");
        }
    }

    /**
     * TODO
     */
    private void processQuitCommand(){
        dm.saveState();
        dbm.saveState();
        bm.flushBuffers();
    }


    public static void main(String[]args){
        try {

            SGBD sgbd = new SGBD(DBConfig.loadDBConfig(args[0]));
            sgbd.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
