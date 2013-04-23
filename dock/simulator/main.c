/*
    dockit wrapper
*/

#include <signal.h>
#include <stdlib.h>
#include <unistd.h>

#include "ch.h"
#include "hal.h"

extern void loop();
extern void setup();
extern void quit();

static void sig_hdlr(int signum) {
	switch (signum) {
    	case SIGINT:
            quit();
            exit(1);
            break;
    }
}

static void (*mainFcn)() = 0;

/**
 * Start ChibiOS/RT - does not return
 * \param[in] mainThread Function to be called before repeated calls
 *                       to loop().
 */
void chBegin(void (*mainThread)()) {
    mainFcn = mainThread;

    /*
     * System initializations.
     * - HAL initialization, this also initializes the configured device drivers
     *   and performs the board-specific initializations.
     * - Kernel initialization, the main() function becomes a thread and the
     *   RTOS is active.
     */
    halInit();
    chSysInit();
    
    if (mainFcn) mainFcn();
    while(1) {loop();}
}

/*------------------------------------------------------------------------*
 * Simulator main.                                                        *
 *------------------------------------------------------------------------*/
int main(void) {
    signal(SIGINT, &sig_hdlr);

    setup();
    return 0;
}
