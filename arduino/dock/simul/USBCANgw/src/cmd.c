/*
 * cmd.c
 *
 *  Created on: Jan 2, 2013
 *      Author: affleb
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>

#include "cmd.h"
#include "usb.h"



int	cmd_proto_version()
{
	printf("proto version\n");
	return 0;
}

int	cmd_get_sensors()
{
	printf("get sensors\n");
	return 0;
}

int	cmd_file_list()
{
	printf("file list\n");
	return 0;
}

int	cmd_file_delete()
{
	printf("file delete\n");
	return 0;
}

int	cmd_file_open()
{
	printf("file open\n");
	return 0;
}

int	cmd_file_write()
{
	printf("file write\n");
	return 0;
}

int	cmd_file_close()
{
	printf("file close\n");
	return 0;
}

int	cmd_file_uniq_id()
{
	printf("file uniq id\n");
	return 0;
}

int	cmd_bt_name()
{
	printf("bt name\n");
	return 0;
}

int	cmd_bt_pin()
{
	printf("bt pin\n");
	return 0;
}

int	cmd_time()
{
	printf("time\n");
	return 0;
}


struct s_settings
{
	uint8_t alarm1;
	uint8_t alarm2;
	uint8_t alarm3;
	uint8_t brightness;
	uint8_t color1;
	uint8_t color2;
	uint8_t color3;
	uint8_t volume;
};


/**
 *
 * (alarm:u8,u8,u8,brightness:u8,color:u8,u8,u8:volume:u8)
 */
int	cmd_settings()
{
	char 							buf[8];
	struct s_settings	settings;

	settings.brightness = 42;
	settings.volume = 42;
	memcpy(buf, (char *)&settings, 8);

	adk_write(&buf, 8);
	printf("settings\n");
	return 0;
}

/**
 *
 */
int	cmd_alarm_file()
{
	printf("alarm file\n");
	return 0;
}

/**
 *
 */
int	cmd_get_licence()
{
	printf("get licence\n");
	return 0;
}

/**
 *
 */
int	cmd_display_mode()
{
	printf("display mode\n");
	return 0;
}

/**
 *
 */
int	cmd_lock()
{
	char buf[10];

	buf[0] = 2;

	printf("lock\n");
	adk_write(&buf, 1);

	return 0;
}

/**
 *
 */
int cmd_reboot()
{

	return 0;
}



/**
 *
 */
int cmd_CAN_set_id()
{
	return 0;
}

/**
 *
 */
int cmd_CAN_read_id_value()
{
	int id = 0x18ff0000;

	return 0;
}

/**
 *
 */
int cmd_CAN_write_id_value()
{
	int id = 0x18ff0000;

	return 0;
}


