package bg.sofia.uni.fmi.mjt.cache;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class MemCacheTest<K, V> {

    private MemCache<Integer, String> mc;
    private LocalDateTime ldt = LocalDateTime.now().plusMinutes(2);

    @Before
    public void setup() {
        mc = new MemCache<>();
    }

    @Test
    public void testGeneral() throws CapacityExceededException {
        fillWIthData();
        assertEquals("one", mc.get(1));
        assertEquals(ldt, mc.getExpiration(1));
    }

    private void fillWIthData() throws CapacityExceededException  {
            mc.set(1, "one", ldt);
            mc.set(2, "two", ldt);
            mc.set(3, "3", LocalDateTime.now());
    }

    @Test
    public void testGeneralWithCapacity() throws CapacityExceededException {
        mc = new MemCache<>(3);
        fillWIthData();
        assertEquals("one", mc.get(1));
        assertEquals("two", mc.get(2));
        assertEquals("3", mc.get(3));
    }

    @Test
    public void testHitRate() throws CapacityExceededException{
        mc = new MemCache<>(20);
        for (int i = 0; i < 20; i++) {
            try {
                mc.set(i, i + "i", LocalDateTime.now().plusSeconds(1));
            } catch (CapacityExceededException e) { 
                e.printStackTrace();
            }
            mc.get(i);
        }
        assertEquals(1.000, mc.getHitRate(), 0.001);
    }

    @Test
    public void testkeyStorageWhenKeyIsExpired() throws CapacityExceededException {
        try {
            mc.set(1, "one", LocalDateTime.now().minusMinutes(1));
        } catch (CapacityExceededException e) {
            e.printStackTrace();
        }
        assertNull(mc.get(1));
    }

    @Test
    public void testKeyStorageWhenKeyIsDublicated() throws CapacityExceededException {

            mc.set(1, "one", LocalDateTime.now().plusMinutes(1));
            mc.set(1, "two", LocalDateTime.now().plusMinutes(1));

        assertEquals("two", mc.get(1));
    }

    private void setLowCacheCapacity() throws CapacityExceededException {
        mc = new MemCache<>(2);
            mc.set(1, "one", LocalDateTime.now().plusMinutes(1));
            mc.set(2, "two", LocalDateTime.now().plusMinutes(1));
    }

    @Test(expected = CapacityExceededException.class)
    public void testWhenCapacityIsExceeded() throws CapacityExceededException {
        mc = new MemCache<>(1);
        mc.set(1, "one", LocalDateTime.now().plusMinutes(1));
        mc.set(2, "two", LocalDateTime.now().plusMinutes(1));
    }

    @Test
    public void testRemoveContainedKeyWhenCapacityExceeded() throws CapacityExceededException{
            setLowCacheCapacity();
            assertEquals("one", mc.get(1));
                mc.remove(1);
            assertEquals(null, mc.get(1));
                mc.set(1, "one again", LocalDateTime.now().plusMinutes(1));
            assertEquals("one again", mc.get(1));
    }

    @Test
    public void testClear() throws CapacityExceededException {
        fillWIthData();
        assertEquals("3", mc.get(3));
        assertEquals(ldt, mc.getExpiration(2));
        mc.clear();
        assertEquals(null, mc.get(1));
        assertEquals(null, mc.get(3));
        assertEquals(null, mc.get(2));
    }

    @Test
    public void testInsertMultipleValues() throws CapacityExceededException { 
        try { 
            mc = new MemCache<>(30);
            int r = randomValue();
            for (int i = 0; i <= 100; i++) {
                    mc.set(r, "r"+r, LocalDateTime.now().plusMinutes(1));
            }
            ldt = LocalDateTime.now().plusSeconds(4);
            mc.set(r, "r"+r, ldt);
            assertEquals("r"+r, mc.get(r));
            assertEquals(ldt, mc.getExpiration(r));
        } catch (CapacityExceededException e) {
            e.printStackTrace();
        }
    }

    private int randomValue() {
        int min = 0, max = 100;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
