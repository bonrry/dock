// DockIt useful defines are declared here.

#ifndef _DOCKIT_H_
#define _DOCKIT_H_

//------------------------------------------------------------------------------
//        REAL BOARD SPECIFICS
//------------------------------------------------------------------------------
#if defined(ARDUINO)
    
    #include <ChibiOS_AVR.h>

    #define out(...) Serial.print(__VA_ARGS__)

    #ifdef __cplusplus
    extern "C"{
    #endif  //  __cplusplus
        size_t chHeapMainSize();
        size_t chUnusedHeapMain();
        size_t chUnusedStack(void *wsp, size_t size);
    #ifdef __cplusplus
    } // extern "C"
    #endif  // __cplusplus
#else
//------------------------------------------------------------------------------
//        SIMULATOR SPECIFICS
//------------------------------------------------------------------------------

    #include "ch.h"
    #include "hal.h"

    #define out(...) fprintf(stderr, __VA_ARGS__)

#endif  // ARDUINO

//------------------------------------------------------------------------------
//        COMMON DECLARATIONS
//------------------------------------------------------------------------------

#ifdef __cplusplus
extern "C"{
#endif  //  __cplusplus
    void chBegin(void (*mainThread)());
#ifdef __cplusplus
} // extern "C"
#endif  // __cplusplus

#define LOG(msg) chMsgSend((Thread*)waLogThread, (msg_t)msg)

#endif  /* _DOCKIT_H_ */
