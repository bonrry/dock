################################################################
#
# Add your sources in sources.mk
# This file should not be edited, unless for a good reason :)
#
###############################################################
CXX=g++
CC=g++
CXXFLAGS+=$(IDIR) -g -MMD -m32
CFLAGS+=$(IDIR) -g -MMD -m32
LDFLAGS+=-g --32 -m32
LDLIBS+=
RM=rm -rf

UTIL_INSTALL=install
UNAME := $(shell uname)
ifeq ($(UNAME), Darwin)
UTIL_INSTALL=/usr/local/bin/ginstall
endif

ODIR=gen
LDIR=lib
LIBUSB_DIR=external/libusb/libusbx-1.0.15-rc3
LIBUSB_INSTALL_DIR=out
LIBUSB_INSTALL_PATH=$(LIBUSB_DIR)/$(LIBUSB_INSTALL_DIR)
LIBUSB=$(LIBUSB_INSTALL_PATH)/lib/libusb-1.0.a

# The binary we are building
BINARY_DIR=out
BINARY=$(BINARY_DIR)/dockSimulator

# Libraries and includes
LIBS:=-L$(LIBUSB_INSTALL_PATH)/lib -lusb-1.0
IDIR=-I/usr/include/ -I$(LIBUSB_INSTALL_PATH)/includes

# c and cpp source files
SRCS_CPP=
SRCS_C=
-include sources.mk

CPP_OBJS=$(subst .cpp,.o,$(SRCS_CPP))
C_OBJS=$(subst .c,.o,$(SRCS_C))
OBJS=$(CPP_OBJS) $(C_OBJS)

all: $(BINARY)

$(BINARY): $(OBJS)
	mkdir -p $(BINARY_DIR) ; \
	$(CXX) -o $@ $^ $(CXXFLAGS) $(LDLIBS) $(LIBS)

libusb: $(LIBUSB)

$(LIBUSB):
	cd $(LIBUSB_DIR) ; \
	mkdir -p $(LIBUSB_INSTALL_DIR)/lib ; \
	mkdir -p $(LIBUSB_INSTALL_DIR)/includes ; \
	./configure ; \
	$(MAKE) ; \
	/bin/bash libtool --mode=install $(UTIL_INSTALL) -c libusb/libusb-1.0.la '$(CURDIR)/$(LIBUSB_INSTALL_PATH)/lib' ; \
	cp libusb/libusb.h $(LIBUSB_INSTALL_DIR)/includes/ ; \
	cd -

.PHONY: clean

clean:
	$(RM) $(OBJS) $(OBJS:%.o=%.d) *~ $(LDIR)/*~ .depend

clean-libusb:
	cd $(LIBUSB_DIR) ; \
	$(RM) $(LIBUSB_INSTALL_DIR) ; \
	$(MAKE) clean ; \
	cd -
	
dist-clean: clean
	$(RM) $(BINARY_DIR)

-include $(OBJS:%.o=%.d)
