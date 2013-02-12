################################################################################
# Automatically-generated file. Do not edit!
################################################################################

-include ../makefile.init

RM := rm -rf

# All of the sources participating in the build are defined here
#-include sources.mk
O_SRCS := 
C_SRCS := 
S_UPPER_SRCS := 
OBJ_SRCS := 
ASM_SRCS := 
OBJS := 
C_DEPS := 
EXECUTABLES := 

# Every subdirectory with source files must be described here
SUBDIRS := \
src \

-include subdir.mk


#-include objects.mk
USER_OBJS :=
LIBS := -L/opt/local/lib -lusb-1.0


ifneq ($(MAKECMDGOALS),clean)
ifneq ($(strip $(C_DEPS)),)
-include $(C_DEPS)
endif
endif

-include ../makefile.defs

# Add inputs and outputs from these tool invocations to the build variables 

# All Target
all: dock

# Tool invocations
USBCANgw: $(OBJS) $(USER_OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	gcc  -o "dock" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(OBJS)$(C_DEPS)$(EXECUTABLES) dock
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets
