package fr.upc.mi.bdda.DiskManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DiskManagerTest {

    private DiskManager dm;
    private DBConfig config;

    @BeforeEach
    void beforeEach(){
        config = new DBConfig("./DB", 3, 9, 15, "LRU");
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


}