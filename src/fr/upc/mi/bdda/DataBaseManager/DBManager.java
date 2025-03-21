package fr.upc.mi.bdda.DataBaseManager;

import fr.upc.mi.bdda.BufferManager.BufferManager;
import fr.upc.mi.bdda.DiskManager.DBConfig;
import fr.upc.mi.bdda.DiskManager.DiskManager;
import fr.upc.mi.bdda.FileAccess.ColInfo;
import fr.upc.mi.bdda.FileAccess.Relation;

import java.io.*;
import java.util.HashMap;

/**
 * Classe principale qui s'occupe des bases de données.
 * <br/>(Voir TP6-B pour comprendre le fonctionnement plus en détail)
 */
public class DBManager{
    private HashMap<String, Database> databases;
    private Database current;

    private transient DBConfig config;

    /**
     * Main constructor.
     *
     * @param config une instance de la config.
     */
    public DBManager(DBConfig config){
        this.current = null;
        this.databases = new HashMap<>();
        this.config = config;

    }

    /**
     * Creer une nouvelle database.
     *
     * @param name le nom de la database.
     */
    public void createDatabase(String name){
        databases.put(name, new Database(name));
    }

    /**
     * Definie la database courrante.
     *
     * @param name le nom de la database.
     */
    public void setCurrentDatabase(String name){
        current = databases.get(name);
    }

    /**
     * Ajoute une table a la database courrante.
     *
     * @param table la relation.
     */
    public void addTableToCurrentDatabase(Relation table){
        current.addTable(table);
    }

    /**
     * Recupere la table correspondante.
     *
     * @param tableName nom de la relation.
     * @return la relation.
     */
    public Relation getTableFromCurrentDatabase(String tableName){
        return current.getTable(tableName);
    }

    /**
     * Supprime la table de la database courrante.
     *
     * @param tableName le nom de la relation.
     */
    public void removeTableFromCurrentDatabase(String tableName){
        current.removeTable(tableName);
    }

    /**
     * Supprime une database.
     *
     * @param dbName le nom de la database.
     */
    public void removeDatabase(String dbName){
        if(dbName.equals(current.name)) current = null;
        databases.remove(dbName);
    }

    /**
     * Supprime toutes les tables de la database courrante.
     */
    public void removeTablesFromCurrentDatabase(){
        current.removeTables();
    }

    /**
     * Supprime toutes les databases
     */
    public void removeDatabases(){
        current = null;
        databases.clear();
    }

    /**
     * Affiche toutes les databases existantes.
     */
    public void listDatabases(){
        for(String dbName : databases.keySet()) System.out.println(dbName);
    }

    /**
     * Affiche toutes les tables de la database courrante.
     */
    public void listTablesInCurrentDatabase(){
        current.listTables();
    }

    /**
     * Serialize l'etat des database.
     *
     * @param dm l'instance du DiskManager.
     */
    public void saveState(DiskManager dm){
        try {
            int i = 0;

            File f = new File(config.getDbpath() + "/databases/nameDB.save");
            FileOutputStream fos = new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(dm.getNbFichiers());
            oos.writeObject(databases);

            for(Database db : databases.values()) {
                f = new File(config.getDbpath() + "/databases/DB"+i+".save");
                f.createNewFile();
                fos = new FileOutputStream(f);
                oos = new ObjectOutputStream(fos);

                oos.writeObject(db);
                i++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reload l'etat des databases.
     *
     * @param dm une instance de DiskManager.
     * @param bm une instance de BufferManager.
     */
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

                dm.setNbFichiers((int)ois.readObject());
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
                    }

                    i++;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Classe représentant une database.
     */
    private static class Database implements Serializable {
        private String name;
        private HashMap<String, Relation> tables;

        /**
         * Main constructor.
         *
         * @param name le nom de la database.
         */
        public Database(String name){
            this.name = name;
            this.tables = new HashMap<>();
        }

        /**
         * Ajoute une table à la database.
         *
         * @param table la relation à ajouter.
         */
        public void addTable(Relation table){
            tables.put(table.getName(),table);
        }

        /**
         * Recupere une relation de la database.
         *
         * @param tableName le nom de la relation.
         * @return la relation.
         */
        public Relation getTable(String tableName){
            return tables.get(tableName);
        }

        /**
         * Supprime une table de la database.
         *
         * @param tableName le nom de la relation.
         */
        public void removeTable(String tableName){
            tables.remove(tableName);
        }

        /**
         * Supprime toutes les tables de la database.
         */
        public void removeTables(){
            tables.clear();
        }

        /**
         * Affiche toutes les tables de la database.
         */
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
