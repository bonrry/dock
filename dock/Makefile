#
#       !!!! Do NOT edit this makefile with an editor which replace tabs by spaces !!!!
#
##############################################################################################
#
# On command line:
#
# make all = Create SIMULATOR project
#
# make arduino = Create project for arduino IDE  => TODO !!!!
#
# make clean = Clean project files.
#
# To rebuild project do "make clean" and "make all".
#

##############################################################################################
# Start of default section
#

UNAME := $(shell uname)

TRGT = 
UTIL_INSTALL=install
CC   = $(TRGT)g++
ifeq ($(UNAME), Darwin)
    CC   = $(TRGT)clang
    CXX  = $(TRGT)clang++
    UTIL_INSTALL=/usr/local/bin/ginstall
endif
AS   = $(TRGT)gcc -x assembler-with-cpp --32

# List all default C defines here, like -D_DEBUG=1
DDEFS = -DSIMULATOR -DSHELL_USE_IPRINTF=FALSE

# List all default ASM defines here, like -D_DEBUG=1
DADEFS =

# List all default directories to look for include files here
DINCDIR = /usr/include

# List the default directory to look for the libraries here
DLIBDIR =

# List all default libraries here
DLIBS =

#
# End of default section
##############################################################################################

##############################################################################################
# Start of user section
#

# Define project name here
PROJECT = dockItSimulator
BINARY_DIR = out
BINARY = $(BINARY_DIR)/$(PROJECT)

# libusb : use our lin, patched for MacOS X and to be compiled for 32bit arch.
LIBUSB_DIR=external/libusb/libusbx-1.0.15-rc3
LIBUSB_INSTALL_DIR=out
LIBUSB_INSTALL_PATH=$(LIBUSB_DIR)/$(LIBUSB_INSTALL_DIR)
LIBUSB=$(LIBUSB_INSTALL_PATH)/lib/libusb-1.0.a

# Define linker script file here
LDSCRIPT =

# List all user C define here, like -D_DEBUG=1
UDEFS =

# Define ASM defines here
UADEFS =

# Imported source files
CHIBIOS = external/ChibiOS
include $(CHIBIOS)/boards/simulator/board.mk
include ${CHIBIOS}/os/hal/hal.mk
include ${CHIBIOS}/os/hal/platforms/Posix/platform.mk
include ${CHIBIOS}/os/ports/GCC/SIMIA32/port.mk
include ${CHIBIOS}/os/kernel/kernel.mk
include ${CHIBIOS}/test/test.mk
include src/sources.mk
include simulator/sources.mk

# List C source files here
SRC  = ${PORTSRC} \
       ${KERNSRC} \
       ${TESTSRC} \
       ${HALSRC} \
       ${PLATFORMSRC} \
       $(BOARDSRC) \
       ${CHIBIOS}/os/various/shell.c \
       ${CHIBIOS}/os/various/chprintf.c \
       ${SIMULATORSRC} \
       ${DOCKITSRC} \

SRC_CPP  = ${SIMULATORSRC_CPP} \
       	   ${DOCKITSRC_CPP}

# List ASM source files here
ASRC =

# List all user directories here
UINCDIR = $(PORTINC) $(KERNINC) $(TESTINC) \
          $(HALINC) $(PLATFORMINC) $(BOARDINC) ${SIMULATORINC} \
          ${DOCKITINC} ${CHIBIOS}/os/various $(LIBUSB_INSTALL_PATH)/includes

# List the user directory to look for the libraries here
ULIBDIR = $(LIBUSB_INSTALL_PATH)/lib

# List all user libraries here
ULIBS = -lusb-1.0

# Define optimisation level here
OPT = -ggdb -O2 -fomit-frame-pointer

#
# End of user defines
##############################################################################################

INCDIR  = $(patsubst %,-I%,$(DINCDIR) $(UINCDIR))
LIBDIR  = $(patsubst %,-L%,$(DLIBDIR) $(ULIBDIR))
DEFS    = $(DDEFS) $(UDEFS)
ADEFS   = $(DADEFS) $(UADEFS)
OBJS    = $(ASRC:.s=.o) $(SRC:.c=.o) $(SRC_CPP:.cpp=.o)
LIBS    = $(DLIBS) $(ULIBS)

ASFLAGS = -Wa,-amhls=$(<:.s=.lst) $(ADEFS)
CPFLAGS = $(OPT) -Wall -Wextra -Wstrict-prototypes -fverbose-asm -m32 $(DEFS)

ifeq ($(UNAME), Darwin)
	OSX_ARCH = -arch i386
	CPFLAGS += $(OSX_ARCH)
	LDFLAGS += -m32 $(OSX_ARCH) $(LIBDIR)
else
  # Linux, or other
  CPFLAGS += -Wa,-alms=$(<:.c=.lst)
  LDFLAGS = --32 -m32 -Wl,-Map=$(BINARY).map,--cref,--no-warn-mismatch $(LIBDIR)
endif

# Generate dependency information
CPFLAGS += -MD -MP -MF .dep/$(@F).d

#
# makefile rules
#

all: libusb $(OBJS) $(BINARY)

# Let make build objects by himself, he knows our system... 
CXXFLAGS += $(INCDIR) $(CPFLAGS)
CFLAGS += $(INCDIR) $(CPFLAGS) 
#%.o : %.cpp
#	$(CC) -c $(CPFLAGS) -I . $(INCDIR) $< -o $@
#
#%.o : %.c
#	$(CC) -c $(CPFLAGS) -I . $(INCDIR) $< -o $@
#
%.o : %.s
	$(AS) -c $(ASFLAGS) $< -o $@

$(BINARY): libusb $(OBJS)
	mkdir -p $(BINARY_DIR) ; \
	$(CXX) $(OBJS) $(LDFLAGS) $(LIBS) -o $@

gcov:
	-mkdir gcov
	$(COV) -u $(subst /,\,$(SRC) $(SRC_CPP))
	-mv *.gcov ./gcov

libusb: $(LIBUSB)

$(LIBUSB):
	cd $(LIBUSB_DIR) ; \
	mkdir -p $(LIBUSB_INSTALL_DIR)/lib ; \
	mkdir -p $(LIBUSB_INSTALL_DIR)/includes ; \
	CFLAGS="-arch i386" LDFLAGS="-arch i386" ./configure ; \
	CFLAGS="-arch i386" LDFLAGS="-arch i386" $(MAKE) ; \
	/bin/bash libtool --mode=install $(UTIL_INSTALL) -c libusb/libusb-1.0.la '$(CURDIR)/$(LIBUSB_INSTALL_PATH)/lib' ; \
	cp libusb/libusb.h $(LIBUSB_INSTALL_DIR)/includes/ ; \
	cd -

clean: clean-libusb                                 
	-rm -f $(OBJS)
	-rm -rf $(BINARY_DIR)
	-rm -f $(BINARY).map
	-rm -f $(SRC:.c=.c.bak)
	-rm -f $(SRC:.c=.lst)
	-rm -f $(SRC_CPP:.cpp=.cpp.bak)
	-rm -f $(SRC_CPP:.cpp=.lst)
	-rm -f $(ASRC:.s=.s.bak)
	-rm -f $(ASRC:.s=.lst)
	-rm -fR .dep

clean-libusb:
	cd $(LIBUSB_DIR) ; \
	rm -rf $(LIBUSB_INSTALL_DIR) ; \
	$(MAKE) clean ; \
	cd -;

#
# Include the dependency files, should be the last of the makefile
#
-include $(shell mkdir .dep 2>/dev/null) $(wildcard .dep/*)

# *** EOF ***
