package bg.sofia.uni.fmi.mjt.cache;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Random;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.SystemMenuBar;

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
    public void testGeneral() {
        fillWIthData();
        System.out.println("1: " + mc.get(1));
        System.out.println("2: " + mc.get(2));
        assertEquals("one", mc.get(1));
        assertEquals(ldt, mc.getExpiration(1));
        System.out.println("Hit rate: " + mc.getHitRate());
    }

    private void fillWIthData() {
        try {
            mc.set(1, "one", ldt);
            mc.set(2, "two", ldt);
            mc.set(3, "3", LocalDateTime.now());
        } catch (CapacityExceededException e) {
            e.getCause();
            e.getMessage();
        }
    }

    @Test
    public void testGeneralWithCapacity() {
        mc = new MemCache<>(3);
        fillWIthData();
        assertEquals("one", mc.get(1));
        assertEquals("two", mc.get(2));
        assertEquals("3", mc.get(3));
    }

    @Test
    public void testHitRate() {
        mc = new MemCache<>(20);
        for (int i = 0; i < 21; i++) {
            try {
                mc.set(i, i + "i", LocalDateTime.now().plusSeconds(1));
            } catch (CapacityExceededException e) {
                e.getCause();
                e.getMessage();
            }
            mc.get(i);
            i++;
        }
        System.out.println(mc.getHitRate());
        assertNotNull(mc.getHitRate());
        String actual = String.valueOf(mc.getHitRate());
        assertEquals("1.0", actual);
    }

    @Test
    public void testkeyStorageWhenKeyIsExpired() {
        try {
            mc.set(1, "one", LocalDateTime.now().minusMinutes(1));
        } catch (CapacityExceededException e) {
            e.printStackTrace();
        }
        System.out.println(mc.get(1));
        assertNull(mc.get(1));
    }

    @Test
    public void testKeyStorageWhenKeyIsDublicated() {
        try {
            mc.set(1, "one", LocalDateTime.now().plusMinutes(1));
            mc.set(1, "two", LocalDateTime.now().plusMinutes(1));
        } catch (CapacityExceededException e) {
            e.getCause();
            e.getMessage();
        }
        System.out.println(mc.get(1));
        assertEquals("two", mc.get(1));
    }

    private void setLowCacheCapacity() throws CapacityExceededException {
        mc = new MemCache<>(1);
        try {
            mc.set(1, "one", LocalDateTime.now().plusMinutes(1));
            mc.set(2, "two", LocalDateTime.now().plusMinutes(1));
        } catch (CapacityExceededException e) {
            throw new CapacityExceededException(e.getMessage(), e.getCause());
        }
    }

    @Test(expected = CapacityExceededException.class)
    public void testWhenCapacityIsExceeded() throws CapacityExceededException {
        try {
            setLowCacheCapacity();
        } catch (CapacityExceededException e) {
            throw new CapacityExceededException(e.getMessage(), e.getCause());
        }
    }

    @Test
    public void testRemoveContainedKeyWhenCapacityExceeded() {
        try {
            setLowCacheCapacity();
        } catch (CapacityExceededException e) {
            e.getCause();
            e.getMessage();
            mc.remove(1);
        } finally {
            assertEquals(null, mc.get(1));
            try {
                mc.set(2, "two", LocalDateTime.now().plusMinutes(1));
            } catch (CapacityExceededException e) {
                e.getCause();
                e.getMessage();
            }
            assertEquals("two", mc.get(2));
        }
    }

    @Test
    public void testClear() {
        fillWIthData();
        assertEquals("3", mc.get(3));
        assertEquals(ldt, mc.getExpiration(2));
        mc.clear();
        assertEquals(null, mc.get(1));
        assertEquals(null, mc.get(3));
        assertEquals(null, mc.get(2));
    }

    @Test
    public void testInsertMultipleValues() { //TODO
        mc = new MemCache<>(30);
        int r = randomValue();
        for (int i = 0; i <= 100; i++) {
            try {
                mc.set(r, "r"+r, LocalDateTime.now().plusMinutes(1));
            } catch (CapacityExceededException e) {
                e.getCause();
                e.getMessage();
            }
        }
        ldt = LocalDateTime.now().plusSeconds(4);
        try {
            mc.set(r, "r"+r, ldt);
        } catch (CapacityExceededException e) {
            e.getCause(); e.getMessage();
        }
        assertEquals("r"+r, mc.get(r));
        assertEquals(ldt, mc.getExpiration(r));
    }

    private int randomValue() {
        int min = 0, max = 100;
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
