/*
 * cmd.h
 *
 *  Created on: Jan 2, 2013
 *      Author: affleb
 */

#ifndef CMD_H_
#define CMD_H_

struct s_arg
{
	int nb_arg;
	char** arg;
};

struct s_cmd
{
	int 				id;
	void 				(*func)(struct s_arg*);
	int					nb_arg;
};

#define CMD_GET_PROTO_VERSION 1 // () -> (u8 protocolVersion)
#define CMD_GET_SENSORS 			2 // () -> (sensors:
											// i32,i32,i32,i32,u16,u16,u16,u16,u16,u16,u16,i16,i16,i16,i16,i16,i16)
#define CMD_FILE_LIST 				3 // FIRST: (char name[]) -> (fileinfo or
											// single zero byte) OR NONLATER: ()
											// -> (fileinfo or empty or single
											// zero byte)
#define CMD_FILE_DELETE				4 // (char name[0-255)) -> (char
											// success)
#define CMD_FILE_OPEN					5 // (char name[0-255]) -> (char success)
#define CMD_FILE_WRITE				6 // (u8 data[]) -> (char success)
#define	CMD_FILE_CLOSE				7 // () -> (char success)
#define	CMD_GET_UNIQ_ID				8 // () -> (u8 uniq[16])
#define CMD_BT_NAME						9 // (char name[]) -> () OR () -> (char
										// name[])
#define	CMD_BT_PIN						10 // (char PIN[]) -> () OR () -> (char
										// PIN[])
#define	CMD_TIME							11 // (timespec) -> (char success)) OR () >
										// (timespec)
#define	CMD_SETTINGS					12 // () ->
											// (alarm:u8,u8,u8,brightness:u8,color:u8,u8,u8:volume:u8)
											// or
											// (alarm:u8,u8,u8,brightness:u8,color:u8,u8,u8:volume:u8)
											// > (char success)
#define	CMD_ALARM_FILE				13 // () -> (char file[0-255]) OR (char
											// file[0-255]) > (char success)
#define CMD_GET_LICENSE				14 // () -> (u8 licensechunk[]) OR ()
											// if last sent
#define CMD_DISPLAY_MODE			15 // () -> (u8) OR (u8) -> ()
#define CMD_LOCK							16 // () -> (u8) OR (u8) -> ()

#define	CMD_REBOOT						17

int	cmd_proto_version();
int	cmd_get_sensors();
int	cmd_file_list();
int	cmd_file_delete();
int	cmd_file_open();
int	cmd_file_write();
int	cmd_file_close();
int	cmd_file_uniq_id();
int	cmd_bt_name();
int	cmd_bt_pin();
int	cmd_time();
int	cmd_settings();
int	cmd_alarm_file();
int	cmd_get_licence();
int	cmd_display_mode();
int	cmd_lock();
int	cmd_reboot();

#endif /* !_CMD_H_ */
