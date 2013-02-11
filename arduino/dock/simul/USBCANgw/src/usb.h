/*
 * usb.h
 *
 *  Created on: Dec 31, 2012
 *      Author: affleb
 */

#ifndef USB_H_
#define USB_H_

#define ACCESSORY_STRING_VENDOR "telethonSommeil"
#define ACCESSORY_STRING_NAME   "telethonSommeil"
#define ACCESSORY_STRING_LONGNAME "telethonSommeil Dock"
#define ACCESSORY_STRING_VERSION  "1.0"
#define ACCESSORY_STRING_URL    "http://telethon.bemyapp.com/"
#define ACCESSORY_STRING_SERIAL "0000000012345678"

#define ACCESSORY_PID 0x2D01
#define ACCESSORY_PID_ALT 0x2D00


#define PHONE_VID 0x04E8 /* Samsung */
#define PHONE_PID 0x6860 /* Galaxy S3 */

//int mainPhase();
//int init(void);
//int deInit(void);
//void error(int code);
//void status(int code);
//int setupAccessory(
//	const char* manufacturer,
//	const char* modelName,
//	const char* description,
//	const char* version,
//	const char* uri,
//	const char* serialNumber);

void adk_close();
int adk_read(void *buf, size_t len);
int adk_write(void *buf, size_t len);
int adk_open(uint16_t vid, uint16_t pid);

#if 1 // defined(HAVE_COREFOUNDATION_COREFOUNDATION_H)
	#include <CoreFoundation/CoreFoundation.h>
	#define htole32(x) CFSwapInt32HostToLittle(x)
	#define le32toh(x) CFSwapInt32LittleToHost(x)
#endif

#endif /* USB_H_ */
