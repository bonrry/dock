################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../src/USBCANgw.c \
../src/cmd.c \
../src/usb.c 

OBJS += \
./src/USBCANgw.o \
./src/cmd.o \
./src/usb.o 

C_DEPS += \
./src/USBCANgw.d \
./src/cmd.d \
./src/usb.d 


# Each subdirectory must supply rules for building sources it contributes
src/%.o: ../src/%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I/usr/include/ -I/opt/local/include/libusb-1.0 -O3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


