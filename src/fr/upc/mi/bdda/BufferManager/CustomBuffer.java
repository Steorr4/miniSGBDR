package fr.upc.mi.bdda.BufferManager;

//Packages
import fr.upc.mi.bdda.DiskManager.*;

//JAVA imports
import java.nio.ByteBuffer;

/**
 * Wrapper autour un ByteBuffer afin d'y apporter les informations nécessaires au fonctionnement du BufferManager.
 */
public class CustomBuffer {
    private ByteBuffer bb; // Buffer en RAM
    private PageId pid; // ID de la page associée
    private int pin_count; // Nombre d'utilisations en cours
    private boolean dirty_flag; // Modification de la page ?
    private long time; // Temps au moment de la liération complète

    /**
     * Main constructor.
     *
     * @param pid la page représentée par le Buffer.
     * @param config une instance de la Config pour récupérer la taille d'un page.
     */
    public CustomBuffer(PageId pid, DBConfig config){
        this.bb = ByteBuffer.allocateDirect(config.getPagesize());
        this.pid = pid;
        this.pin_count = 1;
        this.dirty_flag = false;
        this.time = 0;
    }

    //Getters & Setters
    public ByteBuffer getBb() {
        return bb;
    }
    public void setBb(ByteBuffer bb) {
        this.bb = bb;
    }
    public PageId getPid() {
        return pid;
    }
    public void setPid(PageId pid) {
        this.pid = pid;
    }
    public int getPin_count() {
        return pin_count;
    }
    public void setPin_count(int pin_count) {
        this.pin_count = pin_count;
    }
    public boolean isDirty_flag() {
        return dirty_flag;
    }
    public void setDirty_flag(boolean dirty_flag) {
        this.dirty_flag = dirty_flag;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    //ToString
    @Override
    public String toString() {
        return "CustomBuffer{" +
                "bb=" + bb +
                ", pid=" + pid +
                ", pin_count=" + pin_count +
                ", dirty_flag=" + dirty_flag +
                ", time=" + time +
                '}';
    }
}
