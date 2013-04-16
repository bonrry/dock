#!/bin/sh

ldvarname="LD_LIBRARY_PATH"
host=`uname`
if [ "$host"="Darwin" ] ; then
	ldvarname="DYLD_LIBRARY_PATH"
elif [ "$host"="Linux" ] ; then
	ldvarname="LD_LIBRARY_PATH"
fi

if [ -f out/dockSimulator ]; then
	echo "====================================================="
	echo "STARTING SIMULATOR ON $host. To quit, press Ctrl+c."
	echo "====================================================="
	echo ""
  	eval "$ldvarname=libusb/libusbx-1.0.15-rc3/out/lib/ out/dockSimulator"
else
	echo "=====================================================" 
	echo "    out/dockSimulator binary not found." 
	echo "    To build it, run 'make libusb' and then 'make'" 
	echo "====================================================="
fi
