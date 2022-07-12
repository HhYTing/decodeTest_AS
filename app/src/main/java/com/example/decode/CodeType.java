package com.example.decode;

public class CodeType {
	public final static int NO_SUPPORT 	=   -1;
	public final static int UNKNOW_CODE 	=   0;

	/* code type define */
	public final static int  SET_CODETYPE_EAN13    =      1;
	public final static int  SET_CODETYPE_EAN8     =      2;
	public final static int  SET_CODETYPE_UPCA     =      3;
	public final static int  SET_CODETYPE_UPCE     =      4;
	public final static int  SET_CODETYPE_C128     =      5;
	public final static int  SET_CODETYPE_C39      =      6;
	public final static int  SET_CODETYPE_ITF25    =      7;
	public final static int  SET_CODETYPE_CBAR     =      8;
	public final static int  SET_CODETYPE_C93      =      9;
	public final static int  SET_CODETYPE_TRIOPTIC     =      10;
	public final static int  SET_CODETYPE_NL128    =      11;
	public final static int  SET_CODETYPE_AIM128     =      12;
	public final static int  SET_CODETYPE_EAN128   =      13;
	public final static int  SET_CODETYPE_PHARMA_ONE     =      14;
	public final static int  SET_CODETYPE_PHARMA_TWO    =      15;
	public final static int  SET_CODETYPE_MATRIX25 =      16;
	public final static int  SET_CODETYPE_PLESSEY   =      17;
	public final static int  SET_CODETYPE_C11      =      18;
	public final static int  SET_CODETYPE_RSS_EXP   =     19;
	public final static int  SET_CODETYPE_RSS_EXP_ST =    20;
	public final static int  SET_CODETYPE_RSS_14_LIM =    21;
	public final static int  SET_CODETYPE_RSS_14    =     22;
	public final static int  SET_CODETYPE_RSS_14_ST =     23;
	public final static int  SET_CODETYPE_MSI    =      24;
	public final static int  SET_CODETYPE_STD25    =      25;
	public final static int  SET_CODETYPE_IDSTL25  =      26;
	public final static int  SET_CODETYPE_TELEPEN  =      27;
	public final static int  SET_CODETYPE_NEC25    =      28;
	public final static int  SET_CODETYPE_DTLOGC25 =      29;
	public final static int  SET_CODETYPE_C32   =     30;
	public final static int  SET_CODETYPE_DOT   =     31;
	public final static int  SET_CODETYPE_PDF417	=	    32;
	public final static int  SET_CODETYPE_QR		=	    33;
	public final static int  SET_CODETYPE_DATAMATRIX	=    34;
	public final static int  SET_CODETYPE_M_PDF		 =   35;
	public final static int  SET_CODETYPE_M_QR		 =   36;
	public final static int  SET_CODETYPE_MAXICODE	 =   37;
	public final static int  SET_CODETYPE_AZTEC		 =   38;
	public final static int  SET_CODETYPE_HX		 =	    39;
	public final static int  SET_CODETYPE_GM =   40;
	public final static int  SET_CODETYPE_CODABLOCK_A =   41;
	public final static int  SET_CODETYPE_CODABLOCK_F =   42;
	public final static int  SET_CODETYPE_COD16K =   43;
	public final static int  SET_CODETYPE_COD49 =   44;
	public final static int  SET_CODETYPE_COMPOSITE =   45;

	public final static int  SET_CODETYPE_KP_POST =   51;
	public final static int  SET_CODETYPE_AUT_POST =   52;

	public final static int  SET_CODETYPE_OCR =   54;

	/* SetBar paramter define */
	public final static int  SET_CLASS_ENABLE      =      0x10001000;
	public final static int  SET_CLASS_PROPERTY    =      0x10001001;
	public final static int  SET_CLASS_LENGTH      =      0x10001002;  //
	public final static int  SET_CLASS_MISC      =        0x10001003;  //
	public final static int  SET_CLASS_USERDEFINE      =  0xFFFFFFFF;  //

	// 条码使能开关
	public final static int  SET_VAL_DISABLE       =      0;           // 关闭使能
	public final static int  SET_VAL_ENABLE        =      1;           // 开启使能
	public final static int  SET_VAL_INVENABLE     =      2;           // 开启反色使能

	// 通用码制属性，种类有:CodeBar\ITF25\Matrix25\NEC25
	//|_________________________________________________________________________________________
	//| bit0     | bit1     | bit2     | bit3     | bit4     | bit5     | bit6     | bit7      |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	//| checksum | undef    | undef    | strip    | undef    | undef    | undef    | undef     |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	public final static int  SET_VAL_NO_CHECK      =      0;           // Disable checksum checking (default)
	public final static int  SET_VAL_CHECKSUM      =      1;           // Enable checksum checking
	public final static int  SET_VAL_STRIP         =      8;           // Strip the checksum from the result string(与checksum属性组合使用)

	// C11专用属性，不允许无校验
	//|_________________________________________________________________________________________
	//| bit0     | bit1     | bit2     | bit3     | bit4     | bit5     | bit6     | bit7      |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	//| twocheck | onecheck | undef    | strip    | undef    | undef    | undef    | undef     |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	// 优先级顺序: twocheck > onecheck
	public final static int  SET_VAL_C11_NO_CHECK    =    0;           // Disable checksum checking (same with twocheck)
	public final static int  SET_VAL_C11_TWOCHECK    =    1;           // Two checksum digits checked (default)
	public final static int  SET_VAL_C11_ONECHECK    =    2;           // One checksum digit checked
	public final static int  SET_VAL_C11_STRIP       =    8;           // Strip the checksum from the result string(与checksum属性组合使用)

	// MSI-Pleasy专用属性
	//|_________________________________________________________________________________________
	//| bit0     | bit1     | bit2     | bit3     | bit4     | bit5     | bit6     | bit7      |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	//| mod10    | mod10/11 | mod10/10 | strip    | undef    | undef    | undef    | undef     |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	// 优先级顺序: mod10 > mod10/11 > mod10/10
	public final static int  SET_VAL_MSIP_NO_CHECK    =   0;           // Disable checksum checking (default)
	public final static int  SET_VAL_MSIP_MOD10       =   1;           // a single mod 10 checksum check           (first compare)
	public final static int  SET_VAL_MSIP_MOD10_MOD11 =   2;           // a mod 11 and a mod 10 checksum check     (second compare)
	public final static int  SET_VAL_MSIP_MOD10_MOD10 =   4;           // two mod 10 checksum checks               (third compare)
	public final static int  SET_VAL_MSIP_STRIP       =   8;           // Strip the checksum from the result string(与checksum属性组合使用)

	// C39专用属性
	//|_________________________________________________________________________________________
	//| bit0     | bit1     | bit2     | bit3     | bit4     | bit5     | bit6     | bit7      |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	//| checksum | undef    | undef    | strip    | fullASC  | undef    | undef    | undef     |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	public final static int  SET_VAL_C39_NO_CHECK     =   0x00;        // Disable checksum checking (default)
	public final static int  SET_VAL_C39_CHECKSUM     =   0x01;        // Enable checksum checking
	public final static int  SET_VAL_C39_STRIP        =   0x08;        // Strip the checksum from the result string(与checksum属性组合使用)
	public final static int  SET_VAL_C39_FULLASCII    =   0x10;        // Full ASCII

	// EAN/UPC专用属性
	//|_________________________________________________________________________________________
	//| bit0     | bit1     | bit2     | bit3     | bit4     | bit5     | bit6     | bit7      |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	//| checksum | undef    | undef    | strip    | supp     | supp2    | supp5    | expan     |
	//|__________|__________|__________|__________|__________|__________|__________|___________|
	public final static int  SET_VAL_EAN_NO_CHECK     =   0x00;       // Disable checksum checking (无效)
	public final static int  SET_VAL_EAN_CHECKSUM     =   0x01;        // Enable checksum checking (default)
	public final static int  SET_VAL_EAN_STRIP        =   0x08;        // Strip the checksum from the result string(无效)
	public final static int  SET_VAL_EAN_SUPPLEMENTAL =   0x10;        // Enable supplemental decoding
	public final static int  SET_VAL_EAN_SUPPLEMENTAL2 =  0x20;        // Enable 2 digit supplemental symbol
	public final static int  SET_VAL_EAN_SUPPLEMENTAL5 =  0x40;        // Enable 5 digit supplemental symbol
	public final static int  SET_VAL_EAN_EXPANSION     =  0x80;        // Enable EAN8 and UPC8 expand to EAN13 and UPC13

	// 长度属性定义，种类有:ITF25\Matrix25\NEC25\STD25\IDSTL25\DTLOGC25
	//|_____________________________________
	//| bit0 ~ bit3     | bit4 ~ bit7      |
	//|_________________|__________________|
	//| min length      | max length       |
	//|_________________|__________________|
	//public final static int  SET_VAL_MIN_LENGTH(x)   =    (x & 0x0F)
	//public final static int  SET_VAL_MAX_LENGTH(x)   =    ((x & 0x0F) << 4)


	public static String getCodeTypeString(int codeType){
		String ret="";
		switch(codeType){
			case NO_SUPPORT:
				ret = "NO_SUPPORT";
				break;
			case UNKNOW_CODE:
				ret = "UNKNOW_CODE";
				break;
			case SET_CODETYPE_EAN13:
				ret = "EAN-13";
				break;
			case SET_CODETYPE_EAN8:
				ret = "EAN-8";
				break;
			case SET_CODETYPE_UPCA:
				ret = "UPC-A";
				break;
			case SET_CODETYPE_UPCE:
				ret = "UPC-E";
				break;
			case SET_CODETYPE_C128:
				ret = "C128";
				break;
			case SET_CODETYPE_C39:
				ret = "C39";
				break;
			case SET_CODETYPE_ITF25:
				ret = "ITF25";
				break;
			case SET_CODETYPE_CBAR:
				ret = "CodaBar";
				break;
			case SET_CODETYPE_C93:
				ret = "C93";
				break;
			case SET_CODETYPE_TRIOPTIC:
				ret = "TRIOPTIC";
				break;
			case SET_CODETYPE_NL128:
				ret = "C128";
				break;
			case SET_CODETYPE_AIM128:
				ret = "C128";
				break;
			case SET_CODETYPE_EAN128:
				ret = "C128";
				break;
			case SET_CODETYPE_PHARMA_ONE:
				ret = "PHARMA_ONE";
				break;
			case SET_CODETYPE_PHARMA_TWO:
				ret = "PHARMA_TWO";
				break;
			case SET_CODETYPE_MATRIX25:
				ret = "MATRIX 25";
				break;
			case SET_CODETYPE_PLESSEY:
				ret = "PLESSEY";
				break;
			case SET_CODETYPE_C11:
				ret = "C11";
				break;
			case SET_CODETYPE_RSS_EXP:
				ret = "GS1 DATABAR";
				break;
			case SET_CODETYPE_RSS_EXP_ST:
				ret = "GS1 DATABAR";
				break;
			case SET_CODETYPE_RSS_14_LIM:
				ret = "GS1 DATABAR";
				break;
			case SET_CODETYPE_RSS_14:
				ret = "GS1 DATABAR";
				break;
			case SET_CODETYPE_RSS_14_ST:
				ret = "GS1 DATABAR";
				break;
			case SET_CODETYPE_MSI:
				ret = "MSI";
				break;
			case SET_CODETYPE_STD25:
				ret = "STRAIGHT 25";
				break;
			case SET_CODETYPE_IDSTL25:
				ret = "I25";
				break;
			case SET_CODETYPE_TELEPEN:
				ret = "TELEPEN";
				break;
			case SET_CODETYPE_NEC25:
				ret = "NEC25";
				break;
			case SET_CODETYPE_DTLOGC25:
				ret = "HK25";
				break;
			case SET_CODETYPE_C32:
				ret = "C32";
				break;
			case SET_CODETYPE_DOT:
				ret = "DOT";
				break;
			case SET_CODETYPE_PDF417:
				ret = "PDF417";
				break;
			case SET_CODETYPE_QR:
				ret = "QR CODE";
				break;
			case SET_CODETYPE_DATAMATRIX:
				ret = "DATA MATRIX";
				break;
			case SET_CODETYPE_M_PDF:
				ret = "MICROPDF";
				break;
			case SET_CODETYPE_M_QR:
				ret = "MICROQR";
				break;
			case SET_CODETYPE_MAXICODE:
				ret = "MAXICODE";
				break;
			case SET_CODETYPE_AZTEC:
				ret = "AZTEC";
				break;
			case SET_CODETYPE_HX:
				ret = "HANXIN";
				break;
			case SET_CODETYPE_GM:
				ret = "GM";
				break;
			case SET_CODETYPE_CODABLOCK_A:
				ret = "CODEBLOCK A";
				break;
			case SET_CODETYPE_CODABLOCK_F:
				ret = "CODEBLOCK F";
				break;
			case SET_CODETYPE_COD16K:
				ret = "COD16K";
				break;
			case SET_CODETYPE_COD49:
				ret = "COD49";
				break;
			case SET_CODETYPE_COMPOSITE:
				ret = "COMPOSITE";
				break;
			case SET_CODETYPE_KP_POST:
				ret = "KP_POST";
				break;
			case SET_CODETYPE_AUT_POST:
				ret = "AUT_POST";
				break;
			case SET_CODETYPE_OCR:
				ret = "OCR";
				break;
			default:
				ret = "UNKNOW_CODE";
				break;
		}
		return ret;
	}
}
