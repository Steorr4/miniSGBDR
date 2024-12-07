package fr.upc.mi.bdda.BufferManager;

import fr.upc.mi.bdda.DiskManager.DBConfig;
import fr.upc.mi.bdda.DiskManager.DiskManager;
import fr.upc.mi.bdda.DiskManager.PageId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.*;

class BufferManagerTest {

    static DBConfig config;
    static DiskManager dm;
    static BufferManager bm;
    static PageId pid;

    @BeforeAll
    static void beforeAll() throws IOException {
        config = new DBConfig("./DB", 256, 1024, 3, "MRU");
        dm = new DiskManager(config);
        bm = new BufferManager(config, dm);
        pid = dm.allocPage();

        CustomBuffer buff = new CustomBuffer(pid, config);
        buff.putInt(0, 123);
        dm.writePage(pid, buff);
    }

    @Test
    void testGetPageBufferListEmpty() throws BufferManager.BufferCountExcededException {
        bm.getPage(pid);
        boolean cond = false;
        for(CustomBuffer b : bm.getBufferList()){
            if (b.getPid() == pid) {
                cond = true;
                break;
            }
        }
        assertTrue(cond);
    }

    @Test
    void testGetPageAlreadyLoaded() throws BufferManager.BufferCountExcededException {
        bm.getPage(pid);
        bm.getPage(pid);
        assertEquals(1,bm.getBufferList().size());
        assertEquals(2,bm.getBufferList().getFirst().getPin_count());
    }

    @Test
    void testGetPageBufferListFull() throws BufferManager.BufferCountExcededException, IOException {
        bm.getPage(pid);

        PageId p1 = dm.allocPage();
        PageId p2 = dm.allocPage();
        PageId p3 = dm.allocPage();

        CustomBuffer buff = new CustomBuffer(pid, config);
        buff.putInt(0, 123);

        dm.writePage(p1, buff);
        dm.writePage(p2, buff);
        dm.writePage(p3, buff);

        bm.getPage(p1);
        bm.getPage(p2);

        assertThrows(BufferManager.BufferCountExcededException.class, ()->
                bm.getPage(p3));
    }
    //TODO : getPage() -> testRemplacement, testEcriture
}