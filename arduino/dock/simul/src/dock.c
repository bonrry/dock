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

AndroidAccessory acc("Titi inc.",
					"dockit",
					"DawnDock - to wake you up happy",
					"0.2",
					"https://plus.google.com/u/0/106874330031036467055",
					"00000000000042");

#define USB_READ_TIMEOUT               20 // in milliseconds
#define SLEEP_DELAY                 30000 // in microseconds
#define SLEEP_DELAY_NOT_CONNECTED 2000000 // in microseconds

int timer = 1;

void setup() {
	acc.powerOn();
}

#define BUF_SIZE 512
char in_buf[BUF_SIZE];
char out_buf[BUF_SIZE];

void readMessage() {
	int res = 0;
	char *msg;
	
	//fprintf(stderr, "read:");
	do {
		res = acc.read(in_buf, BUF_SIZE, USB_READ_TIMEOUT);
		//fprintf(stderr, " done\n");
		if (res > 0 && in_buf[1] != 'z') {
			msg = strndup(in_buf, res);
			fprintf(stderr, "IN msg of %d bytes: %X %c %X\n", res, msg[0], msg[1], (char)msg[2]);
			free(msg);
		}
	} while (res > 0);
}

void sendMessage(char command, char *data, int len) {
	int r = 0;
	int i = 0;
	
	if (len > BUF_SIZE - 2) {
		fprintf(stderr, "Failed writing: TOO MUCH DATA! FIXME, split it !\n", r);
		return;	
	}
	bzero(out_buf, BUF_SIZE);
	out_buf[0] = len + 1;
	out_buf[1] = command;
	for (i = 0; i < len; ++i) {
		out_buf[2 + i] = data[i];	
	}
	r = acc.write(out_buf, len + 2);
	if (r <= 0) {
		fprintf(stderr, "Failed writing: r=%d\n", r);
	} else {
		//fprintf(stderr, "sent!\n");
	}
}

void loop() {
	int r = 0;
	int	i = 0;
	char random_data;

	if (acc.isConnected()) {
		readMessage(); // read timeout induce delay here...

		//if (timer % 10000)
		{
			random_data = rand() % 256;
			sendMessage('M', &random_data, 1);
		}
	} else {
		// Not connected...
		fprintf(stderr, "Off\n");
		usleep(SLEEP_DELAY_NOT_CONNECTED);
	}
	usleep(SLEEP_DELAY);
	timer++;
}

void quit() {
	acc.close();
}