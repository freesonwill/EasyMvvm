

#pragma once

#include <stdint.h>

#define DUCKGO_VERSION      6
#define SOCKET_BUFFER       102400
typedef unsigned char       BYTE;
typedef unsigned int        UINT32;
typedef unsigned short      WORD;
typedef unsigned int        DWORD;

#define CCSPayloadCipher _xs_d
#define CCSPayloadCipher_p _xs_p

class CCSPayloadCipher_p;
class CCSPayloadCipher
{
public:
    CCSPayloadCipher();
    virtual ~CCSPayloadCipher();
    
    /**
     when network close, call me
     */
    void reset();

    /**
     
     WORD mid = 1;
     WORD sid = 2;
     
     const char *pData = "json,proto";
     unsigned short wDataSize = len;
     
     BYTE cbDataBuffer[SOCKET_BUFFER]; // memory manage by user
     memset(cbDataBuffer, 0, SOCKET_BUFFER);
     
     unsigned short outLen = 0;
     pack(mid, sid, pData, wDataSize, cbDataBuffer, &outLen);
     */
    int pack(unsigned short mid,
             unsigned short sid,
             const char *data,
             unsigned int dataSize,
             
             unsigned char *outData,
             unsigned short *outDataSize
             );
    
    /**
     size_t len = 0;
     const char *pData = "network data";
     
     WORD wPacketSize = 0;
     BYTE cbDataBuffer[SOCKET_BUFFER];
     
     unsigned short mid = -1;
     unsigned short sid = -1;
     
     unsigned char *pDataBuffer = 0; // json, proto data, point to cbDataBuffer[?]
     unsigned int wDataSize = 0; // json,proto data size
     unpack((unsigned char*)pData, len, &mid, &sid, cbDataBuffer, &pDataBuffer, &wDataSize);
     */
    int unpack(unsigned char *buf,
               unsigned int bufSize,
               
               unsigned short *mid,
               unsigned short *sid,
               unsigned char *data,
               unsigned char **pDataBuffer,
               unsigned int *dataBufferSize
               );
private:
    CCSPayloadCipher_p *_p;
};
