package fr.upc.mi.bdda.DiskManager;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DBConfigTest {
    @ParameterizedTest
    @CsvSource({"'./DB', -1, 4096, 3, 'MRU'", "'./DB', 1024, -12, 3, 'MRU'","'./DB', 1024, 4096, -3, 'MRU'"})
    void testDBConfigIllegalArgument(String dbpath, int pagesize, int dm_maxfilesize,
                                     int bm_buffercount, String bm_policy){
        assertThrows(IllegalArgumentException.class, ()->
                new DBConfig(dbpath, pagesize, dm_maxfilesize, bm_buffercount, bm_policy));
    }

    @ParameterizedTest
    @ValueSource(strings="./fauxchemin")
    void testLoadDBConfigMissingFile(String fic_config){
        assertThrows(IOException.class,()->
                DBConfig.LoadDBConfig(fic_config));
    }

    @ParameterizedTest
    @CsvSource({"'./DB', 1024, 4096, 3, 'MRU'", "'./DB', 3, 9, 15, 'LRU'"})
    void testGetters(String dbpath, int pagesize, int dm_maxfilesize,
                                     int bm_buffercount, String bm_policy){
        DBConfig config = new DBConfig(dbpath, pagesize, dm_maxfilesize, bm_buffercount, bm_policy);

        assertEquals(config.getDbpath(),dbpath);
        assertEquals(config.getPagesize(),pagesize);
        assertEquals(config.getDm_maxfilesize(),dm_maxfilesize);
        assertEquals(config.getBm_buffercount(),bm_buffercount);
        assertEquals(config.getBm_policy(),bm_policy);
    }



}