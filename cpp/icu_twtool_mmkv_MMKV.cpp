#include "MMKV.h"
#include "icu_twtool_mmkv_MMKV.h"

#include "MMKVLog.h"

using std::string;
using std::vector;

static string jstring2string(JNIEnv* env, jstring str)
{
    // ReSharper disable once CppDFAConstantConditions
    if (str)
    {
        if (const char* kstr = env->GetStringUTFChars(str, nullptr))
        {
            string result(kstr);
            env->ReleaseStringUTFChars(str, kstr);
            return result;
        }
    }
    return "";
}

static jstring string2jstring(JNIEnv* env, const string& str)
{
    return env->NewStringUTF(str.c_str());
}


static vector<string> jarray2vector(JNIEnv* env, jobjectArray array)
{
    vector<string> keys;
    // ReSharper disable once CppDFAConstantConditions
    if (array)
    {
        const jsize size = env->GetArrayLength(array);
        keys.reserve(size);
        for (jsize i = 0; i < size; i++)
        {
            if (const auto str = (jstring)env->GetObjectArrayElement(array, i))
            {
                keys.push_back(jstring2string(env, str));
                env->DeleteLocalRef(str);
            }
        }
    }
    return keys;
}


static jobjectArray vector2jarray(JNIEnv* env, const vector<string>& arr)
{
    const auto size = static_cast<jsize>(arr.size());
    const auto result = env->NewObjectArray(size, env->FindClass("java/lang/String"), nullptr);
    if (result)
    {
        for (size_t index = 0; index < arr.size(); index++)
        {
            const auto value = string2jstring(env, arr[index]);
            env->SetObjectArrayElement(result, static_cast<jsize>(index), value);
            env->DeleteLocalRef(value);
        }
    }
    return result;
}

JNIEXPORT void JNICALL Java_icu_twtool_mmkv_MMKV_jniInitialize(JNIEnv* env, jclass, jstring path)
{
    const char* nativePath = env->GetStringUTFChars(path, nullptr);
    MMKV::initializeMMKV(string2MMKVPath_t(nativePath));
    env->ReleaseStringUTFChars(path, nativePath);
}

JNIEXPORT jlong JNICALL Java_icu_twtool_mmkv_MMKV_getDefaultMMKV(JNIEnv* env, jclass, jint mode, jstring cryptKey)
{
    MMKV* kv = nullptr;

    if (cryptKey)
    {
        string crypt = jstring2string(env, cryptKey);
        if (!crypt.empty())
        {
            kv = MMKV::defaultMMKV(static_cast<MMKVMode>(mode), &crypt);
        }
    }
    if (!kv)
    {
        kv = MMKV::defaultMMKV(static_cast<MMKVMode>(mode), nullptr);
    }

    return reinterpret_cast<jlong>(kv);
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeBool
(JNIEnv* env, jobject, jlong handle, jstring oKey, jboolean value)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->set(static_cast<bool>(value), key);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeInt
(JNIEnv* env, jobject, jlong handle, jstring oKey, jint value)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->set(static_cast<int32_t>(value), key);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeLong
(JNIEnv* env, jobject, jlong handle, jstring oKey, jlong value)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->set(value, key);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeFloat
(JNIEnv* env, jobject, jlong handle, jstring oKey, jfloat value)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->set(value, key);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeDouble
(JNIEnv* env, jobject, jlong handle, jstring oKey, jdouble value)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->set(value, key);
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeBytes
(JNIEnv* env, jobject, jlong handle, jstring oKey, jbyteArray oValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        if (oValue)
        {
            mmkv::MMBuffer value(0);
            {
                const jsize len = env->GetArrayLength(oValue);
                if (void* bufferPtr = env->GetPrimitiveArrayCritical(oValue, nullptr))
                {
                    value = mmkv::MMBuffer(bufferPtr, len);
                    env->ReleasePrimitiveArrayCritical(oValue, bufferPtr, JNI_ABORT);
                }
                else
                {
                    MMKVError("fail to get array: %s=%p", key.c_str(), oValue);
                }
            }
            return kv->set(value, key);
        }

        kv->removeValueForKey(key);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeString
(JNIEnv* env, jobject, jlong handle, jstring oKey, jstring oValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        if (oValue)
        {
            const string value = jstring2string(env, oValue);
            return kv->set(value, key);
        }
        kv->removeValueForKey(key);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_encodeSet
(JNIEnv* env, jobject, jlong handle, jstring oKey, jobjectArray arrStr)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        if (arrStr)
        {
            const vector<string> value = jarray2vector(env, arrStr);
            return kv->set(value, key);
        }
        kv->removeValueForKey(key);
        return true;
    }
    return false;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_decodeBool
(JNIEnv* env, jobject, jlong handle, jstring oKey, jboolean defaultValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->getBool(key, defaultValue);
    }
    return defaultValue;
}

JNIEXPORT jint JNICALL Java_icu_twtool_mmkv_MMKV_decodeInt
(JNIEnv* env, jobject, jlong handle, jstring oKey, jint defaultValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->getInt32(key, defaultValue);
    }
    return defaultValue;
}

JNIEXPORT jlong JNICALL Java_icu_twtool_mmkv_MMKV_decodeLong
(JNIEnv* env, jobject, jlong handle, jstring oKey, jlong defaultValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->getInt64(key, defaultValue);
    }
    return defaultValue;
}

JNIEXPORT jfloat JNICALL Java_icu_twtool_mmkv_MMKV_decodeFloat
(JNIEnv* env, jobject, jlong handle, jstring oKey, jfloat defaultValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->getFloat(key, defaultValue);
    }
    return defaultValue;
}

JNIEXPORT jdouble JNICALL Java_icu_twtool_mmkv_MMKV_decodeDouble
(JNIEnv* env, jobject, jlong handle, jstring oKey, jdouble defaultValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->getDouble(key, defaultValue);
    }
    return defaultValue;
}

JNIEXPORT jbyteArray JNICALL Java_icu_twtool_mmkv_MMKV_decodeBytes
(JNIEnv* env, jobject, jlong handle, jstring oKey)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        if (mmkv::MMBuffer value; kv->getBytes(key, value))
        {
            const auto length = static_cast<jsize>(value.length());
            const auto result = env->NewByteArray(length);
            env->SetByteArrayRegion(result, 0, length, static_cast<const jbyte*>(value.getPtr()));
            return result;
        }
    }
    return nullptr;
}

JNIEXPORT jstring JNICALL Java_icu_twtool_mmkv_MMKV_decodeString
(JNIEnv* env, jobject, jlong handle, jstring oKey, jstring oDefaultValue)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        if (string value; kv->getString(key, value))
        {
            return string2jstring(env, value);
        }
    }
    return oDefaultValue;
}

JNIEXPORT jobjectArray JNICALL Java_icu_twtool_mmkv_MMKV_decodeStringSet
(JNIEnv* env, jobject, jlong handle, jstring oKey)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        if (vector<string> value; kv->getVector(key, value))
        {
            return vector2jarray(env, value);
        }
    }
    return nullptr;
}

JNIEXPORT jboolean JNICALL Java_icu_twtool_mmkv_MMKV_containsKey
(JNIEnv* env, jobject, jlong handle, jstring oKey)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        return kv->containsKey(key);
    }
    return false;
}

JNIEXPORT void JNICALL Java_icu_twtool_mmkv_MMKV_removeValueForKey
(JNIEnv* env, jobject, jlong handle, jstring oKey)
{
    if (const auto kv = reinterpret_cast<MMKV*>(handle); kv && oKey)
    {
        const string key = jstring2string(env, oKey);
        kv->removeValueForKey(key);
    }
}
