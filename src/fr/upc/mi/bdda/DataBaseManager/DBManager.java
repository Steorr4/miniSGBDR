package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.DiskManager.DBConfig;
import fr.upc.mi.bdda.DiskManager.DiskManager;
import fr.upc.mi.bdda.FileAccess.ColInfo;
import fr.upc.mi.bdda.FileAccess.Relation;

import java.io.*;
import java.util.HashMap;

public class DBManager{
    private HashMap<String, Database> databases;
    private Database current;

    private transient DBConfig config;

    public DBManager(DBConfig config){
        this.current = null;
        this.databases = new HashMap<>();
        this.config = config;

    }

    public void createDatabase(String name){
        databases.put(name, new Database(name));
    }

    public void setCurrentDatabase(String name){
        current = databases.get(name);
    }

    public void addTableToCurrentDatabase(Relation table){
        current.addTable(table);
    }

    public Relation getTableFromCurrentDatabase(String tableName){
        return current.getTable(tableName);
    }

    public void removeTableFromCurrentDatabase(String tableName){
        current.removeTable(tableName);
    }

    public void removeDatabase(String dbName){
        if(dbName.equals(current.name)) current = null;
        databases.remove(dbName);
    }

    public void removeTablesFromCurrentDatabase(){
        current.removeTables();
    }

    public void removeDatabases(){
        current = null;
        databases.clear();
    }

    public void listDatabases(){
        for(String dbName : databases.keySet()) System.out.println(dbName);
    }

    public void listTablesInCurrentDatabase(){
        current.listTables();
    }

    public void saveState(){
        try {
            int i = 0;

            File f = new File(config.getDbpath() + "/databases/nameDB.save");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(databases);

            for(Database db : databases.values()) {
                f = new File(config.getDbpath() + "/databases/DB"+i+".save");
                f.createNewFile();
                fos = new FileOutputStream(f);
                oos = new ObjectOutputStream(fos);

                oos.writeObject(db);
                //DEBUG********************************88
                System.out.println("Database " + db.name + " saved with " + db.tables.size() + " tables.");
                for (Relation r : db.tables.values()) {
                    System.out.println("Table " + r.getName() + " saved with " + r.getAllRecords().size() + " records.");
                }
                //****************************************
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BufferManager.BufferCountExcededException e) {
            throw new RuntimeException(e);
        }

    }

    public void loadState(DiskManager dm, BufferManager bm){
        try{
            int i = 0;

            File f = new File(config.getDbpath() + "/databases");
            if(!f.exists()) {
                f.mkdirs();
            }

            f = new File(config.getDbpath() + "/databases/nameDB.save");
            if(!f.exists()) {
                f.createNewFile();
            }else {

                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);

                HashMap<String, Database> dbnames = (HashMap<String, Database>) ois.readObject();
                for (String s : dbnames.keySet()) {
                    f = new File(config.getDbpath() + "/databases/DB" + i + ".save");
                    fis = new FileInputStream(f);
                    ois = new ObjectInputStream(fis);

                    databases.put(s, (Database) ois.readObject());
                    HashMap<String, Relation> relations = databases.get(s).tables;
                    for (Relation r : relations.values()) {
                        r.setBm(bm);
                        r.setDm(dm);
                        System.out.println("Table " + r.getName() + " loaded with " + r.getAllRecords().size() + " records."); //DEBUG
                    }

                    i++;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (BufferManager.BufferCountExcededException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     *
     */
    private static class Database implements Serializable {
        private String name;
        private HashMap<String, Relation> tables;

        public Database(String name){
            this.name = name;
            this.tables = new HashMap<>();
        }

        public void addTable(Relation table){
            tables.put(table.getName(),table);
        }

        public Relation getTable(String tableName){
            return tables.get(tableName);
        }

        public void removeTable(String tableName){
            tables.remove(tableName);
        }

        public void removeTables(){
            tables.clear();
        }

        public void listTables(){
            StringBuilder sb;
            for(Relation table : tables.values()){
                sb = new StringBuilder();
                sb.append(table.getName()).append(" (");
                for (ColInfo col : table.getColonnes()){
                    sb.append(col.getNomCol()).append(":").append(col.getTypeCol()).append(",");
                }
                sb.deleteCharAt(sb.length()-1);
                sb.append(")");
                System.out.println(sb);
            }
        }
    }
}
