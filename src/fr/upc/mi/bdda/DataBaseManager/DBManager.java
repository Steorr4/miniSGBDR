package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.DiskManager.DBConfig;
import fr.upc.mi.bdda.FileAccess.Relation;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class DBManager {
    HashMap<String, Database> databases;
    Database current;

    DBConfig config;

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
        databases.remove(dbName);
    }

    public void removeTablesFromCurrentDatabase(){
        current.removeTables();
    }

    public void removeDatabases(){
        databases.clear();
    }

    public void listDatabases(){
        for(String dbName : databases.keySet()) System.out.println(dbName);
    }

    public void listTablesInCurrentDatabase(){
        current.listTables();
    }

    // Va poser probeleme si plusieurs values pour une clef.
    public void saveState(){
        try {
            int i = 0;

            File f = new File(config.getDbpath() + "/databases/nameDB");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(databases.keySet());

            for(Database db : databases.values()) {
                f = new File(config.getDbpath() + "/databases/DB"+i);
                fos = new FileOutputStream(f);
                oos = new ObjectOutputStream(fos);

                oos.writeObject(db);
                i++;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void loadState(){
        try{
            int i = 0;

            File f = new File(config.getDbpath() + "/databases/nameDB");
            FileInputStream fis = new FileInputStream(f);
            ObjectInputStream ois = new ObjectInputStream(fis);

            Set<String> dbnames = (Set<String>)ois.readObject();

            for(String s: dbnames){
                f = new File(config.getDbpath() + "/databases/DB"+i);
                fis = new FileInputStream(f);
                ois = new ObjectInputStream(fis);

                databases.put(s,(Database) ois.readObject());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }




    /**
     *
     */
    private class Database implements Serializable {
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
            for(String tableName : tables.keySet()) System.out.println(tableName);
        }
    }
}
