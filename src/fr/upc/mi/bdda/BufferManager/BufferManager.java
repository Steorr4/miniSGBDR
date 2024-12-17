package fr.upc.mi.bdda.BufferManager;

//Packages
import fr.upc.mi.bdda.DiskManager.*;

//JAVA imports
import java.util.ArrayList;
import java.util.List;

/**
 * Classe qui s'occupe de la gestion des buffers et de la RAM.
 * Les couches au-dessus y font appel pour pouvoir lire/ecrire directement dans les pages chargées en RAM afin
 * de ne pas avoir à faire des accés disque répété.
 * <br/>(Voir TP3-A pour comprendre le fonctionnement plus en détail)
 */
public class BufferManager {
    private final DBConfig config; // Instance Config
    private final DiskManager dm; // Instance DiskManager

    private List<CustomBuffer> bufferList; // Cadres de page
    private final int bufferCount; // Nombre de cadre de page
    private String policy; // Politique de remplacement
    private long time; // Temps global

    /**
     * Main constructor.
     *
     * @param config l'instance de la Config.
     * @param dm l'instance du DiskManager.
     */
    public BufferManager(DBConfig config, DiskManager dm) {
        this.config = config;
        this.dm = dm;
        this.bufferList = new ArrayList<>(config.getBm_buffercount());
        this.bufferCount = config.getBm_buffercount();
        this.policy = config.getBm_policy();
        this.time = 0;
    }

    /**
     * Retourne à la demande de l'appelant, un buffer géré par le BufferManager
     * et met à jour le contenu des cadres de page.
     *
     * @param pid l'identifiant de la page demandée.
     * @return le buffer correspondant à la page.
     * @throws BufferCountExcededException si les cadres de pages sont pleins et que tous les buffers sont
     * en cours d'utilisation.
     */
    public CustomBuffer getPage(PageId pid) throws BufferCountExcededException {

        // Si la page est déjà chargée dans un cadre, on renvoie son buffer associé
        for (CustomBuffer cb : bufferList){
            if (pid.equals(cb.getPid())){
                cb.setPin_count(cb.getPin_count()+1);
                return cb;
            }
        }

        /*
            S'il y a des cadres disponibles, on crée un buffer de la page que l'on insère dans un cadre et on renvoie
            son buffer associé.
        */
        if (bufferList.size()<bufferCount){
            CustomBuffer cb = new CustomBuffer(pid, config);
            dm.readPage(pid,cb);
            bufferList.add(cb);
            return cb;
        }

        // Récupère une page inutilisée dans un cadre en fonction de la politique de remplacement
        CustomBuffer oldBuffer = remplacement();

        // Ecriture sur disque si la page a été modif, remplacement de l'ancienne page par la nouvelle
        if(oldBuffer.isDirty_flag()) dm.writePage(oldBuffer.getPid(), oldBuffer);
        CustomBuffer cb = new CustomBuffer(pid, config);
        dm.readPage(pid, cb); //TODO Jvais tellement me suicider
        bufferList.remove(oldBuffer);
        bufferList.add(cb);
        return cb;
    }

    /**
     * Applique la politique de remplacement précisée (MRU/LRU) pour selectionner un buffer candidat.
     *
     * @return le buffer candidat au remplacement.
     * @throws BufferCountExcededException si les cadres de pages sont pleins et que tous les buffers sont
     * en cours d'utilisation.
     * @throws IllegalArgumentException si la politique de remplacement spécifié n'est pas LRU ou MRU
     */
    private CustomBuffer remplacement() throws BufferCountExcededException, IllegalArgumentException {
        CustomBuffer candidat = null;

        switch (policy) {
            case "LRU":
                for (int i = 0; i < bufferCount; i++) {
                    if (bufferList.get(i).getPin_count() == 0) {
                        if (candidat == null) candidat = bufferList.get(i);
                        else if (candidat.getTime() > bufferList.get(i).getTime()) candidat = bufferList.get(i);
                    }
                }
                break;

            case "MRU":
                for (int i = 0; i < bufferCount; i++) {
                    if (bufferList.get(i).getPin_count() == 0) {
                        if (candidat == null) candidat = bufferList.get(i);
                        else if (candidat.getTime() < bufferList.get(i).getTime()) candidat = bufferList.get(i);
                    }
                }
                break;

            default:
                throw new IllegalArgumentException("Politique de remplacement inconnue");
        }

        if (candidat==null) throw new BufferCountExcededException("Les cadres de pages sont remplis");
        return candidat;
    }


    /**
     * Libère une page en abaissant son pin_count et met à jour son dirty_flag si elle a été modifiée ou non.
     * La page n'est pas supprimé des cadres, elle le sera seulement au moment de se faire remplacer par la methode
     * getPage().
     * Si au moment de la libération le pin_count atteint 0, le temps global est incrémenté et
     * le buffer enregistre ce temps de libération dans sa variable locale.
     *
     * @param pid la page que l'appelant souhaite libérer.
     * @param valdirty l'indication de s'il y a eu modification.
     */
    public void freePage(PageId pid, boolean valdirty){
        for(CustomBuffer buff : bufferList){
            if(buff.getPid().equals(pid)){
                buff.setPin_count(buff.getPin_count()-1);
                if(valdirty) buff.setDirty_flag(true);
                if(buff.getPin_count()==0){
                    time++;
                    buff.setTime(time);
                }
                break;
            }
        }
    }

    /**
     * Change la politique de remplacement.
     *
     * @param policy un String qui représente la politique de remplacement que l'on veut aplliquer.
     */
    public void setCurrentReplacementPolicy(String policy){
        this.policy=policy;
    }

    /**
     * Ecrit sur disque toutes les pages modifiées jusque-là et vide les cadres de page.
     */
    public void flushBuffers(){
        for (CustomBuffer buff : bufferList){
            if (buff.isDirty_flag()) dm.writePage(buff.getPid(),buff);
        }
        bufferList.clear();
    }

    //Getters & Setters
    public List<CustomBuffer> getBufferList() {
        return bufferList;
    }
    public void setBufferList(List<CustomBuffer> bufferList) {
        this.bufferList = bufferList;
    }
    public long getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Exception en cas de cadres de page pleins.
     */
    public static class BufferCountExcededException extends Exception {
        public BufferCountExcededException(String message) {
            super(message);
        }
    }

    public DBConfig getConfig() {return config;}
}
