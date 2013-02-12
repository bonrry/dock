
#include "wrapper.h"

extern void setup();
extern void loop();

int main(void)
{
	
	setup();

	while(true) {
		loop();	
	}

	return 0;
}
