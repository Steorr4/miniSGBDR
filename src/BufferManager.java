import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BufferManager {
    private DBConfig config;
    private DiskManager dm;
    private List<CustomBuffer> bufferList;

    //Constructor
    public BufferManager(DBConfig config, DiskManager dm) {
        this.config = config;
        this.dm = dm;
        this.bufferList = new ArrayList<CustomBuffer>(config.getBm_buffercount());
    }

    public CustomBuffer getPage(PageId pid){

        for (CustomBuffer cb : bufferList){
            if (pid.equals(cb.getPid())){
                cb.pin_count++;
                return cb;
            }
        }
        for (int i = 0; i < config.getBm_buffercount(); i++ ){
            if (bufferList.get(i)==null) {
                CustomBuffer cb = new CustomBuffer(pid);
                dm.ReadPage(pid,cb.getBb());
                bufferList.add(cb);
                return cb;
            }
        }
        CustomBuffer candidat = null;
        if (config.getBm_policy().equals("LRU")) {
            for (int i = 0; i < config.getBm_buffercount(); i++) {
                if (bufferList.get(i).pin_count==0 && bufferList.get(i).time != 0){
                    if (candidat==null) candidat = bufferList.get(i);
                    else if (candidat.time > bufferList.get(i).time) candidat = bufferList.get(i);
                }

            }
        }else if(config.getBm_policy().equals("MRU")){
            for (int i = 0; i < config.getBm_buffercount(); i++) {
                if (bufferList.get(i).pin_count == 0 && bufferList.get(i).time != 0) {
                    if (candidat == null) candidat = bufferList.get(i);
                    else if (candidat.time < bufferList.get(i).time) candidat = bufferList.get(i);
                }
            }
        }
        if(candidat.dirty_flag) dm.WritePage(candidat.pid, candidat.bb);
        CustomBuffer cb = new CustomBuffer(pid);
        bufferList.set(bufferList.indexOf(candidat), cb);
        return cb;
    }//TODO: tester et finir la fonction


    //Class du buffer
    private class CustomBuffer{
        private ByteBuffer bb;
        private PageId pid;
        private int pin_count;
        private boolean dirty_flag;
        private int time;

        public CustomBuffer(PageId pid){
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
    }



}
