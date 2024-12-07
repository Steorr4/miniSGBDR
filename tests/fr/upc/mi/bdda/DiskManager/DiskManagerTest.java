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
        dm.allocPage();
        File file = new File(config.getDbpath()+"/BinData/F"+1+".rsdb");
        assertTrue(file.exists());
    }

    @Test
    void testAllocPageFileCreationAfterOneFileComplete() throws IOException{
        for(int i = 0; i<=config.getDm_maxfilesize()/config.getPagesize(); i++){
            dm.allocPage();
        }
        File file = new File(config.getDbpath()+"/BinData/F"+2+".rsdb");
        assertTrue(file.exists());
    }

    @Test
    void testReadPage() throws IOException {
        dm.allocPage();
        PageId pid = dm.allocPage();
        CustomBuffer buff = new CustomBuffer(pid, config);
        String filePath = config.getDbpath()+"/BinData/F"+pid.getFileIdx()+".rsdb";

        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        raf.seek(config.getPagesize());
        raf.write(5);
        raf.close();

        dm.readPage(pid, buff);
        assertEquals(5,buff.getByte());
    }

    @Test
    void testWritePage() throws IOException {
        dm.allocPage();
        PageId pid = dm.allocPage();
        CustomBuffer buff = new CustomBuffer(pid, config);
        String filePath = config.getDbpath()+"/BinData/F"+pid.getFileIdx()+".rsdb";

        buff.putInt(127);
        RandomAccessFile raf = new RandomAccessFile(filePath, "rw");
        raf.seek(config.getPagesize());

        dm.writePage(pid, buff);
        assertEquals(127,raf.readInt());
        raf.close();
    }

    @Test
    void testDeallocPage() throws IOException {
        for(int i = 0; i < config.getPagesize(); i++) dm.allocPage();
        PageId pid = dm.allocPage();
        for(int i = 0; i < config.getPagesize(); i++) dm.allocPage();

        dm.deallocPage(pid);
        assertTrue(dm.getPagesLibres().contains(pid));
    }

    @Test
    void testSaveLoadState() throws IOException {

        for(int i = 0; i < config.getPagesize(); i++) dm.allocPage();
        PageId pid1 = dm.allocPage();
        PageId pid2 = dm.allocPage();
        PageId pid3 = dm.allocPage();
        for(int i = 0; i < config.getPagesize(); i++) dm.allocPage();

        dm.deallocPage(pid1);
        dm.deallocPage(pid2);

        dm.saveState();
        File file = new File(config.getDbpath()+"/dm.save");
        assertTrue(file.exists());

        dm.loadState();
        assertTrue(dm.getPagesLibres().contains(pid1) &&
                dm.getPagesLibres().contains(pid2));
        assertFalse(dm.getPagesLibres().contains(pid3));
        assertEquals(3, dm.getPagesLibres().size());

    }

}