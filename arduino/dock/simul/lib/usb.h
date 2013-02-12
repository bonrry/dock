#ifndef USB_H_
#define USB_H_

#define ACCESSORY_STRING_VENDOR   "telethonSommeil"
#define ACCESSORY_STRING_NAME     "telethonSommeil"
#define ACCESSORY_STRING_LONGNAME "telethonSommeil Dock"
#define ACCESSORY_STRING_VERSION  "1.0"
#define ACCESSORY_STRING_URL      "http://telethon.bemyapp.com/"
#define ACCESSORY_STRING_SERIAL   "0000000012345678"

#define ACCESSORY_PID 0x2D01
#define ACCESSORY_PID_ALT 0x2D00

#define PHONE_VID 0x04E8 /* Samsung */
#define PHONE_PID 0x6860 /* Galaxy S3 */

void adk_close();

int adk_read(void *buf, size_t len);
int adk_write(void *buf, size_t len);
int adk_open(uint16_t vid, uint16_t pid);

#endif /* USB_H_ */
