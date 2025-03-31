#include <jni.h>
#include <string>

#include <android/log.h>

#include "realgo/realgo.h"

#define  LOG_TAG    "realgo"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)

// https://stackoverflow.com/a/49566764
jobject NewInteger(JNIEnv *env, int value) {
    jclass integerClass = env->FindClass("java/lang/Integer");
    jmethodID integerConstructor = env->GetMethodID(integerClass, "<init>", "(I)V");
    return env->NewObject(integerClass, integerConstructor, static_cast<jint>(value));
}

static CCPayloadCipher *getChipper(JNIEnv *env, jobject thiz) {
    jclass jc = env->GetObjectClass(thiz);
    jfieldID fid = env->GetFieldID(jc, "mNativePtr", "J");
    jlong p = (jlong) env->GetLongField(thiz, fid);
    CCPayloadCipher *chiper = (CCPayloadCipher *) p;
    return chiper;
}

extern "C"
JNIEXPORT jbyteArray JNICALL

Java_com_walisport_lib_1socket_NativeLib_pack(JNIEnv *env,
                                                      jobject thiz,
                                                      jshort mid,
                                                      jshort sid,
                                                      jstring data,
                                                      jint dataSize
) {

    //LOGD("[jni] dataSize = %d", dataSize);

    CCPayloadCipher *chiper = getChipper(env, thiz);
    LOGD("get chiper %p", chiper);

    unsigned char outData[SOCKET_BUFFER];
    memset(outData, 0, SOCKET_BUFFER);
    unsigned short outDataSize = 0;
    const char *cstr = env->GetStringUTFChars(data, 0);
    unsigned int len = dataSize;
    chiper->pack(mid, sid, cstr, len, outData, &outDataSize);

//    std::string hexStr;
//    for (int i = 0; i < outDataSize; ++i) {
//        char buf[16] = {0};
//        snprintf(buf, 16, "%02X ", outData[i]);
//        hexStr += buf;
//    }
//    LOGD("%s", hexStr.c_str());

    env->ReleaseStringUTFChars(data, cstr);

    jstring result;
    jbyteArray bytes = 0;


    //LOGD("%s: outDataSize %u", __FUNCTION__, outDataSize);
    jbyteArray jarrRet = env->NewByteArray(outDataSize);
    env->SetByteArrayRegion(jarrRet, 0, outDataSize, (jbyte *) outData);

    return jarrRet;

}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_walisport_lib_1socket_NativeLib_newPack(JNIEnv *env,
                                                         jobject thiz,
                                                         jshort mid,
                                                         jshort sid,
                                                         jbyteArray data,
                                                         jint dataSize
) {
    // 获取CCPayloadCipher对象
    CCPayloadCipher *cipher = getChipper(env, thiz);
    if (cipher == nullptr) {
        return nullptr;
    }
    //LOGD(">.>%s: mid=%u,sid=%u", __FUNCTION__, mid, sid);
    jbyte *dataBytes = env->GetByteArrayElements(data, nullptr);
    if (dataBytes == nullptr) {
        return nullptr;
    }

    unsigned char outData[SOCKET_BUFFER];
    memset(outData, 0, SOCKET_BUFFER);
    unsigned short outDataSize = 0;
    cipher->pack(mid, sid, reinterpret_cast<char *>(dataBytes), dataSize,
                 outData, &outDataSize);

    env->ReleaseByteArrayElements(data, dataBytes, 0);

    jbyteArray jarrRet = env->NewByteArray(outDataSize);
    if (jarrRet == nullptr) {
        return nullptr;
    }
    jbyte *outDataPtr = reinterpret_cast<jbyte *>(outData);
    env->SetByteArrayRegion(jarrRet, 0, outDataSize, outDataPtr);
    return jarrRet;
}
extern "C"
JNIEXPORT jlong JNICALL
Java_com_walisport_lib_1socket_NativeLib_nativeCreateChiper(JNIEnv *env, jobject thiz) {

    CCPayloadCipher *chiper = new CCPayloadCipher();

    LOGD("create chipper=%p", chiper);
    return (jlong) chiper;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_walisport_lib_1socket_NativeLib_reset(JNIEnv *env, jobject thiz) {

    CCPayloadCipher *chiper = getChipper(env, thiz);

    LOGD("reset chipper=%p", chiper);
    chiper->reset();
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_walisport_lib_1socket_NativeLib_unpack(JNIEnv *env,
                                                        jobject thiz,
                                                        jbyteArray data
) {
    CCPayloadCipher *chiper = getChipper(env, thiz);

    jsize len = env->GetArrayLength(data);
    jbyte *body = env->GetByteArrayElements(data, 0);
    unsigned char *someUnsignedChar = new unsigned char[len];
    for (jint i = 0; i < len; i++) {
        someUnsignedChar[i] = (unsigned char) body[i];
    }

    unsigned short mid = -1;
    unsigned short sid = -1;

    unsigned char cbDataBuffer[SOCKET_BUFFER];
    memset(cbDataBuffer, 0, SOCKET_BUFFER);

    unsigned char *pDataBuffer = 0;
    unsigned int wDataSize = 0;

    chiper->unpack(someUnsignedChar, len, &mid, &sid, cbDataBuffer, &pDataBuffer, &wDataSize);

    std::string str;
    if (wDataSize) {
        str.assign((const char *) pDataBuffer, wDataSize);
    }

    // return value
    jobjectArray retobjarr = (jobjectArray) env->NewObjectArray(3,
                                                                env->FindClass("java/lang/Object"),
                                                                NULL);
    env->SetObjectArrayElement(retobjarr, 0, NewInteger(env, mid));
    env->SetObjectArrayElement(retobjarr, 1, NewInteger(env, sid));
    env->SetObjectArrayElement(retobjarr, 2, env->NewStringUTF(str.data()));

    //LOGD(">.>%s: mid=%u,sid=%u,text=[%s],textSize=[%d]", __FUNCTION__, mid, sid, str.data(),
    //     wDataSize);
    env->ReleaseByteArrayElements(data, body, 0);

    delete[] someUnsignedChar;
    return retobjarr;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_walisport_lib_1socket_NativeLib_newUnpack(JNIEnv *env, jobject thiz,
                                                           jbyteArray data) {
    CCPayloadCipher *cipher = getChipper(env, thiz);

    // Get the length of the byte array
    jsize len = env->GetArrayLength(data);
    // Get the elements of the byte array
    jbyte *body = env->GetByteArrayElements(data, nullptr);
    // Convert jbyteArray to unsigned char array
    unsigned char *someUnsignedChar = new unsigned char[len];
    env->GetByteArrayRegion(data, 0, len, reinterpret_cast<jbyte *>(someUnsignedChar));
    unsigned short mid = -1;
    unsigned short sid = -1;
    unsigned char cbDataBuffer[SOCKET_BUFFER] = {0}; // Initialize with 0
    unsigned char *pDataBuffer = nullptr;
    unsigned int wDataSize = 0;
//    LOGD("before unpack");
    // Call unpack function
    cipher->unpack(someUnsignedChar, len, &mid, &sid, cbDataBuffer, &pDataBuffer, &wDataSize);
//    LOGD("after unpack");
    // Create a jbyteArray to store pDataBuffer data
    jbyteArray dataBufferArray = env->NewByteArray(wDataSize);
    env->SetByteArrayRegion(dataBufferArray, 0, wDataSize, reinterpret_cast<jbyte *>(pDataBuffer));
//    LOGD("before create object array ");
    // Create jobjectArray to contain results
    jobjectArray retobjarr = env->NewObjectArray(3, env->FindClass("java/lang/Object"), nullptr);
    env->SetObjectArrayElement(retobjarr, 0, NewInteger(env, mid));
    env->SetObjectArrayElement(retobjarr, 1, NewInteger(env, sid));
    env->SetObjectArrayElement(retobjarr, 2, dataBufferArray);
//    LOGD("before to string");
    // Log the results
    std::string str(reinterpret_cast<const char *>(pDataBuffer), wDataSize);
    //LOGD(">.>%s: mid=%u, sid=%u, text=[%s], textSize=[%d]", __FUNCTION__, mid, sid, str.c_str(),
     //    wDataSize);

    // Release resources
    env->DeleteLocalRef(dataBufferArray);
    env->ReleaseByteArrayElements(data, body, 0);
    delete[] someUnsignedChar;
    return retobjarr;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_walisport_lib_1socket_NativeLib_nativeFinalizer(JNIEnv *env, jobject thiz,
                                                                 jlong ptr) {
    CCPayloadCipher *chiper = getChipper(env, thiz);

    LOGD("delete chipper=%p", chiper);
    if (chiper) {
        delete chiper;
    }

}
extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_walisport_lib_1socket_NativeLib_test(JNIEnv *env, jobject thiz) {


    short int mid = 7;
    short int sid = 6;
    std::string str = "hello";
    // return value
    jobjectArray retobjarr = (jobjectArray) env->NewObjectArray(3,
                                                                env->FindClass("java/lang/Object"),
                                                                NULL);
    env->SetObjectArrayElement(retobjarr, 0, NewInteger(env, mid));
    env->SetObjectArrayElement(retobjarr, 1, NewInteger(env, sid));
    env->SetObjectArrayElement(retobjarr, 2, env->NewStringUTF(str.data()));

    return retobjarr;
}
