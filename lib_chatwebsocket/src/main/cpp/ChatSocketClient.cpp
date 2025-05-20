#include <jni.h>
#include <string>

#include <android/log.h>

#include "libduckgo/duckgo.h"

#define  LOG_TAG    "duckgo"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

// https://stackoverflow.com/a/49566764
jobject NewInteger(JNIEnv *env, int value) {
    jclass integerClass = env->FindClass("java/lang/Integer");
    jmethodID integerConstructor = env->GetMethodID(integerClass, "<init>", "(I)V");
    return env->NewObject(integerClass, integerConstructor, static_cast<jint>(value));
}

static CCSPayloadCipher *getChipper(JNIEnv *env, jobject thiz) {
    jclass jc = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(jc, "mNativePtr", "J");
    jlong p = (jlong) env->GetLongField(thiz, fid);
    CCSPayloadCipher *chiper = (CCSPayloadCipher *) p;
    return chiper;
}

extern "C"
JNIEXPORT void JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_init(JNIEnv *env, jobject thiz, jint cipher_type) {
    CCSPayloadCipher *chiper = getChipper(env, thiz);
    LOGD("init chipper=%p with type=%d", chiper, cipher_type);
    chiper->init(cipher_type);
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_pack(JNIEnv *env,
                                                       jobject thiz,
                                                       jshort mid,
                                                       jshort sid,
                                                       jshort rid,
                                                       jstring data,
                                                       jint dataSize
) {
    CCSPayloadCipher *chiper = getChipper(env, thiz);
    LOGD("get chiper %p", chiper);

    unsigned char outData[SOCKET_BUFFER];
    memset(outData, 0, SOCKET_BUFFER);
    unsigned short outDataSize = 0;
    const char *cstr = env->GetStringUTFChars(data, 0);
    unsigned int len = dataSize;
    chiper->pack(mid, sid, rid, cstr, len, outData, &outDataSize);

    env->ReleaseStringUTFChars(data, cstr);

    jbyteArray jarrRet = env->NewByteArray(outDataSize);
    env->SetByteArrayRegion(jarrRet, 0, outDataSize, (jbyte *) outData);

    return jarrRet;
}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_newPack(JNIEnv *env,
                                                          jobject thiz,
                                                          jshort mid,
                                                          jshort sid,
                                                          jshort rid,
                                                          jbyteArray data,
                                                          jint dataSize
) {
    CCSPayloadCipher *cipher = getChipper(env, thiz);
    if (cipher == nullptr) {
        return nullptr;
    }

    jbyte *dataBytes = env->GetByteArrayElements(data, nullptr);
    if (dataBytes == nullptr) {
        return nullptr;
    }

    unsigned char outData[SOCKET_BUFFER];
    memset(outData, 0, SOCKET_BUFFER);
    unsigned short outDataSize = 0;
    cipher->pack(mid, sid, rid, reinterpret_cast<char *>(dataBytes), dataSize,
                 outData, &outDataSize);

    env->ReleaseByteArrayElements(data, dataBytes, 0);

    jbyteArray jarrRet = env->NewByteArray(outDataSize);
    if (jarrRet == nullptr) {
        return nullptr;
    }
    env->SetByteArrayRegion(jarrRet, 0, outDataSize, reinterpret_cast<jbyte *>(outData));
    return jarrRet;
}

extern "C"
JNIEXPORT jlong JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_nativeCreateChiper(JNIEnv *env, jobject thiz) {
    CCSPayloadCipher *chiper = new CCSPayloadCipher();
    LOGD("create chipper=%p", chiper);
    return (jlong) chiper;
}

extern "C"
JNIEXPORT void JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_reset(JNIEnv *env, jobject thiz) {
    CCSPayloadCipher *chiper = getChipper(env, thiz);
    LOGD("reset chipper=%p", chiper);
    chiper->reset();
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_unpack(JNIEnv *env,
                                                         jobject thiz,
                                                         jbyteArray data
) {
    CCSPayloadCipher *chiper = getChipper(env, thiz);

    jsize len = env->GetArrayLength(data);
    jbyte *body = env->GetByteArrayElements(data, 0);
    unsigned char *someUnsignedChar = new unsigned char[len];
    for (jint i = 0; i < len; i++) {
        someUnsignedChar[i] = (unsigned char) body[i];
    }

    unsigned short mid = -1;
    unsigned short sid = -1;
    unsigned short rid = -1;

    unsigned char cbDataBuffer[SOCKET_BUFFER];
    memset(cbDataBuffer, 0, SOCKET_BUFFER);

    unsigned char *pDataBuffer = 0;
    unsigned int wDataSize = 0;

    chiper->unpack(someUnsignedChar, len, &mid, &sid, &rid, cbDataBuffer, &pDataBuffer, &wDataSize);

    std::string str;
    if (wDataSize) {
        str.assign((const char *) pDataBuffer, wDataSize);
    }

    jobjectArray retobjarr = (jobjectArray) env->NewObjectArray(4,
                                                                env->FindClass("java/lang/Object"),
                                                                NULL);
    env->SetObjectArrayElement(retobjarr, 0, NewInteger(env, mid));
    env->SetObjectArrayElement(retobjarr, 1, NewInteger(env, sid));
    env->SetObjectArrayElement(retobjarr, 2, NewInteger(env, rid));
    env->SetObjectArrayElement(retobjarr, 3, env->NewStringUTF(str.data()));

    env->ReleaseByteArrayElements(data, body, 0);
    delete[] someUnsignedChar;
    return retobjarr;
}

//extern "C"
//JNIEXPORT jobjectArray JNICALL
//Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_newUnpack(JNIEnv *env, jobject thiz,
//                                                    jbyteArray data) {
//    CCSPayloadCipher *cipher = getChipper(env, thiz);
//
//    jsize len = env->GetArrayLength(data);
//    jbyte *body = env->GetByteArrayElements(data, nullptr);
//    unsigned char *someUnsignedChar = new unsigned char[len];
//    env->GetByteArrayRegion(data, 0, len, reinterpret_cast<jbyte *>(someUnsignedChar));
//
//    unsigned short mid = -1;
//    unsigned short sid = -1;
//    unsigned short rid = -1;
//    unsigned char cbDataBuffer[SOCKET_BUFFER] = {0};
//    unsigned char *pDataBuffer = nullptr;
//    unsigned int wDataSize = 0;
//
//    cipher->unpack(someUnsignedChar, len, &mid, &sid, &rid, cbDataBuffer, &pDataBuffer, &wDataSize);
//
//    jbyteArray dataBufferArray = env->NewByteArray(wDataSize);
//    env->SetByteArrayRegion(dataBufferArray, 0, wDataSize, reinterpret_cast<jbyte *>(pDataBuffer));
//
//    jobjectArray retobjarr = env->NewObjectArray(4, env->FindClass("java/lang/Object"), nullptr);
//    env->SetObjectArrayElement(retobjarr, 0, NewInteger(env, mid));
//    env->SetObjectArrayElement(retobjarr, 1, NewInteger(env, sid));
//    env->SetObjectArrayElement(retobjarr, 2, NewInteger(env, rid));
//    env->SetObjectArrayElement(retobjarr, 3, dataBufferArray);
//
//    env->DeleteLocalRef(dataBufferArray);
//    env->ReleaseByteArrayElements(data, body, 0);
//    delete[] someUnsignedChar;
//    return retobjarr;
//}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_newUnpack(JNIEnv *env, jobject thiz,
                                                            jbyteArray data) {
    CCSPayloadCipher *cipher = getChipper(env, thiz);
    if (cipher == nullptr) {
        return nullptr;
    }

    jsize len = env->GetArrayLength(data);
    jbyte *body = env->GetByteArrayElements(data, nullptr);
    if (body == nullptr) {
        return nullptr;
    }

    unsigned short mid = -1;
    unsigned short sid = -1;
    unsigned short rid = -1;
    unsigned char cbDataBuffer[SOCKET_BUFFER] = {0};
    unsigned char *pDataBuffer = nullptr;
    unsigned int wDataSize = 0;

    int result = cipher->unpack(reinterpret_cast<unsigned char*>(body), len,
                                &mid, &sid, &rid,
                                cbDataBuffer, &pDataBuffer, &wDataSize);

    env->ReleaseByteArrayElements(data, body, 0);

    if (result != 0 || wDataSize == 0) { // 假设返回0表示成功
        return nullptr;
    }

    // 创建返回数组
    jobjectArray retobjarr = env->NewObjectArray(4, env->FindClass("java/lang/Object"), nullptr);
    if (retobjarr == nullptr) {
        return nullptr;
    }

    // 设置MID
    env->SetObjectArrayElement(retobjarr, 0, NewInteger(env, mid));
    // 设置SID
    env->SetObjectArrayElement(retobjarr, 1, NewInteger(env, sid));
    // 设置RID
    env->SetObjectArrayElement(retobjarr, 2, NewInteger(env, rid));

    // 设置数据
    jbyteArray dataArray = env->NewByteArray(wDataSize);
    env->SetByteArrayRegion(dataArray, 0, wDataSize, reinterpret_cast<jbyte*>(pDataBuffer));
    env->SetObjectArrayElement(retobjarr, 3, dataArray);
    env->DeleteLocalRef(dataArray);

    return retobjarr;
}


extern "C"
JNIEXPORT void JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_nativeFinalizer(JNIEnv *env, jobject thiz,
                                                                  jlong ptr) {
    CCSPayloadCipher *chiper = getChipper(env, thiz);
    LOGD("delete chipper=%p", chiper);
    if (chiper) {
        delete chiper;
    }
}