//------------------------------------------------------------------------------
//------------------------------------------------------------------------------
// 16AO64 Example Code
//------------------------------------------------------------------------------
// Revision History:
//------------------------------------------------------------------------------
// Revision  Date        Name      Comments
// 1.0       09/31/12    Gary      Created
//
//------------------------------------------------------------------------------
//------------------------------------------------------------------------------

#define _WIN32_WINDOWS 0x0500 // Not supported by 95

#include <conio.h>
#include <stdio.h>
#include <wtypes.h>
#include <windows.h>
#include <time.h>
#include "tools.h"
#include "AO64eintface.h"

#pragma warning(disable : 4996)



// Main Routines for the PMC-16AO16
void AO64_Auto_Cal(void);
void AO64_Basic_output_test(void);
void Display_submenu(void);
void Set_AO64_to_0020(void);
void Set_AO64_to_8000(void);
void Set_AO64_to_FFE0(void);
void Set_AO64_to_Walking(void);
void AO64_Sequential_Direct(void);
void AO64_Simultaneous_Direct(void);
void AO64_Continuous_Function(void);
void AO64_MultiBd_Continuous_Function(void);
void AO64_Periodic_Function(void);
void AO64_Function_Burst(void);
void AO64_Function_Sequencing(void);
void AO64_Init_test(void);
void AO64_Connect_Outputs(void);


U32 i, j, ValueRead, ValueRead1, *BuffPtr, *NewBuffPtr;
U32 ulNumBds, ulBdNum, ulAuxBdNum, ulErr;
U32 numChan = 0;
U32 numAuxChan = 0;
U32 id_off,eog,eof;
U32 aux_id_off,aux_eog,aux_eof;
U32 disconnect = 0;
U32 aux_disconnect = 0;
U32 TestData,ReadValue[16385];
U16 saved_x, saved_y, CurX=2, CurY=2;
char mmchar, kbchar, datebuf[16], timebuf[16];
char cBoardInfo[400];
U32 ulData[131072];

double vRange = 10.00;

//==============================================================================
//
//==============================================================================
void main(int argc, char * argv[])
{
  CursorVisible(FALSE);
  ulNumBds = AO64_66_FindBoards(&cBoardInfo[0], &ulErr);

  if(ulErr)
  {
   ShowAPIError(ulErr);
   do{}while( !kbhit() );         // Wait for a Key Press
   exit(0);
  }
  if(ulNumBds < 1)
  {
      printf("  ERROR: No Boards found in the system\n");
      do{}while( !kbhit() );         // Wait for a Key Press
  }
  else
  {
		ulBdNum = 0;
		ClrScr();
		printf("\n\n");
		printf("  ====================================================\n");
		printf("   Select Board Number to Test                          \n");
		printf("  ====================================================\n");
		printf("%s\n\n",cBoardInfo);
		do
		{
		  kbchar = prompt_for_key("  Please Make selection: \n");
			  switch(kbchar)
			  {
				case '1':
				{
				  ulBdNum = 1;
				  break;
				}
				case '2':
				{
				  ulBdNum = 2;
				  break;
				}
				case '3':
				{
				  ulBdNum = 3;
				  break;
				}
				case '4':
				{
				  ulBdNum = 4;
				  break;
				}
				case '5':
				{
				  ulBdNum = 5;
				  break;
				}
			  }
		 if((ulBdNum == 0) || (ulBdNum > ulNumBds))
		   printf("  Error - This is not a valid entry");
		}while((ulBdNum == 0) || (ulBdNum > ulNumBds));

		AO64_66_Get_Handle(&ulErr, ulBdNum);
		if(ulErr)
		{
		 ShowAPIError(ulErr);
		 do{}while( !kbhit() );         // Wait for a Key Press
		 exit(0);
		}
		else{
			ValueRead = AO64_66_Read_Local32(ulBdNum, &ulErr, FW_REV);
			switch((ValueRead>>16)&0x03){
				case 1:
				case 2: numChan = 32; break;
				case 3: numChan = 16; break;
				default: numChan = 64;
			}
			if((ValueRead & 0xFFFF) >= 0x400){
				id_off = 24;
				eog = 30;
				eof = 31;
			}
			else{
				id_off = 16;
				eog = 22;
				eof = 23;
			}
			if(ValueRead & 0x1000000)
				disconnect = 1;
		}
		ulAuxBdNum = 0; 
		if(ulNumBds >1){
		  ulAuxBdNum = ulBdNum;
		  ClrScr();
		  printf("\n\n");
		  printf("  ====================================================\n");
		  printf("   Select Auxillary Board Number to Use               \n");
		  printf("   For Examples Requiring Two Boards (0 = Disabled)   \n");
		  printf("  ====================================================\n");
		  printf("%s\n\n",cBoardInfo);
		  do
		  {
			kbchar = prompt_for_key("  Please Make selection: \n");
			  switch(kbchar)
			  {
				case '0':
				{
				  ulAuxBdNum = 0;
				  break;
				}
				case '1':
				{
				  ulAuxBdNum = 1;
				  break;
				}
				case '2':
				{
				  ulAuxBdNum = 2;
				  break;
				}
				case '3':
				{
				  ulAuxBdNum = 3;
				  break;
				}
				case '4':
				{
				  ulAuxBdNum = 4;
				  break;
				}
				case '5':
				{
				  ulAuxBdNum = 5;
				  break;
				}
			  }
			if((ulAuxBdNum == ulBdNum) || (ulAuxBdNum > ulNumBds))
		   printf("  Error - This is not a valid entry");
		  }while((ulAuxBdNum == ulBdNum) || (ulBdNum > ulNumBds));
		if(ulAuxBdNum)
		  AO64_66_Get_Handle(&ulErr, ulAuxBdNum);
		if(ulErr)
		{
		 ShowAPIError(ulErr);
		 do{}while( !kbhit() );         // Wait for a Key Press
		 exit(0);
		}
		else{
			ValueRead = AO64_66_Read_Local32(ulAuxBdNum, &ulErr, FW_REV);
			switch((ValueRead>>16)&0x03){
				case 1:
				case 2: numAuxChan = 32; break;
				case 3: numAuxChan = 16; break;
				default: numAuxChan = 64;
			}
			if((ValueRead & 0xFFFF) >= 0x400){
				aux_id_off = 24;
				aux_eog = 30;
				aux_eof = 31;
			}
			else{
				aux_id_off = 16;
				aux_eog = 22;
				aux_eof = 23;
			}
			if(ValueRead & 0x1000000)
				aux_disconnect = 1;
		}

	}
    for(i=0; i<numChan; i++)
	  ReadValue[i] = ((i<<id_off)|(1<eog)|0x8000);//Will use this to reset outputs to midscale
    ReadValue[i-1] |= (1<<eof);

    do
    {
      ClrScr();
      printf("\n\n");
      printf("  ====================================================\n");
      printf("   PCIe-16AO64 Sample Code - Board # %d               \n",ulBdNum);
      printf("  ====================================================\n");
      printf("   1 - Board Init Test                                \n");
      printf("   2 - Autocalibration and read back of Firmware Rev. \n");
      printf("   3 - Output Channels basic operation                \n");
      printf("   4 - Sequential Direct Output                       \n");
      printf("   5 - Simultaneous Direct Output                     \n");
      printf("   6 - Continuous Function Output                     \n");
      printf("   7 - Periodic Function Output                       \n");
      printf("   8 - Function Burst Output                          \n");
      printf("   9 - Function Sequencing Output                     \n");
      printf("   A - Multiboard Continuous Function Output          \n");
      printf("   X - EXIT (return to DOS)                           \n");
      printf("  ----------------------------------------------------\n");
      mmchar = prompt_for_key("  Please Make selection: \n");

      switch(mmchar)
      {
        case '1': //
          AO64_Init_test();
          break;
        case '2': //
          AO64_Auto_Cal();
          break;
        case '3': //
          AO64_Basic_output_test();
          break;
        case '4': //
          AO64_Sequential_Direct();
          break;
        case '5': //
          AO64_Simultaneous_Direct();
          break;
        case '6': //
          AO64_Continuous_Function();
          break;
        case '7': //
          AO64_Periodic_Function();
          break;
        case '8': //
          AO64_Function_Burst();
          break;
        case '9': //
          AO64_Function_Sequencing();
          break;
        case 'A': //
		  if(ulAuxBdNum)
			AO64_MultiBd_Continuous_Function();
          break;
        case 'X': //
          break;
        default:
          printf("  Invalid Selection: Press AnyKey to continue...\n");
          getch();
          break;
      }
    }while(mmchar!='X');

	AO64_66_Close_Handle(ulBdNum, &ulErr);

	CursorVisible(TRUE);
  }

} /* end main */

//------------------------------------------------------------------------------
void AO64_Init_test(void)
{
	
  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);

  cprintf("\n A. Checking the Board Initialization Defaults:");

  PositionCursor(CurX,CurY++);
  cprintf(" Wrote 0x8000 to BCR");

  AO64_66_Initialize(ulBdNum, &ulErr);
  Busy_Signal(20);
  PositionCursor(CurX,CurY++);
  cprintf("BCR Reads:........(0x481X) : %04X",AO64_66_Read_Local32(ulBdNum, &ulErr, BCR));
  PositionCursor(CurX,CurY++);
  cprintf("SMPL_RATE Reads:..(0x00C0) : %04X",AO64_66_Read_Local32(ulBdNum, &ulErr, RATE_A));
  PositionCursor(CurX,CurY++);
  cprintf("BUFF_OP Reads:....(0x1400) : %04X",AO64_66_Read_Local32(ulBdNum, &ulErr, BUFFER_OPS));
  PositionCursor(CurX,CurY++);
  cprintf("FIRM_REV Reads:...         : %04X",AO64_66_Read_Local32(ulBdNum, &ulErr, FW_REV));
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press AnyKey to Continue...");
}

//------------------------------------------------------------------------------
void AO64_Auto_Cal(void)
{
  GS_NOTIFY_OBJECT Event;
  HANDLE myHandle;
  DWORD EventStatus;
  U16 Cal_Data_Addr;
  U32 ulValue;

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 2. Auto Calibration of The Board:");

  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, 0x8000);
  Busy_Signal(10);

  myHandle =
        CreateEvent(
            NULL,           // Not inheritable to child processes
            FALSE,          // Manual reset?
            FALSE,          // Intial state
            NULL            // Name of object
            );

  if (myHandle == NULL){
		cprintf("Insufficent Resources    ...");
  	    anykey();
        return;
		}

    // Store event handle
  Event.hEvent = (U64)myHandle;
  AO64_66_EnableInterrupt(ulBdNum, 0x01, LOCAL, &ulErr);
  AO64_66_Register_Interrupt_Notify(ulBdNum, &Event, 0x01, LOCAL, &ulErr);
  ulValue = AO64_66_Read_Local32(ulBdNum, &ulErr, BCR);
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, ulValue | 0x2000); // Initiate Autocal

  EventStatus = WaitForSingleObject(myHandle,10 * 1000); // Wait for the interrupt

  PositionCursor(CurX,CurY++);
  switch(EventStatus)
  {
	case WAIT_OBJECT_0:
		cprintf("Interrupt was requested    ...");
		break;
	default:
		cprintf("Interrupt was NOT requested...");
		break;
  }

  ulValue = AO64_66_Read_Local32(ulBdNum, &ulErr, BCR);

  PositionCursor(CurX,CurY++);
  if((ulValue & 0x2000) != 0)
    cprintf("Auto Cal BCR Bit did NOT clear...");
  else
    cprintf("Auto Cal BCR Bit Cleared   ...   ");

  PositionCursor(CurX,CurY++);
  if(ulValue & 0x4000)
    cprintf("Auto Cal Status Says PASSED... %04lx",ulValue);
  else
    cprintf("Auto Cal Status Says FAILED... %04lx",ulValue);

  PositionCursor(CurX,CurY++);
    cprintf("AutoCal Complete.....Checking Results");

  PositionCursor(CurX,CurY++);

  for(Cal_Data_Addr=0x0000; Cal_Data_Addr<((numChan*2)-1);)
  {
    PositionCursor(CurX,CurY++);
    AO64_66_Write_Local32(ulBdNum, &ulErr, AUTO_CAL, Cal_Data_Addr);
    TestData = AO64_66_Read_Local32(ulBdNum, &ulErr, AUTO_CAL);
    cprintf("DAC %02i --- GAIN = %04lX",(Cal_Data_Addr/2),TestData);
    Cal_Data_Addr++;

    AO64_66_Write_Local32(ulBdNum, &ulErr, AUTO_CAL, Cal_Data_Addr);
    TestData = AO64_66_Read_Local32(ulBdNum, &ulErr, AUTO_CAL);
    cprintf("  OFFSET = %04lX",TestData);
    Cal_Data_Addr++;
  }

  PositionCursor(CurX,CurY++);
  TestData = AO64_66_Read_Local32(ulBdNum, &ulErr, FW_REV);
  cprintf("Firmware Revision  (xxxx) = %06lX",TestData);
  AO64_66_Cancel_Interrupt_Notify(ulBdNum, &Event, &ulErr);
  CloseHandle(myHandle);
  AO64_66_DisableInterrupt(ulBdNum, 0x07, LOCAL, &ulErr);// Disable ALL interrupts
  anykey();

}

//------------------------------------------------------------------------------
void AO64_Basic_output_test(void)
//------------------------------------------------------------------------------
{
  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 3. Output Channels basic operation and shorts:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Board...");
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, 0x8000);
  Busy_Signal(10);
  cprintf(" - Initialization Completed");
  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!= 1){
    cprintf(" - Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
//	return;
	}
  else{
    cprintf(" - Autocal PASSED\n");
	PositionCursor(CurX,CurY++);
    prompt_for_key("Press any key to continue....");
  }
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, 0x0030);
  Display_submenu();

}  //  end of AO64_Basic_output_test()

//------------------------------------------------------------------------------
void  Display_submenu(void)
//------------------------------------------------------------------------------
{
  for(;;)
  {
    ClrScr();
    CurX=CurY=2;
    PositionCursor(CurX,CurY++);
    cprintf(" 3. Output Channels basic operation:");
    PositionCursor(CurX,CurY++);
    cprintf("  A - Set all analog output channels to 0020");
    PositionCursor(CurX,CurY++);
    cprintf("  B - Set all analog output channels to 8000");
    PositionCursor(CurX,CurY++);
    cprintf("  C - Set all analog output channels to FFE0");
    PositionCursor(CurX,CurY++);
    cprintf("  D - Set analog outputs to walking one pattern");
    PositionCursor(CurX,CurY++);
    cprintf("  X - Return to main menu");
    PositionCursor(CurX,CurY++);

    kbchar = getch();
    switch(kbchar)
    {
      case 'a':
      case 'A':
        Set_AO64_to_0020();
        break;
      case 'b':
      case 'B':
        Set_AO64_to_8000();
        break;
      case 'c':
      case 'C':
        Set_AO64_to_FFE0();
        break;
      case 'd':
      case 'D':
        Set_AO64_to_Walking();
        break;
      case 'X':
      case 'x':
        ClrScr();
        goto EXITLBL;
    }
  }
  EXITLBL:;
}
//-----------------------------------
void  Set_AO64_to_FFE0(void)
//-----------------------------------
{
  U32 Cntr;

// This shows how to write directly to the registers.
// The user must keep up with ALL bits in the register
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0020); // set en clk h bit

  for (Cntr =0;Cntr < numChan; Cntr++){ // Simultaneous Clocking
	  ValueRead = (0xFFE0 | (Cntr<<id_off));
	  if(Cntr == (numChan - 1))
		  ValueRead |= (1<<eog); // EOG Flag
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  cprintf("  C - Set all analog output channels to FFE0");
  PositionCursor(CurX,CurY++);
  cprintf("Use a meter to verify all channels set to FFE0.");
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press any key to return to menu...");
  for(Cntr=0; Cntr<numChan; Cntr++)
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ReadValue[Cntr]);
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0000); // set en clk bit Lo
}

//-----------------------------------
void  Set_AO64_to_0020(void)
//-----------------------------------
{
  U32 Cntr;

// Calling these functions frees the user from keeping
// track of ALL the bits in the register
  AO64_66_Enable_Clock(ulBdNum, &ulErr);// set en clk bit Hi

  for (Cntr =0;Cntr < numChan; Cntr++){ // Simultaneous Clocking
	  ValueRead = (0x0020 | (Cntr<<id_off));
	  if(Cntr == (numChan - 1))
		  ValueRead |= (1<<eog); // EOG Flag
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  cprintf("  A - Set all analog output channels to 0020");
  PositionCursor(CurX,CurY++);
  cprintf("Use a meter to verify all channels set to 0020.");
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press any key to return to menu...");
  for(Cntr=0; Cntr<numChan; Cntr++)
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ReadValue[Cntr]);
  AO64_66_Disable_Clock(ulBdNum, &ulErr);
}

//-----------------------------------
void  Set_AO64_to_8000(void)
//-----------------------------------
{
  U32 Cntr;

  AO64_66_Enable_Clock(ulBdNum, &ulErr);// set en clk bit Hi

  for (Cntr =0;Cntr < numChan; Cntr++){ // Simultaneous Clocking
	  ValueRead = (0x8000 | (Cntr<<id_off));
	  if(Cntr == (numChan - 1))
		  ValueRead |= (1<<eog); // EOG Flag
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  cprintf("  B - Set all analog output channels to 8000");
  PositionCursor(CurX,CurY++);
  cprintf("Use a meter to verify all channels set to 8000.");
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press any key to return to menu...");
  AO64_66_Disable_Clock(ulBdNum, &ulErr);
}


//-----------------------------------
void  Set_AO64_to_Walking(void)
//-----------------------------------
{
  U32 Loop, Cntr;

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  cprintf("Setting analog outputs to walking-one");
  PositionCursor(CurX,CurY++);
  cprintf("   for cross-channel check.");
  PositionCursor(CurX,CurY++);

  AO64_66_Enable_Clock(ulBdNum, &ulErr);// set en clk bit Hi

  for (Loop=0; Loop < numChan; Loop++) 
  {
    for (Cntr =0;Cntr < numChan; Cntr++)
    {
     ValueRead = ((Cntr<<id_off) | (1<<eog));// Using Sequential Clocking
	 if(Loop == Cntr)
        AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead | 0xFFE0);
      else
        AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead | 0x8000);
    }
    PositionCursor(CurX,CurY);
    cprintf("Verify Ch %02i set to FFE0, and others are midscale",Loop);
    getch();
  }
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press AnyKey to Continue....");
  AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead | 0x8000);
  AO64_66_Disable_Clock(ulBdNum, &ulErr);
}


//------------------------------------------------------------------------------
void AO64_Sequential_Direct(void)
//------------------------------------------------------------------------------
{
  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 4. Sequential Direct Output:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Board...");
  AO64_66_Initialize(ulBdNum, &ulErr);
  Busy_Signal(10);
  cprintf("Initialization Completed");
  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!=1){
    cprintf(" - Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
	return;
	}
  else
     cprintf(" - Autocal PASSED");

  AO64_66_Enable_Clock(ulBdNum, &ulErr);// set en clk bit Hi
  AO64_Connect_Outputs();
  PositionCursor(CurX,CurY++);
  prompt_for_key("Please Verify that all Channels are now at Zero Volts.");

  for(i=0; i<numChan; i++)
  {
    AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, (i<<id_off)|(1<<eog)|0xC000);
    PositionCursor(CurX,CurY);
    cprintf("Please Verify that Chan %02i is now at Half PFS...",i);
    wait_for_key();
  }
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press AnyKey to Continue...");
  for(i=0; i<numChan; i++)
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ReadValue[i]);
  AO64_66_Disable_Clock(ulBdNum, &ulErr);

}


//------------------------------------------------------------------------------
void AO64_Simultaneous_Direct(void)
{
  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 5. Simultaneous Direct Output:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Board...");
  AO64_66_Initialize(ulBdNum, &ulErr);
  Busy_Signal(10);
  cprintf("Initialization Completed");
  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!=1){
    cprintf(" - Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
//	return;
  }
  else
    cprintf(" - Autocal PASSED");

  AO64_66_Enable_Clock(ulBdNum, &ulErr);// set en clk bit Hi
  AO64_Connect_Outputs();

  PositionCursor(CurX,CurY++);
  cprintf("BCR Reads: %04X", AO64_66_Read_Local32(ulBdNum, &ulErr, BCR));
  PositionCursor(CurX,CurY++);
  cprintf("BUFF_OP Reads: %04X", AO64_66_Read_Local32(ulBdNum, &ulErr, BUFFER_OPS));

  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  prompt_for_key("Please Verify that all Channels are now at Zero Volts.");
  PositionCursor(CurX,CurY++);

  for(i=0; i<numChan; i++)
  {
    PositionCursor(CurX,CurY);
    cprintf("Loading Value for Channel %02i",i);
	ValueRead = ((i<<id_off)| 0xC000);
	if(i == (numChan-1))
		ValueRead |= (1<<eog);
    AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
    PositionCursor(CurX,(U16)(CurY+1));
    if(i != (numChan-1))
      prompt_for_key("Please Verify that all Channels remain at Zero Volts.");
    else
      cprintf("Please Verify that all Channels are now at Half PFS. ");
  }
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press AnyKey to Continue...");
  for(i=0; i<numChan; i++)
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ReadValue[i]);
  AO64_66_Disable_Clock(ulBdNum, &ulErr);

}



//------------------------------------------------------------------------------
void AO64_Continuous_Function(void)
{
  GS_NOTIFY_OBJECT Event;
  HANDLE myHandle;
  DWORD EventStatus;
  U32 loop;
  U32 numTimes = 4096;// WARNING - don't overflow allocated buffer

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 6. Continuous Function Output:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Board...");
  AO64_66_Initialize(ulBdNum, &ulErr);
  Busy_Signal(10);
  cprintf("Initialization Completed");

  AO64_66_Set_Sample_Rate(ulBdNum,500000.0, &ulErr); 

  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!=1){
    cprintf(" - Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
	return;
  }
  else
    cprintf(" - Autocal PASSED");

  PositionCursor(CurX,CurY++);
  prompt_for_key("Please Verify that all Channels are now at Zero Volts.");

  BuffPtr = &ulData[0];
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_THRSHLD, 65536);

// This example will generate a 24.4Hz square wave on 16 channels
// 100000/(65536/16) 100Khz sample rate, 65536 samples, 16 channels
  
  for(loop=0; loop<numTimes; loop++)
  {
	  if(!(loop%2)){
		for(i=0; i<16; i++)
			*(BuffPtr+(loop*16)+i) = ((i<<id_off)|0x4000);// Channel Tag + Value
	    *(BuffPtr+(loop*16)+(i-1)) |= (1<<eog);// Last Channel Tag
	  }
	  else{
		for(i=0; i<16; i++)
			*(BuffPtr+(loop*16)+i) = ((i<<id_off)|0xC000);// Channel Tag + Value
	    *(BuffPtr+(loop*16)+(i-1)) |= (1<<eog);// Last Channel Tag
 	  }

  }

  myHandle =
        CreateEvent(
            NULL,           // Not inheritable to child processes
            FALSE,          // Manual reset?
            FALSE,          // Intial state
            NULL            // Name of object
            );

  if (myHandle == NULL){
		cprintf("Insufficent Resources    ...");
  	    anykey();
        return;
		}

    // Store event handle
  Event.hEvent = (U64)myHandle;
  AO64_66_EnableInterrupt(ulBdNum, 0x04, LOCAL, &ulErr);
  AO64_66_Register_Interrupt_Notify(ulBdNum, &Event, 0x04, LOCAL, &ulErr);
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  cprintf("Continuously Writing using interrupts now...");
  PositionCursor(CurX,CurY++);
  cprintf("Press AnyKey to return to the main menu...");
  PositionCursor(CurX,CurY++);
  AO64_66_Open_DMA_Channel(ulBdNum, 0x0l, &ulErr);
  // This loads the Output Buffer 3/4 Full
  AO64_66_DMA_Transfer(ulBdNum, 0x0l, 0x10000, BuffPtr, &ulErr); // 65536
  AO64_66_DMA_Transfer(ulBdNum, 0x0l, 0x10000, BuffPtr, &ulErr); // 65536
  AO64_66_DMA_Transfer(ulBdNum, 0x0l, 0x10000, BuffPtr, &ulErr); // 65536
  
  kbflush();
  
  AO64_Connect_Outputs();
  AO64_66_Enable_Clock(ulBdNum, &ulErr);// set en clk bit Hi

  do
  {
    
     EventStatus = WaitForSingleObject(myHandle,3 * 1000); // Wait for the interrupt

     switch(EventStatus)	
	 {						
	  case WAIT_OBJECT_0:	
	   AO64_66_DMA_Transfer(ulBdNum, 0x0l, 0x10000, BuffPtr, &ulErr); // 65536
	   AO64_66_DMA_Transfer(ulBdNum, 0x0l, 0x10000, BuffPtr, &ulErr); // 65536
      break;
	  default:
	 	PositionCursor(CurX,CurY);
		cprintf("Error... Interrupt Timeout\n");
		break;
	 }

  }while(!kbhit());

  AO64_66_Cancel_Interrupt_Notify(ulBdNum, &Event, &ulErr);
  AO64_66_DisableInterrupt(ulBdNum, 0x03, LOCAL, &ulErr);
  AO64_66_Disable_Clock(ulBdNum, &ulErr);
  AO64_66_Close_DMA_Channel(ulBdNum, 0x0l, &ulErr);
  CloseHandle(myHandle);
  kbflush();
}


//------------------------------------------------------------------------------
void AO64_Periodic_Function(void)
{
  U32 loop;

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 7. Periodic Function Output:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Board...");
  AO64_66_Initialize(ulBdNum, &ulErr);
  Busy_Signal(10);
  cprintf("Initialization Completed");
  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!=1){
    cprintf(" - Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
//	return;
  }
  else
    cprintf(" - Autocal PASSED");

  PositionCursor(CurX,CurY++);
  cprintf("Anykey to advance thru the commands");
  PositionCursor(CurX,CurY++);
  prompt_for_key("Write 0x0800 to BUFFER_OP");
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0800);// Disable Clock + Clear Buffer

  PositionCursor(CurX,CurY++);
  prompt_for_key("Write 0x0030 to BCR");
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, 0x0030);// Offset Binary + Enable RATE_A
  PositionCursor(CurX,CurY++);					   
  prompt_for_key("Write Data to the Output Buffer");

  for(loop=0; loop<numChan; loop++){// Write a single value for all channels
	  ValueRead = (loop == (numChan-1))? (1<<eog) : 0;// End Group Flag
	  ValueRead |= ((loop<<id_off) | 0xC000);
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }
  for(loop=0; loop<(numChan/2); loop++){// Write a single value for first half of channels
	  ValueRead = (loop == ((numChan/2)-1))?(1<<eog) : 0;// End Group Flag
	  ValueRead |= ((loop<<id_off) | 0x4000);
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }
  for(loop=0; loop<(numChan/2); loop++){// Write a single value for first half of channels
	  ValueRead = (loop == ((numChan/2)-1))?(1<<eog) : 0;// End Group Flag
	  ValueRead |= ((loop<<id_off) | 0xC000);
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }
  for(loop=0; loop<numChan; loop++){// Write a single value for all channels
	  ValueRead = (loop == (numChan-1))?(1<<eog) : 0;// End Group Flag
	  ValueRead |= ((loop<<id_off) | 0x4000);
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }
  for(loop=0; loop<(numChan/2); loop++){// Write a single value for first half of channels
	  ValueRead = (loop == ((numChan/2)-1))?(1<<eog) : 0;// End Group Flag
	  ValueRead |= ((loop<<id_off) | 0xC000);
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }
  for(loop=0; loop<(numChan/2); loop++){// Write a single value for first half of channels
	  ValueRead = (loop == ((numChan/2)-1))?(3<<eog) : 0;// End Group + End Frame Flag
	  ValueRead |= ((loop<<id_off) | 0x4000);
      AO64_66_Write_Local32(ulBdNum, &ulErr, OUTPUT_DATA_BUFFER, ValueRead);
  }

  PositionCursor(CurX,CurY++);
  prompt_for_key("Sample rate of (0xC000)");
  AO64_66_Write_Local32(ulBdNum, &ulErr, RATE_A, 0xC000);

  PositionCursor(CurX,CurY++);
  AO64_Connect_Outputs();
  prompt_for_key("Write BUFF_OP with 0x0120");// Circular Buffer + Enable Clock
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0120);
  PositionCursor(CurX,CurY++);
  prompt_for_key("Press AnyKey to Continue...");
  AO64_66_Disable_Clock(ulBdNum, &ulErr);

}


//------------------------------------------------------------------------------
void AO64_Function_Burst(void)
{
  U32 loop;
  U32 numTimes = 256;

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 8. Function Burst Output:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Board...");
  AO64_66_Initialize(ulBdNum, &ulErr);
  Busy_Signal(10);
  cprintf("Initialization Completed");
  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!=1){
    cprintf(" - Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
//	return;
  }
  else
    cprintf(" - Autocal PASSED");

  PositionCursor(CurX,CurY++);
  prompt_for_key("Please Verify that all Channels are now at Zero Volts.");

  BuffPtr = &ulData[0];
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0800);// Clear Buffer & No Clk

  for(loop=0; loop < numTimes; loop++)
  {
	  if(!(loop%2)){
		for(i=0; i<numChan; i++)
			*(BuffPtr+(loop*numChan)+i) = ((i<<id_off)|0x4000);// Channel Tag + Value
	    *(BuffPtr+(loop*numChan)+(i-1)) |= (1<<eog);// Last Channel Tag
	  }
	  else{
		for(i=0; i<numChan; i++)
			*(BuffPtr+(loop*numChan)+i) = ((i<<id_off)|0xC000);// Channel Tag + Value
	    *(BuffPtr+(loop*numChan)+(i-1)) |= (1<<eog);// Last Channel Tag
 	  }
  if(loop == (numTimes-1))
	*(BuffPtr+(loop*numChan)+(i-1)) |= (1<<eof);// EOF Flag

  }
  AO64_66_Open_DMA_Channel(ulBdNum, 0x0l, &ulErr);
  AO64_66_DMA_Transfer(ulBdNum, 0x0l, numTimes*numChan, BuffPtr, &ulErr);
  AO64_66_Close_DMA_Channel(ulBdNum, 0x0l, &ulErr);


  AO64_66_Write_Local32(ulBdNum, &ulErr, RATE_A, 0x1333);
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, 0x0030);
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0124);

  AO64_Connect_Outputs();
  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  do
  {
    PositionCursor(CurX,CurY);
    cprintf("Press X to exit... AnyKey to Cause Burst Trigger....");
    do
    {
      PositionCursor(CurX,(U16)(CurY+1));
      cprintf("BUFFER_OPS = %04X", AO64_66_Read_Local32(ulBdNum, &ulErr, BUFFER_OPS));
    }while(!kbhit());

    kbchar=toupper(getch());
    if(kbchar != 'X')
    AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0134);
  }while(kbchar != 'X');

  AO64_66_Disable_Clock(ulBdNum, &ulErr);
  kbflush();
}


//------------------------------------------------------------------------------
void AO64_Function_Sequencing(void)
{
  U32 loop;
  double value,valuet;

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" 9. Function Sequencing Output:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Board...");
  AO64_66_Initialize(ulBdNum, &ulErr);
  Busy_Signal(10);
  cprintf("Initialization Completed");
  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!=1){
    cprintf(" - Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
//	return;
  }
  else
    cprintf(" - Autocal PASSED");

  PositionCursor(CurX,CurY++);
  prompt_for_key("Please Verify that all Channels are now at Zero Volts.");

  PositionCursor(CurX,CurY++);
  cprintf("Write 0x0800 to BUFF_OP");
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0800);

  PositionCursor(CurX,CurY++);
  cprintf("Write 0x0030 to BCR");
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, 0x0030);

  PositionCursor(CurX,CurY++);
  cprintf("Write Data to the Buffers");

  BuffPtr = &ulData[0];
  NewBuffPtr = &ulData[360*numChan];

  // Load the DMA buffers with data
  // Assumes +/- 10V Range
#define pi 3.14

  for(loop=0; loop<360; loop++){
  value = vRange*sin(pi*(double)loop/8.0);
  valuet = vRange*cos(pi*(double)loop/32.0);
   for(i=0; i<numChan; i++){
    *(BuffPtr+(loop*numChan)+i) = ((i<<id_off) | (U32)((value/(vRange*2))*65536.0+32768.0));
    *(NewBuffPtr+(loop*numChan)+i) = ((i<<id_off) | (U32)((valuet/(vRange*2))*65536.0+32768.0));
   }
   *(BuffPtr+(loop*numChan)+(i-1)) |= (1<<eog);
   *(NewBuffPtr+(loop*numChan)+(i-1)) |= (1<<eog);
  }
  *(BuffPtr+((loop*numChan)-1)) |= (1<<eof); 
  *(NewBuffPtr+((loop*numChan)-1)) |= (1<<eof); 
  i = loop;  

  AO64_66_Open_DMA_Channel(ulBdNum, 0x0l, &ulErr);
  AO64_66_DMA_Transfer(ulBdNum, 0x0l, i*numChan, BuffPtr, &ulErr);

  PositionCursor(CurX,CurY++);
  cprintf("Sample rate of %5.1lf",AO64_66_Set_Sample_Rate(ulBdNum,1000.0, &ulErr));

  PositionCursor(CurX,CurY++);
  cprintf("Write BUFF_OP with 0x0120");
  PositionCursor(CurX,CurY++);
  cprintf("X to Exit Loop");
  AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0120);
  PositionCursor(CurX,CurY++);

  do
  {
    //Request to load buffer
    AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0320);
    //Wait For It
    loop=0;// Could use interrupt here
    while(!(AO64_66_Read_Local32(ulBdNum, &ulErr, BUFFER_OPS) & 0x0400) && (loop++ < 20000));

    AO64_66_DMA_Transfer(ulBdNum, 0x0l, i*numChan, NewBuffPtr, &ulErr);

    Sleep(200);

    //Request to load buffer
    AO64_66_Write_Local32(ulBdNum, &ulErr, BUFFER_OPS, 0x0320);
    //Wait For It
    loop=0;
	while(!(AO64_66_Read_Local32(ulBdNum, &ulErr, BUFFER_OPS) & 0x0400) && (loop++ < 20000));

    AO64_66_DMA_Transfer(ulBdNum, 0x0l, i*numChan, BuffPtr, &ulErr);

    Sleep(200);

  }while(!kbhit());

  AO64_66_Close_DMA_Channel(ulBdNum, 0x0l, &ulErr);
  AO64_66_Disable_Clock(ulBdNum, &ulErr);
  kbflush();
}

//------------------------------------------------------------------------------
void AO64_MultiBd_Continuous_Function(void)
{
  GS_NOTIFY_OBJECT Event;
  HANDLE myHandle;
  DWORD EventStatus;
  U32 loop;
  U32 DataRd;
  short flop=1;

  ClrScr();
  CurX=CurY=2;
  PositionCursor(CurX,CurY++);
  cprintf(" A. MultiBoard Continuous Function Output:");

  // Init the Board
  PositionCursor(CurX,CurY++);
  cprintf("Initializing the Boards...");
  AO64_66_Initialize(ulBdNum, &ulErr);
  if(ulAuxBdNum)
	AO64_66_Initialize(ulAuxBdNum, &ulErr);
  Busy_Signal(10);
  AO64_66_Set_Sample_Rate(ulBdNum,200000.0, &ulErr); 
  if(ulAuxBdNum)
	AO64_66_Set_Sample_Rate(ulAuxBdNum,200000.0, &ulErr); 
  cprintf("Initialization Completed");
  PositionCursor(CurX,CurY++);
  cprintf("Performing Autocal");
  if(AO64_66_Autocal(ulBdNum, &ulErr)!=1){
    cprintf(" - Initiator Autocal FAILED\n");
    prompt_for_key("Press AnyKey to return to main....");
//	return;
  }
  if(ulAuxBdNum){
	  if(AO64_66_Autocal(ulAuxBdNum, &ulErr)!=1)
	  {
		cprintf(" - Target Autocal FAILED\n");
		prompt_for_key("Press AnyKey to return to main....");
		return;
	  }
	  else
		cprintf(" - Autocal PASSED");

	  DataRd = AO64_66_Read_Local32(ulAuxBdNum, &ulErr, BUFFER_OPS);
	  AO64_66_Write_Local32(ulAuxBdNum, &ulErr, BUFFER_OPS, DataRd | 0x01);// External Clock
	  DataRd = AO64_66_Read_Local32(ulAuxBdNum, &ulErr, BUFFER_OPS);
	  // Setup the Target for External DAC Clocking
  }

  DataRd = AO64_66_Read_Local32(ulBdNum, &ulErr, BCR);
  DataRd &= ~0x4;// Connect Outputs
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, DataRd | 0x01);// Clock Initiator

  DataRd = AO64_66_Read_Local32(ulAuxBdNum, &ulErr, BCR);
  DataRd &= ~0x4;// Connect Outputs
  AO64_66_Write_Local32(ulAuxBdNum, &ulErr, BCR, DataRd);
  
  
  BuffPtr = &ulData[0];


  for(loop=0; loop<131072; loop++)
  {
    if(loop%2)
		flop = !flop;
	if(flop)
      *(BuffPtr+loop) = 0x0000C000;
    else
      *(BuffPtr+loop) = 0x00004000;

  }


  PositionCursor(CurX,CurY++);
  PositionCursor(CurX,CurY++);
  cprintf("Continuously Writing using interrupts now...");
  PositionCursor(CurX,CurY++);
  cprintf("Press AnyKey to return to the main menu...");
  PositionCursor(CurX,CurY++);
  myHandle =
        CreateEvent(
            NULL,           // Not inheritable to child processes
            FALSE,          // Manual reset?
            FALSE,          // Intial state
            NULL            // Name of object
            );

  if (myHandle == NULL){
		cprintf("Insufficent Resources    ...");
  	    anykey();
        return;
		}

    // Store event handle
  Event.hEvent = (U64)myHandle;
  AO64_66_EnableInterrupt(ulBdNum, 0x04, LOCAL, &ulErr);
  AO64_66_Register_Interrupt_Notify(ulBdNum, &Event, 0x04, LOCAL, &ulErr);

  AO64_66_Open_DMA_Channel(ulBdNum, 0x0l, &ulErr);
  AO64_66_DMA_Transfer(ulBdNum, 0x0l, 0x20000, BuffPtr, &ulErr); // 131072
  if(ulAuxBdNum){
	  AO64_66_Open_DMA_Channel(ulAuxBdNum, 0x01, &ulErr);
	  AO64_66_DMA_Transfer(ulAuxBdNum, 0x01, 0x20000, BuffPtr, &ulErr); // 131072
  }

  // We only need one interrupt since both are clocking off the initiator
  kbflush();
  
  if(ulAuxBdNum)
	  AO64_66_Enable_Clock(ulAuxBdNum, &ulErr);// set en clk bit Hi

  AO64_66_Enable_Clock(ulBdNum, &ulErr);// set en clk bit Hi

  do
  {
    
     EventStatus = WaitForSingleObject(myHandle,6 * 1000); // Wait for the interrupt

     switch(EventStatus)	
	 {						
	  case WAIT_OBJECT_0:	
	   AO64_66_DMA_Transfer(ulBdNum, 0x0l, 0x20000, BuffPtr, &ulErr);
	   if(ulAuxBdNum)
	     AO64_66_DMA_Transfer(ulAuxBdNum, 0x01, 0x20000, BuffPtr, &ulErr);
      break;
	  default:
	 	PositionCursor(CurX,CurY);
		cprintf("Error... Interrupt Timeout\n");
		break;
	 }

  }while(!kbhit());

  AO64_66_Cancel_Interrupt_Notify(ulBdNum, &Event, &ulErr);
  AO64_66_DisableInterrupt(ulBdNum, 0x04, LOCAL, &ulErr);
  AO64_66_Disable_Clock(ulBdNum, &ulErr);
  AO64_66_Close_DMA_Channel(ulBdNum, 0x0l, &ulErr);
  if(ulAuxBdNum)
    AO64_66_Close_DMA_Channel(ulAuxBdNum, 0x01, &ulErr);
  CloseHandle(myHandle);

}

//
// If the disconnect feature is installed
//
void AO64_Connect_Outputs(void)
{
  U32 myData;
  
  if(!disconnect)
	  return;
  myData = AO64_66_Read_Local32(ulBdNum, &ulErr, BCR);
  myData &= ~0x4;
  AO64_66_Write_Local32(ulBdNum, &ulErr, BCR, myData);
}


