// DOCKIT
#if defined(ARDUINO)
    #include <ChibiOS_AVR.h>
    #include <Max3421e.h>
    #include <Usb.h>
#endif // ARDUINO
#include <AndroidAccessory.h>

#include <stdio.h>
#include <stdlib.h>

#include "dockit.h"

volatile uint32_t count = 0;

AndroidAccessory acc("Titi inc.",
                     "dockit",
                     "DawnDock - to wake you up happy",
                     "0.2",
                     "https://plus.google.com/u/0/106874330031036467055",
                     "00000000000042");

//------------------------------------------------------------------------------
/*
 * Console print server done using synchronous messages. This makes the access
 * to the C printf() thread safe and the print operation atomic among threads.
 * Here the message is the zero terminated string itself.
 */
// 64 byte stack beyond task switch and interrupt needs
static WORKING_AREA(waLogThread, 64);
static msg_t print_thread(void *arg) {
    (void)arg;
    while (!chThdShouldTerminate()) {
        Thread *tp = chMsgWait();
        //puts((char *)chMsgGet(tp));
        //fflush(stdout);
        out((char*)chMsgGet(tp));
        chMsgRelease(tp, RDY_OK);
    }
    return 0;
}

//------------------------------------------------------------------------------
// thread 1 - high priority for blinking LED
// 64 byte stack beyond task switch and interrupt needs
static WORKING_AREA(waThread1, 64);

#define BUF_SIZE 512
char in_buf[BUF_SIZE];
char out_buf[BUF_SIZE];

#define USB_READ_TIMEOUT               50 // in milliseconds
#define SLEEP_DELAY                    40 // in milliseconds
#define SLEEP_DELAY_NOT_CONNECTED    2000 // in milliseconds

char usb_thread_log_buf[BUF_SIZE];

void readMessage() {
	int res = 0;
	
	do {
		res = acc.read(in_buf, BUF_SIZE, USB_READ_TIMEOUT);
		if (res > 0 && in_buf[1] != 'z') {
                        snprintf(usb_thread_log_buf, BUF_SIZE, "[usb] IN msg of %d bytes: %X %c %X\n", res, in_buf[0], in_buf[1], (char)in_buf[2]);
			LOG(usb_thread_log_buf);
		}
	} while (res > 0);
}

void sendMessage(char command, char *data, int len) {
	int r = 0;
	int i = 0;
	
	if (len > BUF_SIZE - 2) {
		LOG("[usb] Failed writing: TOO MUCH DATA! FIXME, split it !\n");
		return;
	}
	memset(out_buf, 0, BUF_SIZE);
	out_buf[0] = len + 1;
	out_buf[1] = command;
	for (i = 0; i < len; ++i) {
		out_buf[2 + i] = data[i];
	}
	r = acc.write(out_buf, len + 2);
	if (r <= 0) {
		LOG("[usb] Failed writing\n");
	} else {
		//fprintf(stderr, "sent!\n");
	}
}

static msg_t Thread1(void *arg) {
    (void)arg;
    char random_data;
    
    while (1) {
        if (acc.isConnected()) {
            readMessage(); // read timeout induce delay here...
            random_data = rand() % 256;
            sendMessage('M', &random_data, 1);
        } else {
            // Not connected...
            // Sleep a bit before next scan...
            chThdSleepMilliseconds(SLEEP_DELAY_NOT_CONNECTED);
        }
        // Sleep a bit.
        chThdSleepMilliseconds(SLEEP_DELAY);
    }
    return 0;
}
//------------------------------------------------------------------------------
// thread 2 - print main thread count every second
// 100 byte stack beyond task switch and interrupt needs
static WORKING_AREA(waThread2, 100);

static msg_t Thread2(void *arg) {
    (void)arg;
    char buf[10];
    
    // print count every second
    while (1) {
         // Print count for previous second.
         LOG("[dumb Task] count=");
         snprintf(buf, 10, "%#X\n", count);
         LOG(buf);
         
#if defined(ARDUINO)
         // Print unused stack for threads.
         LOG(", Unused Stack: ");
         LOG(chUnusedStack(waThread1, sizeof(waThread1)));
         LOG(" ");
         LOG(chUnusedStack(waThread2, sizeof(waThread2))); 
         LOG(" ");
         LOG(chUnusedHeapMain());
         LOG("\n");
#endif  // ARDUINO
        
        // Zero count.
        count = 0;
        // Sleep for one second.
        chThdSleepMilliseconds(1000);
        
    }
}
//------------------------------------------------------------------------------
// main thread runs at NORMALPRIO
void mainThread() {
    // Console thread started.
    chThdCreateStatic(waLogThread, sizeof(waLogThread), NORMALPRIO + 1,
                      print_thread, NULL);
    
    // start blink thread
    chThdCreateStatic(waThread1, sizeof(waThread1), NORMALPRIO + 2,
                      Thread1, NULL);
    
    // start print thread
    chThdCreateStatic(waThread2, sizeof(waThread2), NORMALPRIO + 1,
                      Thread2, NULL);
    
    // increment counter
    while (1) {
        count++;
#if defined(SIMULATOR)
        ChkIntSources();
#endif
    }
}
//------------------------------------------------------------------------------
extern "C"{
void setup() {
#if defined(ARDUINO)
    Serial.begin(9600);
#endif  // ARDUINO
    acc.powerOn();
    
    chBegin(mainThread);
    // chBegin never returns, main thread continues with mainThread()
    while(1) {
#if defined(SIMULATOR)
        out("Error: trapped on setup()\n");
#endif
    }
}
//------------------------------------------------------------------------------
void loop() {
    // not used
}
//------------------------------------------------------------------------------
void quit() {
#if defined(SIMULATOR)
	acc.close();
#endif
}
} // extern "C"
