/*
 * Copyright (C) 2013 Jerome et moi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "../wrapper/wrapper.h"
#include "../lib/AndroidAccessory.h"

AndroidAccessory acc("telethonSommeil",
"telethonSommeil",
"telethonSommeil",
"1.0",
"http://telethon.bemyapp.com/",
"0000000012345678");


int timer = 1;

void setup() {
	acc.powerOn();
}

#define BUF_SIZE 64
int m_type = 0;
int m_len = 0;
char m_buf[BUF_SIZE];

void readMessage() {
	int res = 0;
	char *msg;
	
	res = acc.read(m_buf, BUF_SIZE, 1);
	if (res > 0) {
		msg = strndup(m_buf, res);
		fprintf(stderr, "IN msg of %d bytes: %X %c %X\n", res, msg[0], msg[1], (char)msg[2]);
		free(msg);
	}
}

void sendMessage() {
	int 	r = 0;
	char 	msg[BUF_SIZE];
	
	bzero(msg, BUF_SIZE);
	msg[0] = '3';
	msg[1] = 'M';
	msg[2] = rand() % 256;
	r = acc.write(msg, 3);
	if (r <= 0) {
		fprintf(stderr, "Failed writing: r=%d\n", r);
	} else {
		fprintf(stderr, "sent!\n");
	}
}

void loop() {
	int r = 0;
	int	i = 0;

	if (acc.isConnected()) {
		readMessage(); // read timeout induce delay here...

		if (timer % 10000) {
			sendMessage();
		}
	} else {
		// Not connected...
		fprintf(stderr, "Off\n");
		usleep(100000);
	}
	timer++;
}
