/*
 ============================================================================
 Name        : USBCANgw.c
 Author      : 
 Version     :
 Copyright   : Your copyright notice
 Description : Hello World in C, Ansi-style
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "usb.h"
#include "cmd.h"

static struct s_cmd cmd[] =
{
  {CMD_GET_PROTO_VERSION, cmd_proto_version, 0},
  {CMD_GET_SENSORS, cmd_get_sensors, 0},
  {CMD_FILE_LIST, cmd_file_list, 0},
  {CMD_FILE_DELETE, cmd_file_delete, 0},
  {CMD_FILE_OPEN, cmd_file_open, 0},
  {CMD_FILE_WRITE, cmd_file_write, 0},
  {CMD_FILE_CLOSE, cmd_file_close, 0},
  {CMD_GET_UNIQ_ID, cmd_file_uniq_id, 0},
  {CMD_BT_NAME, cmd_bt_name, 0},
  {CMD_BT_PIN, cmd_bt_pin, 0},
  {CMD_TIME, cmd_time, 0},
  {CMD_SETTINGS, cmd_settings, 0},
  {CMD_ALARM_FILE, cmd_alarm_file, 0},
  {CMD_GET_LICENSE, cmd_get_licence, 0},
  {CMD_DISPLAY_MODE, cmd_display_mode, 0},
  {CMD_LOCK, cmd_lock, 0},

  /* CAN */

  {CMD_REBOOT, cmd_reboot, 0}
};



int timer = 1;

void setup() {
	
}

int opened = false;

#define BUF_SIZE 64
int m_type = 0;
int m_len = 0;
char m_buf[BUF_SIZE];

void readMessage() {
	int res = 0;
	char *msg;
	
	res = adk_read(m_buf, BUF_SIZE);
	if (res > 0) {
		msg = strndup(m_buf, res);
		fprintf(stderr, "IN msg of %d bytes: %s\n", res, msg);
		free(msg);
	}
}

void sendMessage() {
	int 	r = 0;
	char 	msg[BUF_SIZE];
	
	bzero(msg, BUF_SIZE);
	msg[0] = '3';
	msg[1] = 'M';
	msg[2] = 'z';
	r = adk_write(msg, 3);
	if (r <= 0) {
		fprintf(stderr, "Failed writing: r=%d\n", r);
	}	
}

void loop() {
	int r = 0;
	int	i = 0;

	if (opened) {
		
		fprintf(stderr, "begin read\n");
		readMessage(); // read timeout induce delay here...
		fprintf(stderr, "end read\n");

		if (timer % 10000) {
			sendMessage();
			fprintf(stderr, "sent!\n");
		}
		
		
	} else {
		r = adk_open(PHONE_VID, PHONE_PID);
		if (r >= 0) {
			opened = true;
		} else {
			fprintf(stderr, "adk init() error: %d\n", r);
			adk_close();
			usleep(100000);
			opened = false;
		}
	}
	timer++;
}

int main(void)
{
	
	setup();

	while(true) {
		loop();	
	}

	return 0;
}
