import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BufferManager {
    private DBConfig config;
    private DiskManager dm;
    private List<CustomBuffer> bufferList;
    private int time;

    //Constructor
    public BufferManager(DBConfig config, DiskManager dm) {
        this.config = config;
        this.dm = dm;
        this.bufferList = new ArrayList<CustomBuffer>(config.getBm_buffercount());
        this.time = 0;
    }

    public CustomBuffer getPage(PageId pid){

        for (CustomBuffer cb : bufferList){
            if (pid.equals(cb.getPid())){
                cb.setPin_count(cb.getPin_count()+1);
                return cb;
            }
        }
        for (int i = 0; i < config.getBm_buffercount(); i++ ){
            if (bufferList.get(i)==null) {
                CustomBuffer cb = new CustomBuffer(pid, config);
                dm.ReadPage(pid,cb.getBb());
                bufferList.add(cb);
                return cb;
            }
        }
        CustomBuffer candidat = null;
        if (config.getBm_policy().equals("LRU")) {
            for (int i = 0; i < config.getBm_buffercount(); i++) {
                if (bufferList.get(i).getPin_count()==0 && bufferList.get(i).getTime() != 0){
                    if (candidat==null) candidat = bufferList.get(i);
                    else if (candidat.getTime() > bufferList.get(i).getTime()) candidat = bufferList.get(i);
                }

            }
        }else if(config.getBm_policy().equals("MRU")){
            for (int i = 0; i < config.getBm_buffercount(); i++) {
                if (bufferList.get(i).getPin_count() == 0 && bufferList.get(i).getTime() != 0) {
                    if (candidat == null) candidat = bufferList.get(i);
                    else if (candidat.getTime() < bufferList.get(i).getTime()) candidat = bufferList.get(i);
                }
            }
        }
        if(candidat.isDirty_flag()) dm.WritePage(candidat.getPid(), candidat.getBb());
        CustomBuffer cb = new CustomBuffer(pid, config);
        bufferList.set(bufferList.indexOf(candidat), cb);
        return cb;
    }//TODO: tester et finir la fonction

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

    public void setCurrentReplacementPolicy(String policy){
        config.setBm_policy(policy);
    }

    void flushBuffer(){
        for (CustomBuffer buff : bufferList){
            if (buff.isDirty_flag()) dm.WritePage(buff.getPid(),buff.getBb());
            buff=new CustomBuffer(null,config);
        }
    }

}
