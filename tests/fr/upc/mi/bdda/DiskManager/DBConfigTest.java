package fr.upc.mi.bdda.DiskManager;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DBConfigTest {
    @ParameterizedTest
    @CsvSource({"'./DB', -1, 4096, 3, 'MRU'", "'./DB', -1024, -12, 3, 'MRU'","'./DB', 1024, 4096, -3, 'MRU'"})
    void testIllegalArgumentDBConfig(String dbpath, int pagesize, int dm_maxfilesize,
                                     int bm_buffercount, String bm_policy){
        assertThrows(IllegalArgumentException.class, ()->{
            new DBConfig(dbpath, pagesize, dm_maxfilesize, bm_buffercount, bm_policy);
        });
    }

}