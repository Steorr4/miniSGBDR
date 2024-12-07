package fr.upc.mi.bdda.DiskManager;

import static org.junit.jupiter.api.Assertions.*;

import fr.upc.mi.bdda.BufferManager.CustomBuffer;
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
    void testReadPage() throws IOException {
        dm.AllocPage();
        PageId pid = dm.AllocPage();
        CustomBuffer buff = new CustomBuffer(pid, config);
        String filePath = config.getDbpath()+"/BinData/F"+pid.getFileIdx()+".rsdb";

        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        raf.seek(config.getPagesize());
        raf.write(5);
        raf.close();

        dm.ReadPage(pid, buff);
        assertEquals(5,buff.getByte());
    }

    @Test
    void testWritePage() throws IOException {
        dm.AllocPage();
        PageId pid = dm.AllocPage();
        CustomBuffer buff = new CustomBuffer(pid, config);
        String filePath = config.getDbpath()+"/BinData/F"+pid.getFileIdx()+".rsdb";

        buff.putInt(127);
        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        raf.seek(config.getPagesize());

        dm.WritePage(pid, buff);
        assertEquals(127,raf.readInt());
        raf.close();
    }

    @Test
    void testDeallocPage() throws IOException {
        for(int i = 0; i < config.getPagesize(); i++) dm.AllocPage();
        PageId pid = dm.AllocPage();
        for(int i = 0; i < config.getPagesize(); i++) dm.AllocPage();

        dm.DeallocPage(pid);
        assertTrue(dm.getPagesLibres().contains(pid));
    }

    @Test
    void testSaveLoadState() throws IOException {

        for(int i = 0; i < config.getPagesize(); i++) dm.AllocPage();
        PageId pid1 = dm.AllocPage();
        PageId pid2 = dm.AllocPage();
        PageId pid3 = dm.AllocPage();
        for(int i = 0; i < config.getPagesize(); i++) dm.AllocPage();

        dm.DeallocPage(pid1);
        dm.DeallocPage(pid2);

        dm.SaveState();
        File file = new File(config.getDbpath()+"/dm.save");
        assertTrue(file.exists());

        dm.LoadState();
        assertTrue(dm.getPagesLibres().contains(pid1) &&
                dm.getPagesLibres().contains(pid2));
        assertFalse(dm.getPagesLibres().contains(pid3));
        assertEquals(3, dm.getPagesLibres().size());

    }

}