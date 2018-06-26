/******************************************************************************
 *
 * File Name:
 *
 *     AO64eintface.h
 *
 * Description:
 *
 *     This file contains all the AO16 Driver function prototypes & defines.
 *
 * Revision:
 *
 *     11-01-12 : AO64 Driver 1.0
 *
 ******************************************************************************/
#ifdef  __cplusplus
extern "C" {
#endif

// Board Register Addresses
#define       BCR                0x00
#define       Reserved           0x04
#define       Reserved1          0x08
#define       BUFFER_OPS         0x0C
#define       FW_REV             0x10
#define       AUTO_CAL           0x14
#define       OUTPUT_DATA_BUFFER 0x18
#define       BUFFER_SIZE        0x1C
#define       BUFFER_THRSHLD     0x20
#define       RATE_A             0x24
#define       RATE_B             0x28

// Bit Definitions
#define		  CLK_INITIATOR   	 0x0001
#define		  TRIG_INITIATOR   	 0x0002
#define		  DISCONNECT_OUT   	 0x0004
#define		  REMOTE_GND_SENSE	 0x0008
#define		  OFFSET_BINARY		 0x0010
#define		  EXT_CLK   		 0x0001
#define		  EXT_TRIG   		 0x0002
#define		  ENABLE_CLK   		 0x0020
#define		  CLK_RDY   		 0x0040
#define		  OPEN_BUFF			 0x0000
#define		  CIRC_BUFF   		 0x0100
#define		  LOAD_REQ			 0x0200
#define		  LOAD_RDY			 0x0400
#define		  CLR_BUFFER		 0x0800

#define       LOCAL              0
#define       DMA			     1

#define		  MINUS_ONE_LONG     0xFFFFFFFFL

typedef struct _GS_NOTIFY_OBJECT
{
    U32 IsValidTag;                  // Magic number to determine validity
    U64 pWaitObject;                 // -- INTERNAL -- Wait object used by the driver
    U64 hEvent;                      // User event handle (HANDLE can be 32 or 64 bit)
} GS_NOTIFY_OBJECT;

typedef struct _GS_PHYSICAL_MEM
{
    U64 UserAddr;                    // User-mode virtual address
    U64 PhysicalAddr;                // Bus physical address
    U64 CpuPhysical;                 // CPU physical address
    U32 Size;                        // Size of the buffer
} GS_PHYSICAL_MEM;

typedef struct _GS_DMA_DESCRIPTOR
{
	U32 BytesDesc_1;				 // BYTES to transfer (U32 = 4 Bytes)
	U32 BytesDesc_2;				 // Max transfer size is 23 bits
	U32 BytesDesc_3;				 // such that max U32 transfer size
	U32 BytesDesc_4;				 // is (2097151) 0x1FFFFF
	U64 PhyAddrDesc_1;				 // Valid PHYSICAL address for contiguous
	U64 PhyAddrDesc_2;				 // memory block
	U64 PhyAddrDesc_3;				 // DO NOT use an address obtained from
	U64 PhyAddrDesc_4;				 // malloc() or similiar functions
	U32 NumDescriptors	 : 4;		 // Number of Descriptor blocks to use
	U8  LocalToPciDesc_1 : 1;		 // NOTE: Valid info MUST be provided for
	U8  LocalToPciDesc_2 : 1;		 // the # of Descriptor blocks specified
	U8  LocalToPciDesc_3 : 1;
	U8  LocalToPciDesc_4 : 1;		 // Direction of Transfer : 1 = Local->Pci
	U8  InterruptDesc_1  : 1;		 // Enable interrupt at Descriptor completion
	U8  InterruptDesc_2  : 1;
    U8  InterruptDesc_3  : 1;
	U8  InterruptDesc_4  : 1;
    U8  DmaChannel       : 1;        // DMA Channel 0 or 1
    U64 Rsvrd;
} GS_DMA_DESCRIPTOR;


//Function declarations

//==============================================================================
//	Find ALL Devices
//  Sets Board #: Bus: Slot: SSID: Type  in pDeviceInfo for PLX boards found
//  Returns # Boards found
//==============================================================================
U32 __cdecl AO64_66_FindBoards(char *pDeviceInfo, U32 *ulError);


//==============================================================================
//
//  Initializes Handle for the passed board number IN THE DRIVER
//  
//==============================================================================
U32 __cdecl AO64_66_Get_Handle(U32 *ulError, U32 BoardNumber);


//==============================================================================
//
//  Closes Handle for the passed board number IN THE DRIVER
//  
//==============================================================================
void __cdecl AO64_66_Close_Handle(U32 BoardNumber, U32 *ulError);


//==============================================================================
//	Read Local Register Data from the Device (32Bit)
//
//  See Users Manual for register bit definitions
//
//  Returns register value 
//==============================================================================
U32 __cdecl AO64_66_Read_Local32(U32 BoardNumber, U32 *ulError, U32 ulRegister);


//==============================================================================
//	Write Local Register Data to the Device (32Bit)
//
//  See Users Manual for register bit definitions
//
//==============================================================================
void __cdecl AO64_66_Write_Local32(U32 BoardNumber, U32 *ulError, U32 ulRegister, U32 uiValue);


//==============================================================================
//
//	Initializes the board, i.e. reset
//
//==============================================================================
void __cdecl AO64_66_Initialize(U32 BoardNumber, U32 *ulErrorIn);


//==============================================================================
//
//	Initiates Auto Calibration 
//  Returns 0x55 for insufficent resources
//  Returns 0xAA for interrupt timeout or 0/1 for autocal status
//
//==============================================================================
U32 __cdecl AO64_66_Autocal(U32 BoardNumber, U32 *ulError);


//==============================================================================
//
//	Clears the Analog Output buffer
//
//==============================================================================
void __cdecl AO64_66_Clear_Buffer(U32 BoardNumber, U32 *ulErrorIn);


//==============================================================================
//
//	Sets Sample rate for the group - Pass the desired rate (2.93 - 500000.0Hz)
//  Returns the ACTUAL Rate based on Clock frequency selected.
//==============================================================================
double __cdecl AO64_66_Set_Sample_Rate(U32 BoardNumber, double fRate, U32 *ulError);


//==============================================================================
//	Enable Interrupts for the Device
//
//  ulType = 0 : Local Interrupt
//               ulValue - See Users Manual for value equates
//  ulType = 1 : DMA Interrupts DMA0 or DMA1
//               ulValue = 0 for DMA0 : 1 for DMA1 
//
//  Returns Interrupt value set for Local Interrupts, or ulValue for DMA
//==============================================================================
U32 __cdecl AO64_66_EnableInterrupt(U32 BoardNumber, U32 ulValue, U32 ulType, U32 *ulError);

//==============================================================================
//	Disable Interrupts for the Device
//
//  ulType = 0 : Local Interrupt
//               ulValue - See Users Manual for value equates
//  ulType = 1 : DMA Interrupts DMA0 or DMA1
//               ulValue = 0 for DMA0 : 1 for DMA1 
//
//==============================================================================
void __cdecl AO64_66_DisableInterrupt(U32 BoardNumber, U32 ulValue, U32 ulType, U32 *ulError);

//==============================================================================
//
//	Registers interrupt event for user notification
//  ulType = 0 : Local Interrupt
//               ulIntr - Not Used
//  ulType = 1 : DMA Interrupts DMA0 or DMA1
//               ulIntr = 0 for DMA0 : 1 for DMA1 
//
//==============================================================================
void __cdecl AO64_66_Register_Interrupt_Notify(U32 BoardNumber, GS_NOTIFY_OBJECT * event, U32 ulIntr, U32 ulType, U32 *ulError);

//==============================================================================
//	Cancels interrupt notification for specified event
//
//==============================================================================
void __cdecl AO64_66_Cancel_Interrupt_Notify(U32 BoardNumber, GS_NOTIFY_OBJECT * event, U32 *ulError);

//==============================================================================
//	Open the DMA Channel for the Device
//==============================================================================
void __cdecl AO64_66_Open_DMA_Channel(U32 BoardNumber, U32 ulChannel, U32 *ulError);


//==============================================================================
//	DMA Transfer for the Device
//
//  Returns # words transferred
//==============================================================================
U32 __cdecl AO64_66_DMA_Transfer(U32 BoardNumber, U32 ulChannel, U32 ulWords, U32* uData,U32 *ulError);


//==============================================================================
//	Close the DMA Channel for the Device
//
//==============================================================================
void __cdecl AO64_66_Close_DMA_Channel(U32 BoardNumber, U32 ulChannel, U32 *ulError);

//==============================================================================
//	Enable Output Clock
//
//==============================================================================
void __cdecl AO64_66_Enable_Clock(U32 BoardNumber, U32 *ulError);

//==============================================================================
//	Disable Output Clock
//
//==============================================================================
void __cdecl AO64_66_Disable_Clock(U32 BoardNumber, U32 *ulError);

//==============================================================================
//	Setup CmdChaining DMA for the Device
//
//==============================================================================
U32 __cdecl AO64_66_Setup_DmaCmdChaining(U32 BoardNumber, GS_DMA_DESCRIPTOR *DmaSetup, U32 *ulError);

//==============================================================================
//	Start CmdChaining DMA for the Device
//
//==============================================================================
U32 __cdecl AO64_66_Start_DmaCmdChaining(U32 BoardNumber, U32 ulChannel, U32 *ulError);

//==============================================================================
//	Close CmdChaining DMA for the Device
//
//==============================================================================
void __cdecl AO64_66_Close_DmaCmdChaining(U32 BoardNumber, U32 ulChannel, U32 *ulError);

//==============================================================================
//	Get block of contiguous Physical Memory
//  and map to virtual memory
//==============================================================================
U32 __cdecl AO64_66_Get_Physical_Memory(U32 BoardNumber, GS_PHYSICAL_MEM *memPtr, BOOLEAN bSmallerOk, U32 *ulError);

//==============================================================================
//	Umap & Free Physical Memory
//  
//==============================================================================
void __cdecl AO64_66_Free_Physical_Memory(U32 BoardNumber, GS_PHYSICAL_MEM *memPtr, U32 *ulError);


#ifdef  __cplusplus
}
#endif
