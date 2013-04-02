#include <signal.h>
#include <stdlib.h>
#include <unistd.h>
#include "wrapper.h"

extern void setup();
extern void loop();
extern void quit();

static void sig_hdlr(int signum) {
	switch (signum) {
    	case SIGINT:
            quit();
            exit(1);
            break;
    }
}

int main(void)
{
	signal(SIGINT, &sig_hdlr);
	setup();

	while(true) {
		loop();	
	}

	return 0;
}
