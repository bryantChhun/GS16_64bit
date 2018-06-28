package bindings;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.ptr.NativeLongByReference;
import java.nio.ByteBuffer;
/**
 * JNA Wrapper for library <b>AO64_64b_Driver_C</b><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public interface AO64_64b_Driver_CLibrary extends Library {
	public static final String JNA_LIBRARY_NAME = "AO64_64b_Driver_C";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(AO64_64b_Driver_CLibrary.JNA_LIBRARY_NAME);
	public static final AO64_64b_Driver_CLibrary INSTANCE = (AO64_64b_Driver_CLibrary)Native.loadLibrary(AO64_64b_Driver_CLibrary.JNA_LIBRARY_NAME, AO64_64b_Driver_CLibrary.class);
	/**
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h</i><br>
	 * enum values
	 */
	public static interface PLX_STATUS {
		/**
		 * 512<br>
		 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:51</i>
		 */
		public static final int ApiSuccess = 0x200;
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:52</i> */
		public static final int ApiFailed = (0x200 + 1);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:53</i> */
		public static final int ApiNullParam = (0x200 + 2);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:54</i> */
		public static final int ApiUnsupportedFunction = (0x200 + 3);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:55</i> */
		public static final int ApiNoActiveDriver = (0x200 + 4);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:56</i> */
		public static final int ApiConfigAccessFailed = (0x200 + 5);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:57</i> */
		public static final int ApiInvalidDeviceInfo = (0x200 + 6);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:58</i> */
		public static final int ApiInvalidDriverVersion = (0x200 + 7);
		/**
		 * 520<br>
		 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:59</i>
		 */
		public static final int ApiInvalidOffset = (0x200 + 8);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:60</i> */
		public static final int ApiInvalidData = (0x200 + 9);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:61</i> */
		public static final int ApiInvalidSize = (0x200 + 10);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:62</i> */
		public static final int ApiInvalidAddress = (0x200 + 11);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:63</i> */
		public static final int ApiInvalidAccessType = (0x200 + 12);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:64</i> */
		public static final int ApiInvalidIndex = (0x200 + 13);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:65</i> */
		public static final int ApiInvalidPowerState = (0x200 + 14);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:66</i> */
		public static final int ApiInvalidIopSpace = (0x200 + 15);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:67</i> */
		public static final int ApiInvalidHandle = (0x200 + 16);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:68</i> */
		public static final int ApiInvalidPciSpace = (0x200 + 17);
		/**
		 * 530<br>
		 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:69</i>
		 */
		public static final int ApiInvalidBusIndex = (0x200 + 18);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:70</i> */
		public static final int ApiInsufficientResources = (0x200 + 19);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:71</i> */
		public static final int ApiWaitTimeout = (0x200 + 20);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:72</i> */
		public static final int ApiWaitCanceled = (0x200 + 21);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:73</i> */
		public static final int ApiDmaChannelUnavailable = (0x200 + 22);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:74</i> */
		public static final int ApiDmaChannelInvalid = (0x200 + 23);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:75</i> */
		public static final int ApiDmaDone = (0x200 + 24);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:76</i> */
		public static final int ApiDmaPaused = (0x200 + 25);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:77</i> */
		public static final int ApiDmaInProgress = (0x200 + 26);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:78</i> */
		public static final int ApiDmaCommandInvalid = (0x200 + 27);
		/**
		 * 540<br>
		 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:79</i>
		 */
		public static final int ApiDmaInvalidChannelPriority = (0x200 + 28);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:80</i> */
		public static final int ApiDmaSglPagesGetError = (0x200 + 29);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:81</i> */
		public static final int ApiDmaSglPagesLockError = (0x200 + 30);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:82</i> */
		public static final int ApiMuFifoEmpty = (0x200 + 31);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:83</i> */
		public static final int ApiMuFifoFull = (0x200 + 32);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:84</i> */
		public static final int ApiPowerDown = (0x200 + 33);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:85</i> */
		public static final int ApiHSNotSupported = (0x200 + 34);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:86</i> */
		public static final int ApiVPDNotSupported = (0x200 + 35);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:87</i> */
		public static final int ApiDeviceInUse = (0x200 + 36);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:88</i> */
		public static final int ApiDmaNotReady = (0x200 + 37);
		/**
		 * 550<br>
		 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:89</i>
		 */
		public static final int ApiInvalidBoardNumber = (0x200 + 38);
		/** <i>native declaration : DriverFiles/66-16AO64/Tools.h:90</i> */
		public static final int ApiInvalidDMANumWords = (0x200 + 39);
		/**
		 * Do not add API errors below this line<br>
		 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:91</i>
		 */
		public static final int ApiLastError = (0x200 + 40);
	};
	/** <i>native declaration : DriverFiles/66-16AO64/Tools.h</i> */
	public static final int PLX_STATUS_START = (int)0x200;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int BCR = (int)0x00;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int Reserved = (int)0x04;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int Reserved1 = (int)0x08;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int BUFFER_OPS = (int)0x0C;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int FW_REV = (int)0x10;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int AUTO_CAL = (int)0x14;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int OUTPUT_DATA_BUFFER = (int)0x18;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int BUFFER_SIZE = (int)0x1C;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int BUFFER_THRSHLD = (int)0x20;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int RATE_A = (int)0x24;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int RATE_B = (int)0x28;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int CLK_INITIATOR = (int)0x0001;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int TRIG_INITIATOR = (int)0x0002;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int DISCONNECT_OUT = (int)0x0004;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int REMOTE_GND_SENSE = (int)0x0008;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int OFFSET_BINARY = (int)0x0010;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int EXT_CLK = (int)0x0001;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int EXT_TRIG = (int)0x0002;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int ENABLE_CLK = (int)0x0020;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int CLK_RDY = (int)0x0040;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int OPEN_BUFF = (int)0x0000;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int CIRC_BUFF = (int)0x0100;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int LOAD_REQ = (int)0x0200;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int LOAD_RDY = (int)0x0400;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int CLR_BUFFER = (int)0x0800;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int LOCAL = (int)0;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final int DMA = (int)1;
	/** <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h</i> */
	public static final long MINUS_ONE_LONG = (long)0xFFFFFFFFL;
	/**
	 * Barry's Windows Programming Tools<br>
	 * Original signature : <code>void CursorVisible(BOOL)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:24</i>
	 */
	void CursorVisible(boolean CVState);
	/**
	 * Original signature : <code>void PositionCursor(U16, U16)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:25</i>
	 */
	void PositionCursor(short X, short Y);
	/**
	 * Original signature : <code>void ClrScr()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:26</i>
	 */
	void ClrScr();
	/**
	 * Original signature : <code>void kbflush()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:27</i>
	 */
	void kbflush();
	/**
	 * Original signature : <code>char wait_for_key()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:28</i>
	 */
	byte wait_for_key();
	/**
	 * Original signature : <code>char prompt_for_key(char*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:29</i><br>
	 * @deprecated use the safer methods {@link #prompt_for_key(java.nio.ByteBuffer)} and {@link #prompt_for_key(com.sun.jna.Pointer)} instead
	 */
	@Deprecated 
	byte prompt_for_key(Pointer message);
	/**
	 * Original signature : <code>char prompt_for_key(char*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:29</i>
	 */
	byte prompt_for_key(ByteBuffer message);
	/**
	 * Original signature : <code>char anykey()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:30</i>
	 */
	byte anykey();
	/**
	 * Original signature : <code>void print_time()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:31</i>
	 */
	void print_time();
	/**
	 * Original signature : <code>void save_cursor()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:32</i>
	 */
	void save_cursor();
	/**
	 * Original signature : <code>void restore_cursor()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:33</i>
	 */
	void restore_cursor();
	/**
	 * Original signature : <code>void Busy_Signal(int)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:34</i>
	 */
	void Busy_Signal(int speed);
	/**
	 * Original signature : <code>BOOL VersionDetect()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:35</i>
	 */
	boolean VersionDetect();
	/**
	 * Original signature : <code>void SetColors(U16)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:36</i>
	 */
	void SetColors(short ConsoleColor);
	/**
	 * Original signature : <code>char dsp_time_scan_for_key()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:37</i>
	 */
	byte dsp_time_scan_for_key();
	/**
	 * Original signature : <code>void print_passed()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:38</i>
	 */
	void print_passed();
	/**
	 * Original signature : <code>void print_failed()</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:39</i>
	 */
	void print_failed();
	/**
	 * Original signature : <code>void ShowAPIError(U32)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/Tools.h:40</i>
	 */
	void ShowAPIError(NativeLong APIReturnCode);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_FindBoards(char*, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:100</i><br>
	 * @deprecated use the safer methods {@link #AO64_66_FindBoards(java.nio.ByteBuffer, com.sun.jna.ptr.NativeLongByReference)} and {@link #AO64_66_FindBoards(com.sun.jna.Pointer, com.sun.jna.ptr.NativeLongByReference)} instead
	 */
	@Deprecated 
	NativeLong AO64_66_FindBoards(Pointer pDeviceInfo, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_FindBoards(char*, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:100</i>
	 */
	NativeLong AO64_66_FindBoards(ByteBuffer pDeviceInfo, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_Get_Handle(U32*, U32)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:108</i>
	 */
	NativeLong AO64_66_Get_Handle(NativeLongByReference ulError, NativeLong BoardNumber);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Close_Handle(U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:116</i>
	 */
	void AO64_66_Close_Handle(NativeLong BoardNumber, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_Read_Local32(U32, U32*, U32)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:126</i>
	 */
	NativeLong AO64_66_Read_Local32(NativeLong BoardNumber, NativeLongByReference ulError, NativeLong ulRegister);
	//NativeLong AO64_66_Read_Local32(NativeLong BoardNumber, NativeLongByReference ulError, int ulRegister);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Write_Local32(U32, U32*, U32, U32)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:135</i>
	 */
	void AO64_66_Write_Local32(NativeLong BoardNumber, NativeLongByReference ulError, NativeLong ulRegister, NativeLong uiValue);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Initialize(U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:143</i>
	 */
	void AO64_66_Initialize(NativeLong BoardNumber, NativeLongByReference ulErrorIn);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_Autocal(U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:153</i>
	 */
	NativeLong AO64_66_Autocal(NativeLong BoardNumber, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Clear_Buffer(U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:161</i>
	 */
	void AO64_66_Clear_Buffer(NativeLong BoardNumber, NativeLongByReference ulErrorIn);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>double AO64_66_Set_Sample_Rate(U32, double, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:169</i>
	 */
	double AO64_66_Set_Sample_Rate(NativeLong BoardNumber, double fRate, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_EnableInterrupt(U32, U32, U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:182</i>
	 */
	NativeLong AO64_66_EnableInterrupt(NativeLong BoardNumber, NativeLong ulValue, NativeLong ulType, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_DisableInterrupt(U32, U32, U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:193</i>
	 */
	void AO64_66_DisableInterrupt(NativeLong BoardNumber, NativeLong ulValue, NativeLong ulType, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Register_Interrupt_Notify(U32, GS_NOTIFY_OBJECT*, U32, U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:204</i>
	 */
	void AO64_66_Register_Interrupt_Notify(NativeLong BoardNumber, GS_NOTIFY_OBJECT event, NativeLong ulIntr, NativeLong ulType, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Cancel_Interrupt_Notify(U32, GS_NOTIFY_OBJECT*, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:210</i>
	 */
	void AO64_66_Cancel_Interrupt_Notify(NativeLong BoardNumber, GS_NOTIFY_OBJECT event, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Open_DMA_Channel(U32, U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:215</i>
	 */
	void AO64_66_Open_DMA_Channel(NativeLong BoardNumber, NativeLong ulChannel, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_DMA_Transfer(U32, U32, U32, U32*, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:223</i>
	 */
	NativeLong AO64_66_DMA_Transfer(NativeLong BoardNumber, NativeLong ulChannel, NativeLong ulWords, NativeLongByReference uData, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Close_DMA_Channel(U32, U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:230</i>
	 */
	void AO64_66_Close_DMA_Channel(NativeLong BoardNumber, NativeLong ulChannel, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Enable_Clock(U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:236</i>
	 */
	void AO64_66_Enable_Clock(NativeLong BoardNumber, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Disable_Clock(U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:242</i>
	 */
	void AO64_66_Disable_Clock(NativeLong BoardNumber, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_Setup_DmaCmdChaining(U32, GS_DMA_DESCRIPTOR*, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:248</i>
	 */
	NativeLong AO64_66_Setup_DmaCmdChaining(NativeLong BoardNumber, GS_DMA_DESCRIPTOR DmaSetup, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_Start_DmaCmdChaining(U32, U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:254</i>
	 */
	NativeLong AO64_66_Start_DmaCmdChaining(NativeLong BoardNumber, NativeLong ulChannel, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Close_DmaCmdChaining(U32, U32, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:260</i>
	 */
	void AO64_66_Close_DmaCmdChaining(NativeLong BoardNumber, NativeLong ulChannel, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>U32 AO64_66_Get_Physical_Memory(U32, GS_PHYSICAL_MEM*, BOOLEAN, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:266</i>
	 */
	NativeLong AO64_66_Get_Physical_Memory(NativeLong BoardNumber, GS_PHYSICAL_MEM memPtr, AO64_64b_Driver_CLibrary.BOOLEAN bSmallerOk, NativeLongByReference ulError);
	/**
	 * ==============================================================================<br>
	 * Original signature : <code>void AO64_66_Free_Physical_Memory(U32, GS_PHYSICAL_MEM*, U32*)</code><br>
	 * <i>native declaration : DriverFiles/66-16AO64/AO64eintface.h:272</i>
	 */
	void AO64_66_Free_Physical_Memory(NativeLong BoardNumber, GS_PHYSICAL_MEM memPtr, NativeLongByReference ulError);
	public static class U64 extends PointerType {
		public U64(Pointer address) {
			super(address);
		}
		public U64() {
			super();
		}
	};
	public static class BOOLEAN extends PointerType {
		public BOOLEAN(Pointer address) {
			super(address);
		}
		public BOOLEAN() {
			super();
		}
	};
}
