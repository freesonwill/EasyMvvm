#include <jni.h>
#include <string>
#include <cstring>
#include <stdexcept>
#include <android/log.h>

#include "libduckgo/duckgo.h"

#define  LOG_TAG    "duckgo"
#define  LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,LOG_TAG,__VA_ARGS__)


// 私有实现类
class CCSPayloadCipher_p {
public:
    int cipherType = 0; // 1:pb, 2:json
    bool initialized = false;
};

CCSPayloadCipher::CCSPayloadCipher() : _p(new CCSPayloadCipher_p()) {}
CCSPayloadCipher::~CCSPayloadCipher() { delete _p; }

void CCSPayloadCipher::init(int cipherType) {
    if (_p) {
        _p->cipherType = cipherType;
        _p->initialized = true;
        LOGD("Cipher initialized with type: %d", cipherType);
    }
}

void CCSPayloadCipher::reset() {
    if (_p) {
        _p->initialized = false;
        _p->cipherType = 0;
    }
}

int CCSPayloadCipher::pack(unsigned short mid,
                           unsigned short sid,
                           unsigned short rid,
                           const char *data,
                           unsigned int dataSize,
                           unsigned char *outData,
                           unsigned short *outDataSize) {
    if (!_p || !_p->initialized || !data || !outData || !outDataSize) {
        return -1;
    }

    if (dataSize == 0 || dataSize > SOCKET_BUFFER - 10) {
        return -2;
    }

    // 协议头 (10字节)
    unsigned char *ptr = outData;

    // 版本 (2字节)
    *ptr++ = DUCKGO_VERSION & 0xFF;
    *ptr++ = (DUCKGO_VERSION >> 8) & 0xFF;

    // 消息头 (6字节)
    *ptr++ = mid & 0xFF;
    *ptr++ = (mid >> 8) & 0xFF;

    *ptr++ = sid & 0xFF;
    *ptr++ = (sid >> 8) & 0xFF;

    *ptr++ = rid & 0xFF;
    *ptr++ = (rid >> 8) & 0xFF;

    // 数据长度 (2字节)
    *ptr++ = dataSize & 0xFF;
    *ptr++ = (dataSize >> 8) & 0xFF;

    // 数据体
    memcpy(ptr, data, dataSize);
    ptr += dataSize;

    *outDataSize = static_cast<unsigned short>(ptr - outData);

    return 0;
}

int CCSPayloadCipher::unpack(unsigned char *buf,
                             unsigned int bufSize,
                             unsigned short *mid,
                             unsigned short *sid,
                             unsigned short *rid,
                             unsigned char *data,
                             unsigned char **pDataBuffer,
                             unsigned int *dataBufferSize) {
    if (!_p || !_p->initialized || !buf || bufSize < 10 ||
        !mid || !sid || !rid || !data || !pDataBuffer || !dataBufferSize) {
        return -1;
    }

    // 检查版本
    unsigned short version = buf[0] | (buf[1] << 8);
    if (version != DUCKGO_VERSION) {
        return -2;
    }

    // 解析消息头
    *mid = buf[2] | (buf[3] << 8);
    *sid = buf[4] | (buf[5] << 8);
    *rid = buf[6] | (buf[7] << 8);

    // 获取数据长度
    unsigned short size = buf[8] | (buf[9] << 8);
    if (size > SOCKET_BUFFER || size > bufSize - 10) {
        return -3;
    }

    // 设置数据指针
    memcpy(data, buf + 10, size);
    *pDataBuffer = data;
    *dataBufferSize = size;

    return 0;
}

// ================= JNI 绑定 =================
extern "C" {

JNIEXPORT jlong JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_createCipher(JNIEnv *, jobject) {
    return reinterpret_cast<jlong>(new CCSPayloadCipher());
}

JNIEXPORT void JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_initCipher(
        JNIEnv *, jobject, jlong handle, jint cipherType) {
    auto cipher = reinterpret_cast<CCSPayloadCipher*>(handle);
    if (cipher) cipher->init(static_cast<int>(cipherType));
}

JNIEXPORT void JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_destroyCipher(
        JNIEnv *, jobject, jlong handle) {
    delete reinterpret_cast<CCSPayloadCipher*>(handle);
}

JNIEXPORT void JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_resetCipher(
        JNIEnv *, jobject, jlong handle) {
    auto cipher = reinterpret_cast<CCSPayloadCipher*>(handle);
    if (cipher) cipher->reset();
}

JNIEXPORT jbyteArray JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_packData(
        JNIEnv *env, jobject, jlong handle,
        jshort mid, jshort sid, jshort rid,
        jbyteArray data) {

    auto cipher = reinterpret_cast<CCSPayloadCipher*>(handle);
    if (!cipher) return nullptr;

    jsize length = env->GetArrayLength(data);
    BYTE buffer[SOCKET_BUFFER];
    unsigned short outLen = 0;

    jbyte* inputData = env->GetByteArrayElements(data, nullptr);
    int result = cipher->pack(
            static_cast<unsigned short>(mid),
            static_cast<unsigned short>(sid),
            static_cast<unsigned short>(rid),
            reinterpret_cast<const char*>(inputData),
            static_cast<unsigned int>(length),
            buffer,
            &outLen
    );
    env->ReleaseByteArrayElements(data, inputData, JNI_ABORT);

    if (result != 0) {
        LOGD("Pack failed with error: %d", result);
        return nullptr;
    }

    jbyteArray ret = env->NewByteArray(outLen);
    env->SetByteArrayRegion(ret, 0, outLen, reinterpret_cast<jbyte*>(buffer));
    return ret;
}

JNIEXPORT jobject JNICALL
Java_arch_cayenne_lib_chatwebsocket_ChatNativeLib_unpackData(
        JNIEnv *env, jobject, jlong handle, jbyteArray data) {

    auto cipher = reinterpret_cast<CCSPayloadCipher*>(handle);
    if (!cipher) return nullptr;

    jsize length = env->GetArrayLength(data);
    BYTE buffer[SOCKET_BUFFER];
    BYTE* pDataBuffer = nullptr;
    unsigned short mid, sid, rid;
    unsigned int dataSize = 0;

    jbyte* inputData = env->GetByteArrayElements(data, nullptr);
    int result = cipher->unpack(
            reinterpret_cast<unsigned char*>(inputData),
            static_cast<unsigned int>(length),
            &mid, &sid, &rid,
            buffer,
            &pDataBuffer,
            &dataSize
    );
    env->ReleaseByteArrayElements(data, inputData, JNI_ABORT);

    if (result != 0) {
        LOGD("Unpack failed with error: %d", result);
        return nullptr;
    }

    // 获取Kotlin类引用
    jclass resultClass = env->FindClass("arch/cayenne/lib/chatwebsocket/ChatNativeLib$UnpackResult");
    jmethodID constructor = env->GetMethodID(resultClass, "<init>", "(SSS[B)V");

    // 创建返回对象
    jbyteArray payload = env->NewByteArray(dataSize);
    env->SetByteArrayRegion(payload, 0, dataSize, reinterpret_cast<jbyte*>(pDataBuffer));

    return env->NewObject(
            resultClass, constructor,
            static_cast<jshort>(mid),
            static_cast<jshort>(sid),
            static_cast<jshort>(rid),
            payload
    );
}

} // extern "C"