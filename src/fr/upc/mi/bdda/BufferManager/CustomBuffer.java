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

    public int remaining(){
        bb.position(0);
        return bb.remaining();
    }
    public void setPos(int i){
        bb.position(i);
    }
    public int getPos(){
        return bb.position();
    }
    public void putInt(int pos, int value){
        bb.putInt(pos, value);
    }
    public void putInt(int value){;
        bb.putInt(value);
        bb.position(0);
    }
    public int getInt(int pos){
        int value = bb.getInt(pos);
        bb.position(0);
        return value;
    }
    public void putFloat(int pos, float value){
        bb.putFloat(pos, value);
    }
    public void putFloat(float value){
        bb.putFloat(value);
        bb.position(0);
    }
    public float getFloat(int pos){
        float value = bb.getFloat(pos);
        bb.position(0);
        return value;
    }
    public void putChar(int pos, char c){
        bb.putChar(pos, c);
    }
    public void putChar(char c){
        bb.putChar(c);
        bb.position(0);
    }
    public char getChar(int pos){
        char value = bb.getChar(pos);
        bb.position(0);
        return value;
    }
    public void putBytes(byte[] data){
        bb.put(data);
        bb.position(0);
    }
    public void putBytes(int pos, byte[] data){
        bb.put(pos, data);
    }
    public void getBytes(byte[] dst){
        bb.get(dst);
        bb.position(0);
    }
    public void getBytes(byte[] dst, int offsetDst, int length){
        bb.get(dst, offsetDst, length);
        bb.position(0);
    }
    public byte getByte(){
        byte b = bb.get();
        bb.position(0);
        return b;
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
