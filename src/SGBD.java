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

    public SGBD(DBConfig config) {
        this.config = config;
        dm = new DiskManager(config);
        bm = new BufferManager(config, dm);
        dbm = new DBManager(config);

        dm.LoadState();
        dbm.loadState();
    }

    void run(){

        Scanner sc = new Scanner(System.in);
        boolean cond = true;

        do{
            System.out.println("?");
            switch (sc.next()){
                //TODO
            }

        }while (true);
    }



    public static void main(String[]args){
        try {

            SGBD sgbd = new SGBD(DBConfig.LoadDBConfig(args[0]));
            sgbd.run();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
