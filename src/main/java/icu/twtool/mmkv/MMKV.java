package icu.twtool.mmkv;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MMKV {

    /**
     * 单进程模式。 MMKV 实例上的默认模式。
     */
    static public final int SINGLE_PROCESS_MODE = 1 << 0;

    private static String rootDir = null;

    public static void initialize(String path) {
        jniInitialize(path);
        rootDir = path;
    }

    private native static void jniInitialize(String path);

    private native static long getDefaultMMKV(int mode, @Nullable String cryptKey);

    /**
     * 在单进程模式下创建默认的MMKV实例。
     *
     * @throws RuntimeException 如果出现运行时错误。
     */
    public static MMKV defaultMMKV() throws RuntimeException {
        return defaultMMKV(SINGLE_PROCESS_MODE, null);
    }

    /**
     * 在自定义进程模式下创建默认的 MMKV 实例，并使用加密密钥。
     *
     * @param mode     MMKV实例的进程模式，默认为{@link #SINGLE_PROCESS_MODE}。
     * @param cryptKey MMKV实例的加密密钥（不超过16字节）。
     * @throws RuntimeException 如果出现运行时错误。
     */
    public static MMKV defaultMMKV(int mode, @Nullable String cryptKey) throws RuntimeException {
        if (rootDir == null) {
            throw new IllegalStateException("You should Call MMKV.initialize() first.");
        }

        long handle = getDefaultMMKV(mode, cryptKey);
        return new MMKV(handle);
    }

    private final long handle;

    public MMKV(long handle) {
        this.handle = handle;
    }

    public boolean encode(String key, boolean value) {
        return encodeBool(handle, key, value);
    }
    public boolean encode(String key, int value) {
        return encodeInt(handle, key, value);
    }
    public boolean encode(String key, long value) {
        return encodeLong(handle, key, value);
    }
    public boolean encode(String key, float value) {
        return encodeFloat(handle, key, value);
    }
    public boolean encode(String key, double value) {
        return encodeDouble(handle, key, value);
    }
    public boolean encode(String key, byte @Nullable [] value) {
        return encodeBytes(handle, key, value);
    }
    public boolean encode(String key, @Nullable String value) {
        return encodeString(handle, key, value);
    }
    public boolean encode(String key, @Nullable Set<String> value) {
        return encodeSet(handle, key, (value == null) ? null : value.toArray(new String[0]));
    }

    private native boolean encodeBool(long handle, String key, boolean value);
    private native boolean encodeInt(long handle, String key, int value);
    private native boolean encodeLong(long handle, String key, long value);
    private native boolean encodeFloat(long handle, String key, float value);
    private native boolean encodeDouble(long handle, String key, double value);
    private native boolean encodeBytes(long handle, String key, byte @Nullable[] value);
    private native boolean encodeString(long handle, String key, @Nullable String value);
    private native boolean encodeSet(long handle, String key, String @Nullable [] value);

    public boolean decodeBool(String key) {
        return decodeBool(handle, key, false);
    }
    public int decodeInt(String key) {
        return decodeInt(handle, key, 0);
    }
    public long decodeLong(String key) {
        return decodeLong(handle, key, 0L);
    }
    public float decodeFloat(String key) {
        return decodeFloat(handle, key, 0f);
    }
    public double decodeDouble(String key) {
        return decodeDouble(handle, key, 0);
    }
    public byte @Nullable [] decodeBytes(String key) {
        return decodeBytes(handle, key);
    }
    public @Nullable String decodeString(String key) {
        return decodeString(handle, key, null);
    }
    public @Nullable Set<String> decodeStringSet(String key) {
        String[] result = decodeStringSet(handle, key);
        if (result != null) {
            return new HashSet<>(Arrays.asList(result));
        }
        return null;
    }

    private native boolean decodeBool(long handle, String key, boolean defaultValue);
    private native int decodeInt(long handle, String key, int defaultValue);
    private native long decodeLong(long handle, String key, long defaultValue);
    private native float decodeFloat(long handle, String key, float defaultValue);
    private native double decodeDouble(long handle, String key, double defaultValue);
    private native byte @Nullable [] decodeBytes(long handle, String key);
    private native @Nullable String decodeString(long handle, String key, @Nullable String defaultValue);
    private native String @Nullable [] decodeStringSet(long handle, String key);

    /**
     * 检查 MMKV是否包含 key。
     */
    public boolean containsKey(String key) {
        return containsKey(handle, key);
    }

    private native boolean containsKey(long handle, String key);

    public void removeValueForKey(String key) {
        removeValueForKey(handle, key);
    }

    private native void removeValueForKey(long handle, String key);
}
