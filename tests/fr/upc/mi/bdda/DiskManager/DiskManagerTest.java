package fr.upc.mi.bdda.DiskManager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;



class DiskManagerTest {

    private DiskManager dm;
    private DBConfig config;

    @BeforeEach
    void beforeEach(){
        config = new DBConfig("./DB", 16, 64, 15, "LRU");
        dm = new DiskManager(config);
    }

    @Test
    void testAllocPageFileCreation() throws IOException{
        dm.AllocPage();
        File file = new File(config.getDbpath()+"/BinData/F"+1+".rsdb");
        assertTrue(file.exists());
    }

    @Test
    void testAllocPageFileCreationAfterOneFileComplete() throws IOException{
        for(int i = 0; i<=config.getDm_maxfilesize()/config.getPagesize(); i++){
            dm.AllocPage();
        }
        File file = new File(config.getDbpath()+"/BinData/F"+2+".rsdb");
        assertTrue(file.exists());
    }

    @Test
    void testReadPage() throws IOException {;
        dm.AllocPage();
        PageId pid = dm.AllocPage();
        ByteBuffer buff = ByteBuffer.allocateDirect(config.getPagesize());
        String filePath = config.getDbpath()+"/BinData/F"+pid.getFileIdx()+".rsdb";

        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        raf.seek(config.getPagesize());
        raf.write(5);
        raf.close();

        dm.ReadPage(pid, buff);
        assertEquals(5,buff.get());
    }

    @Test
    void testWritePage() throws IOException {
        dm.AllocPage();
        PageId pid = dm.AllocPage();
        ByteBuffer buff = ByteBuffer.allocateDirect(config.getPagesize());
        String filePath = config.getDbpath()+"/BinData/F"+pid.getFileIdx()+".rsdb";

        buff.putInt(127);
        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        raf.seek(config.getPagesize());

        dm.WritePage(pid, buff);
        assertEquals(127,raf.readInt());
        raf.close();
    }


}