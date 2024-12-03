package fr.upc.mi.bdda.DiskManager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageIdTest {

    @Test
    void testEquals() {
        PageId pid1 = new PageId(0, 1);
        PageId pid2 = new PageId(0, 2);
        PageId pid3 = new PageId(1, 1);
        PageId pid4 = new PageId(0, 2);

        assertEquals(pid2, pid4);
        assertNotEquals(pid1, pid2);
        assertNotEquals(pid1,pid3);
    }
}