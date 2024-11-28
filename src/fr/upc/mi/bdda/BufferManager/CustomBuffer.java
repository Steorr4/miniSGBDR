package fr.upc.mi.bdda.BufferManager;

//Packages
import fr.upc.mi.bdda.DiskManager.*;

//JAVA imports
import java.nio.ByteBuffer;

public class CustomBuffer {
    private ByteBuffer bb;
    private PageId pid;
    private int pin_count;
    private boolean dirty_flag;
    private int time;
    private DBConfig config;

    public CustomBuffer(PageId pid, DBConfig config){
        this.bb = ByteBuffer.allocateDirect(config.getPagesize());
        this.pid = pid;
        this.pin_count = 1;
        this.dirty_flag = false;
        this.time = 0;
        this.config = config;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }



    public DBConfig getConfig() {
        return config;
    }

    public void setConfig(DBConfig config) {
        this.config = config;
    }

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
