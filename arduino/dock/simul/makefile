################################################################
#
# Add your sources in sources.mk
# This file should not be edited, unless for a good reason :)
#
###############################################################
CXX=g++
CC=g++
CXXFLAGS+=$(IDIR) -g -MMD
CFLAGS+=$(IDIR) -g -MMD
LDFLAGS+=-g
LDLIBS+=
RM=rm -f

ODIR=gen
LDIR=lib

# The binary we are building
BINARY=dockSimulator.app

# Libraries and includes
LIBS:=-L/opt/local/lib -lusb-1.0
IDIR=-I/usr/include/ -I/opt/local/include/libusb-1.0

# c and cpp source files
SRCS_CPP=
SRCS_C=
-include sources.mk

CPP_OBJS=$(subst .cpp,.o,$(SRCS_CPP))
C_OBJS=$(subst .c,.o,$(SRCS_C))
OBJS=$(CPP_OBJS) $(C_OBJS)

all: $(BINARY)

$(BINARY): $(OBJS)
	$(CXX) $(LDFLAGS) -o $@ $^ $(CFLAGS) $(LDLIBS) $(LIBS)
              
.PHONY: clean

clean:
	$(RM) $(OBJS) $(OBJS:%.o=%.d) *~ $(LDIR)/*~ .depend
	
dist-clean: clean
	$(RM) $(BINARY)

-include $(OBJS:%.o=%.d)