/*
 * simplectrl.c
 * This file is part of OsciPrime
 *
 * Copyright (C) 2011 - Manuel Di Cerbo
 *
 * OsciPrime is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * OsciPrime is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OsciPrime; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */
#include <stdio.h>
#include <unistd.h>
#include <libusb.h>
#include <string.h>
#include <stdint.h>
#include <errno.h>

#include "usb.h"
/*
#define IN 0x85
#define OUT 0x07

#define ACCESSORY_PID 0x2D01
#define ACCESSORY_PID_ALT 0x2D00

#define LEN 2

//static
static struct libusb_device_handle* handle;
static char stop;
static char success = 0;

int mainPhase()
{
	unsigned char buffer[500000];
	int response = 0;
	static int transferred;

	response = libusb_bulk_transfer(handle,IN,buffer,16384, &transferred,0);
	if(response < 0){error(response);return -1;}

	response = libusb_bulk_transfer(handle,IN,buffer,500000, &transferred,0);
	if(response < 0){error(response);return -1;}

	return 0;
}


int init()
{
	libusb_init(NULL);
	if((handle = libusb_open_device_with_vid_pid(NULL, VID, PID)) == NULL)
	{
		fprintf(stdout, "Problem acquiring handle\n");
		return -1;
	}
	libusb_claim_interface(handle, 0);
	return 0;
}

int deInit()
{
	//TODO free all transfers individually...
	//if(ctrlTransfer != NULL)
	//	libusb_free_transfer(ctrlTransfer);
	if(handle != NULL)
		libusb_release_interface (handle, 0);
	libusb_exit(NULL);
	return 0;
}

int setupAccessory(
	const char* manufacturer,
	const char* modelName,
	const char* description,
	const char* version,
	const char* uri,
	const char* serialNumber)
{
	unsigned char ioBuffer[2];
	int devVersion;
	int response;
	int i = 0;

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

	if(response < 0)
	{
		error(response);
		return-1;
	}

	devVersion = ioBuffer[1] << 8 | ioBuffer[0];
	fprintf(stdout, "Version Code Device: %d\n", devVersion);

	usleep(1000);//sometimes hangs on the next transfer :(

	response = libusb_control_transfer(handle,0x40,52,0,0,(char*)manufacturer,strlen(manufacturer),0);
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
	if(response < 0)
	{
		error(response);
		return -1;
	}

	fprintf(stdout,"Attempted to put device into accessory mode\n");

	if(handle != NULL)
		libusb_release_interface (handle, 0);

	for(i = 30; ;i--)
	{//attempt to connect to new PID, if that doesn't work try ACCESSORY_PID_ALT
		if((handle = libusb_open_device_with_vid_pid(NULL, VID, ACCESSORY_PID_ALT)) == NULL)
		{
			if(i < 0)
			{
				fprintf(stdout, "Error setting up accessory function \n");
				return -1;
			}
		}
		else
		{
			break;
		}
		sleep(1);
	}
	libusb_claim_interface(handle, 0);
	fprintf(stdout, "Interface claimed, ready to transfer data\n");
	return 0;
}


void error(int code)
{
	fprintf(stdout,"\n");
	switch(code)
	{
	case LIBUSB_ERROR_IO:
		fprintf(stdout,"Error: LIBUSB_ERROR_IO\nInput/output error.\n");
		break;
	case LIBUSB_ERROR_INVALID_PARAM:
		fprintf(stdout,"Error: LIBUSB_ERROR_INVALID_PARAM\nInvalid parameter.\n");
		break;
	case LIBUSB_ERROR_ACCESS:
		fprintf(stdout,"Error: LIBUSB_ERROR_ACCESS\nAccess denied (insufficient permissions).\n");
		break;
	case LIBUSB_ERROR_NO_DEVICE:
		fprintf(stdout,"Error: LIBUSB_ERROR_NO_DEVICE\nNo such device (it may have been disconnected).\n");
		break;
	case LIBUSB_ERROR_NOT_FOUND:
		fprintf(stdout,"Error: LIBUSB_ERROR_NOT_FOUND\nEntity not found.\n");
		break;
	case LIBUSB_ERROR_BUSY:
		fprintf(stdout,"Error: LIBUSB_ERROR_BUSY\nResource busy.\n");
		break;
	case LIBUSB_ERROR_TIMEOUT:
		fprintf(stdout,"Error: LIBUSB_ERROR_TIMEOUT\nOperation timed out.\n");
		break;
	case LIBUSB_ERROR_OVERFLOW:
		fprintf(stdout,"Error: LIBUSB_ERROR_OVERFLOW\nOverflow.\n");
		break;
	case LIBUSB_ERROR_PIPE:
		fprintf(stdout,"Error: LIBUSB_ERROR_PIPE\nPipe error.\n");
		break;
	case LIBUSB_ERROR_INTERRUPTED:
		fprintf(stdout,"Error:LIBUSB_ERROR_INTERRUPTED\nSystem call interrupted (perhaps due to signal).\n");
		break;
	case LIBUSB_ERROR_NO_MEM:
		fprintf(stdout,"Error: LIBUSB_ERROR_NO_MEM\nInsufficient memory.\n");
		break;
	case LIBUSB_ERROR_NOT_SUPPORTED:
		fprintf(stdout,"Error: LIBUSB_ERROR_NOT_SUPPORTED\nOperation not supported or unimplemented on this platform.\n");
		break;
	case LIBUSB_ERROR_OTHER:
		fprintf(stdout,"Error: LIBUSB_ERROR_OTHER\nOther error.\n");
		break;
	default:
		fprintf(stdout, "Error: unkown error\n");
	}
}

void status(int code)
{
	fprintf(stdout,"\n");
	switch(code){
		case LIBUSB_TRANSFER_COMPLETED:
			fprintf(stdout,"Success: LIBUSB_TRANSFER_COMPLETED\nTransfer completed.\n");
			break;
		case LIBUSB_TRANSFER_ERROR:
			fprintf(stdout,"Error: LIBUSB_TRANSFER_ERROR\nTransfer failed.\n");
			break;
		case LIBUSB_TRANSFER_TIMED_OUT:
			fprintf(stdout,"Error: LIBUSB_TRANSFER_TIMED_OUT\nTransfer timed out.\n");
			break;
		case LIBUSB_TRANSFER_CANCELLED:
			fprintf(stdout,"Error: LIBUSB_TRANSFER_CANCELLED\nTransfer was cancelled.\n");
			break;
		case LIBUSB_TRANSFER_STALL:
			fprintf(stdout,"Error: LIBUSB_TRANSFER_STALL\nFor bulk/interrupt endpoints: halt condition detected (endpoint stalled).\nFor control endpoints: control request not supported.\n");
			break;
		case LIBUSB_TRANSFER_NO_DEVICE:
			fprintf(stdout,"Error: LIBUSB_TRANSFER_NO_DEVICE\nDevice was disconnected.\n");
			break;
		case LIBUSB_TRANSFER_OVERFLOW:
			fprintf(stdout,"Error: LIBUSB_TRANSFER_OVERFLOW\nDevice sent more data than requested.\n");
			break;
		default:
			fprintf(stdout,"Error: unknown error\nTry again(?)\n");
			break;
	}
}
*/

#define ADK_GOOGLE_VID 				0x18d1
#define ADK_GOOGLE_PID 				0x2d00
#define ADK_GOOGLE_ADB_PID 		0x2d01

#define ADK_GOOGLE_PID_2 			0x2d04
#define ADK_GOOGLE_ADB_PID_2 	0x2d05


#define ADK_REENUMERATION_DELAY 2

#define ADK_STRING_MANUFACTURER 0
#define ADK_STRING_MODEL 1
#define ADK_STRING_DESCRIPTION 2
#define ADK_STRING_VERSION 3
#define ADK_STRING_URI 4
#define ADK_STRING_SERIAL 5

#define ADK_COMMAND_GETPROTOCOL 51
#define ADK_COMMAND_SENDSTRING 52
#define ADK_COMMAND_START 53

static libusb_device_handle *adk = NULL;
static uint8_t ep_in = 0, ep_out = 0;

#define TIMEOUT 1

int __adk_send_string(libusb_device_handle *handle, int index, const char *s)
{
	int r;

	r = libusb_control_transfer(handle,
									LIBUSB_ENDPOINT_OUT | LIBUSB_REQUEST_TYPE_VENDOR |
									LIBUSB_RECIPIENT_DEVICE, ADK_COMMAND_SENDSTRING, 0, index,
									(void *) s, strlen(s), 0);
	if (r != strlen(s))
	{
		return -EINVAL;
	}
	return 0;
}

int adk_open(uint16_t vid, uint16_t pid)
{
	int						r;
	int 					i;
	int 					accessory_mode = 1;
	libusb_device_handle 	*handle;
	struct libusb_config_descriptor 		*cfg = NULL;
	struct libusb_interface_descriptor 	*ifdsc = NULL;

	libusb_init(NULL);

	handle = libusb_open_device_with_vid_pid(NULL, ADK_GOOGLE_VID, ADK_GOOGLE_ADB_PID);
	if (handle == NULL)
	{
		fprintf(stderr, "device not found(%d, %d)\n", ADK_GOOGLE_VID, ADK_GOOGLE_ADB_PID);
		handle = libusb_open_device_with_vid_pid(NULL, ADK_GOOGLE_VID, ADK_GOOGLE_ADB_PID_2);
		if (handle == NULL)
		{
			handle = libusb_open_device_with_vid_pid(NULL, vid, pid);
			accessory_mode = 0;
		}
	}

	if (handle == NULL)
	{
		fprintf(stderr, "device not found(%d, %d)\n", vid, pid);
		return -ENOENT;
	}
	
	libusb_claim_interface(handle, 0);

	if (accessory_mode == 0)
	{
		/* try to switch to the accessory mode */
		uint16_t version;
		r = libusb_control_transfer(handle,
										LIBUSB_ENDPOINT_IN | LIBUSB_REQUEST_TYPE_VENDOR |
										LIBUSB_RECIPIENT_DEVICE, ADK_COMMAND_GETPROTOCOL, 0, 0,
										(void *) &version, sizeof(version), 0);

		if (r != sizeof(version) || le32toh(version) != 2)
		{
			fprintf(stderr, "GetProtocol command is not supported : %d\n", version);
			return -ENOTSUP;
		}
		fprintf(stderr, "found device, ADK version is %d\n", le32toh(version));

		/* send strings */
		r = __adk_send_string(handle, ADK_STRING_MANUFACTURER, ACCESSORY_STRING_VENDOR);
		if (r < 0)
		{
			fprintf(stderr, "failed to send manufacturer string: %d\n", r);
			return r;
		}
		r = __adk_send_string(handle, ADK_STRING_MODEL, ACCESSORY_STRING_NAME);
		if (r < 0) {
						fprintf(stderr, "failed to send model string: %d\n", r);
						return r;
		}
		r = __adk_send_string(handle, ADK_STRING_DESCRIPTION, ACCESSORY_STRING_LONGNAME);
		if (r < 0) {
						fprintf(stderr, "failed to send description string: %d\n", r);
						return r;
		}
		r = __adk_send_string(handle, ADK_STRING_VERSION, ACCESSORY_STRING_VERSION);
		if (r < 0) {
						fprintf(stderr, "failed to send version string: %d\n", r);
						return r;
		}
		r = __adk_send_string(handle, ADK_STRING_URI, ACCESSORY_STRING_URL);
		if (r < 0) {
						fprintf(stderr, "failed to send URI string: %d\n", r);
						return r;
		}
		r = __adk_send_string(handle, ADK_STRING_SERIAL, ACCESSORY_STRING_SERIAL);
		if (r < 0) {
						fprintf(stderr, "failed to send serial string: %d\n", r);
						return r;
		}

		fprintf(stderr, "starting accessory mode\n");
		r = libusb_control_transfer(handle,
										LIBUSB_ENDPOINT_OUT | LIBUSB_REQUEST_TYPE_VENDOR |
										LIBUSB_RECIPIENT_DEVICE, ADK_COMMAND_START, 0, 0, NULL, 0, 0);
		if (r != 0) {
						return -EINVAL;
		}

		libusb_release_interface(handle, 0);
		libusb_close(handle);

		fprintf(stderr, "started. now re-opening the device\n");
		/* re-open the device after enumeration */
		sleep(ADK_REENUMERATION_DELAY);
		return adk_open(0xffff, 0xffff);
	}
	else
	{
		fprintf(stderr, "found device in accessory mode\n");

		/* configure accessory */

		r = libusb_get_active_config_descriptor(libusb_get_device(handle), &cfg);
		ifdsc = &cfg->interface[0].altsetting[0];
		/*
		for (i = 0; i < ifdsc->bNumEndpoints; i++)
		{
			uint8_t ep = ifdsc->endpoint[i].bEndpointAddress;
			if ((ep & 0x7f) && ep_in == 0)
			{
				ep_in = ep;
			}
			if (((ep & 0x7f) == 0) && ep_out == 0)
			{
				ep_out = ep;
			}
		}
		*/
		ep_in = ifdsc->endpoint[0].bEndpointAddress;
		ep_out = ifdsc->endpoint[1].bEndpointAddress;
		fprintf(stderr, "adk configured\n");
	}
	adk = handle;
	return 0;
}

void adk_close()
{
	libusb_exit(NULL);
}

int adk_read(void *buf, size_t len)
{
	int out = 0;

	libusb_bulk_transfer(adk, ep_in, buf, len, &out, TIMEOUT);
	return out;
}

int adk_write(void *buf, size_t len)
{
	int out;

	libusb_bulk_transfer(adk, ep_out, buf, len, &out, TIMEOUT);
	return out;
}
