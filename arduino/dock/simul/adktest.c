#include <stdio.h>
#include <libusb.h>
#include <string.h>

int ENDPOINT_BULK_IN = 0;//0x83;
int ENDPOINT_BULK_OUT = 0;//0x03; // Have tried 0x00, 0x01 and 0x02

#define VID 0x18D1
#define PID 0x4E11

#define ACCESSORY_PID 0x2D00
#define ACCESSORY_ADB_PID 0x2D01 // Can't get this to work, if ADB is active, can't get handle on device

#define S3_VID 					0x04E8 /* Samsung */
#define S3_PID 					0x6860 /* Galaxy S3 */
#define ADK_GOOGLE_VID 			0x18d1
#define ADK_GOOGLE_PID 			0x2d00
#define ADK_GOOGLE_ADB_PID 		0x2d01
#define ADK_GOOGLE_PID_2 		0x2d04
#define ADK_GOOGLE_ADB_PID_2 	0x2d05

/*
ON OSX
gcc adktest.c -I/usr/include -o adktest -lusb-1.0.0 -I/usr/include -I/opt/local/include/libusb-1.0 -L/opt/local/lib
ON UBUNTU
gcc adktest.c -I/usr/include -o adktest -lusb-1.0 -I/usr/include -I/usr/include/libusb-1.0

Testing on Nexus One with Gingerbread 2.3.4
*/

static int transferTest();
static int init(void);
static int shutdown(void);
static void error(int code);
static void status(int code);
static int setupAccessory(
const char* manufacturer,
const char* modelName,
const char* description,
const char* version,
const char* uri,
const char* serialNumber);

int pid[4] = { ADK_GOOGLE_PID, ADK_GOOGLE_ADB_PID, ADK_GOOGLE_PID_2, ADK_GOOGLE_ADB_PID_2 };

//static
static struct libusb_device_handle* handle;

void error(int code) {
}

int main (int argc, char *argv[]){
	if(init() < 0)
	    return;
	    
	if(setupAccessory(
	    "telethonSommeil",
	    "telethonSommeil",
	    "telethonSommeil Dock",
	    "1.0",
	    "http://telethon.bemyapp.com/",
	    "0000000012345678") < 0) {
	    fprintf(stdout, "Error setting up accessory\n");
	    shutdown();
	    return -1;
	};
	if(transferTest() < 0){
	    fprintf(stdout, "Error in transferTest\n");
	    shutdown();
	    return -1;
	}   
	shutdown();
	fprintf(stdout, "Finished\n");
	return 0;
}

static int transferTest(){
  // TEST BULK IN/OUT
  const static int PACKET_BULK_LEN=64;
  const static int TIMEOUT=200;
  int r,i;
  int transferred;
  char answer[PACKET_BULK_LEN];
  char question[PACKET_BULK_LEN];
  for (i=0;i<PACKET_BULK_LEN; i++) question[i]=i;

    // ***FAILS HERE***
    r = libusb_bulk_transfer(handle, ENDPOINT_BULK_OUT, question, PACKET_BULK_LEN,
                             &transferred, TIMEOUT);
    if (r < 0) {
        fprintf(stderr, "Bulk write error %d\n", r);
        error(r);
        return r;
    }
    fprintf(stdout, "Wrote %d bytes", transferred);

    r = libusb_bulk_transfer(handle, ENDPOINT_BULK_IN, answer,PACKET_BULK_LEN,
                             &transferred, TIMEOUT);
    if (r < 0) {
        fprintf(stderr, "Bulk read error %d\n", r);
        error(r);
        return r;
    }
    fprintf(stdout, "Read %d bytes", transferred);

    if (transferred < PACKET_BULK_LEN) {
        fprintf(stderr, "Bulk transfer short read (%d)\n", r);
        error(r);
        return -1;
    }
    printf("Bulk Transfer Loop Test Result:\n");
    //     for (i=0;i< PACKET_BULK_LEN;i++) printf("%i, %i,\n ",question[i],answer[i]);
    for(i = 0;i < PACKET_BULK_LEN; i++) {
        if(i%8 == 0)
            printf("\n");
        printf("%02x, %02x; ",question[i],answer[i]);
    }
    printf("\n\n");

    return 0;

}


static int init(){
	libusb_init(NULL);
	/*
	if((handle = libusb_open_device_with_vid_pid(NULL, S3_VID, S3_PID)) == NULL){
	    fprintf(stdout, "Problem acquiring handle\n");
	    return -1;
	}
	*/
	if ((handle = libusb_open_device_with_vid_pid(NULL, ADK_GOOGLE_VID, ADK_GOOGLE_ADB_PID)) == NULL)	{
		fprintf(stderr, "device not found(%d, %d)\n", ADK_GOOGLE_VID, ADK_GOOGLE_ADB_PID);
		if ((handle = libusb_open_device_with_vid_pid(NULL, ADK_GOOGLE_VID, ADK_GOOGLE_ADB_PID_2)) == NULL) {
			handle = libusb_open_device_with_vid_pid(NULL, S3_VID, S3_PID);
		}
	}
	if (handle == NULL) {
		fprintf(stderr, "device not found(%d, %d)\n", S3_VID, S3_PID);
		return -1;
	}
	
	libusb_claim_interface(handle, 0);
	return 0;
}

static int shutdown(){
	if(handle != NULL)
	    libusb_release_interface (handle, 0);
	libusb_exit(NULL);
	return 0;
}

static int setupAccessory(
	const char* manufacturer,
	const char* modelName,
	const char* description,
	const char* version,
	const char* uri,
	const char* serialNumber){
	
	unsigned char ioBuffer[2];
	int devVersion;
	int response;
	struct libusb_config_descriptor *cfg = NULL;
	struct libusb_interface_descriptor *ifdsc = NULL;
	int i;

	response = libusb_control_transfer(
	    handle, //handle
	    0xC0, //bmRequestType
	    51, //bRequest
	    0, //wValue
	    0, //wIndex
	    ioBuffer, //data
	    2, //wLength
	    0 //timeout
	);
	
	if(response < 0){error(response);return-1;}
	
	devVersion = ioBuffer[1] << 8 | ioBuffer[0];
	fprintf(stdout,"Version Code Device: %d\n", devVersion);
	
	usleep(1000);//sometimes hangs on the next transfer :(
	
	response = libusb_control_transfer(handle,0x40,52,0,0,(char*)manufacturer,strlen(manufacturer)+1,0);
	if(response < 0){error(response);return -1;}
	response = libusb_control_transfer(handle,0x40,52,0,1,(char*)modelName,strlen(modelName)+1,0);
	if(response < 0){error(response);return -1;}
	response = libusb_control_transfer(handle,0x40,52,0,2,(char*)description,strlen(description)+1,0);
	if(response < 0){error(response);return -1;}
	response = libusb_control_transfer(handle,0x40,52,0,3,(char*)version,strlen(version)+1,0);
	if(response < 0){error(response);return -1;}
	response = libusb_control_transfer(handle,0x40,52,0,4,(char*)uri,strlen(uri)+1,0);
	if(response < 0){error(response);return -1;}
	response = libusb_control_transfer(handle,0x40,52,0,5,(char*)serialNumber,strlen(serialNumber)+1,0);
	if(response < 0){error(response);return -1;}
	
	fprintf(stdout,"Accessory Identification sent\n", devVersion);
	
	response = libusb_control_transfer(handle,0x40,53,0,0,NULL,0,0);
	if(response < 0){error(response);return -1;}
	
	fprintf(stdout,"Attempted to put device into accessory mode\n", devVersion);
	
	if(handle != NULL) {
	    libusb_release_interface(handle, 0);
		libusb_close(handle);
	}
	
	int tries = 4;
	for(;;){
	    tries--;
	    if((handle = libusb_open_device_with_vid_pid(NULL, VID, pid[tries])) == NULL){
	        if(tries < 0){
	            return -1;
	        }
	    }else{	
	        break;
	    }
	}
	
	libusb_claim_interface(handle, 0);
	response = libusb_get_active_config_descriptor(libusb_get_device(handle), &cfg);
	ifdsc = &cfg->interface[0].altsetting[0];
	if (ifdsc->bNumEndpoints != 2) {
		fprintf(stdout, "Should have two endpoint, found %d\n", ifdsc->bNumEndpoints);
		return -1;
	}
	ENDPOINT_BULK_IN = ifdsc->endpoint[0].bEndpointAddress;
	ENDPOINT_BULK_OUT = ifdsc->endpoint[1].bEndpointAddress;
	fprintf(stderr, "adk configured - in=%d, out=%d\n", ENDPOINT_BULK_IN, ENDPOINT_BULK_OUT);
	fprintf(stdout, "Interface claimed, ready to transfer data\n");
	return 0;
}

// error reporting function left out for brevity