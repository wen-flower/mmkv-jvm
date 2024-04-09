package icu.twtool.mmkv;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MMKVTest {

    @BeforeAll
    static void before() {
        System.out.println("initialize");
        String userDir = System.getProperty("user.dir");
        System.load(userDir + "\\cpp\\cmake-build-debug\\mmkv.dll");
        MMKV.initialize(userDir + "\\build\\test-dir");
    }

    @Test
    void testBool() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.bool";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertFalse(mmkv.decodeBool(key));

        assertTrue(mmkv.encode(key, true));
        assertTrue(mmkv.containsKey(key));
        assertTrue(mmkv.decodeBool(key));

        assertTrue(mmkv.encode(key, false));
        assertFalse(mmkv.decodeBool(key));
    }

    @Test
    void testInt() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.int";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertEquals(0, mmkv.decodeInt(key));

        assertTrue(mmkv.encode(key, 1));
        assertTrue(mmkv.containsKey(key));
        assertEquals(1, mmkv.decodeInt(key));

        assertTrue(mmkv.encode(key, 2));
        assertEquals(2, mmkv.decodeInt(key));
    }

    @Test
    void testLong() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.long";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertEquals(-1L, mmkv.decodeLong(key, -1L));
        assertEquals(0L, mmkv.decodeLong(key));

        assertTrue(mmkv.encode(key, 1L));
        assertTrue(mmkv.containsKey(key));
        assertEquals(1L, mmkv.decodeLong(key));

        assertTrue(mmkv.encode(key, 2L));
        assertEquals(2L, mmkv.decodeLong(key));
    }

    @Test
    void testFloat() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.float";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertEquals(0f, mmkv.decodeFloat(key));

        assertTrue(mmkv.encode(key, 1f));
        assertTrue(mmkv.containsKey(key));
        assertEquals(1f, mmkv.decodeFloat(key));

        assertTrue(mmkv.encode(key, 2f));
        assertEquals(2f, mmkv.decodeFloat(key));
    }

    @Test
    void testDouble() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.double";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertEquals(0, mmkv.decodeDouble(key));

        assertTrue(mmkv.encode(key, 1.0));
        assertTrue(mmkv.containsKey(key));
        assertEquals(1, mmkv.decodeDouble(key));

        assertTrue(mmkv.encode(key, 2.0));
        assertEquals(2, mmkv.decodeDouble(key));
    }

    @Test
    void testBytes() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.bytes";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertNull(mmkv.decodeBytes(key));

        assertTrue(mmkv.encode(key, new byte[]{1}));
        assertTrue(mmkv.containsKey(key));
        assertArrayEquals(new byte[]{1}, mmkv.decodeBytes(key));

        assertTrue(mmkv.encode(key, new byte[]{1, 2}));
        assertArrayEquals(new byte[]{1, 2}, mmkv.decodeBytes(key));
    }

    @Test
    void testString() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.string";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertNull(mmkv.decodeString(key));

        assertTrue(mmkv.encode(key, "a"));
        assertTrue(mmkv.containsKey(key));
        assertEquals("a", mmkv.decodeString(key));

        assertTrue(mmkv.encode(key, "b"));
        assertEquals("b", mmkv.decodeString(key));
    }

    @Test
    void testStringSet() {
        MMKV mmkv = MMKV.defaultMMKV();
        String key = "test.string.set";
        mmkv.removeValueForKey(key);
        assertFalse(mmkv.containsKey(key));
        assertNull(mmkv.decodeString(key));

        Set<String> value = new HashSet<>();
        value.add("a");
        assertTrue(mmkv.encode(key, value));
        assertTrue(mmkv.containsKey(key));
        assertArrayEquals(value.toArray(), Objects.requireNonNull(mmkv.decodeStringSet(key)).toArray());

        value.add("b");
        assertTrue(mmkv.encode(key, value));
        assertArrayEquals(value.toArray(), Objects.requireNonNull(mmkv.decodeStringSet(key)).toArray());
    }
}