package com.example.decodeTest;

public class testSetting {
	public final static int CAMERA_ID_BACK = 0;
	public final static int CAMERA_ID_FRONT = 1;
	private static boolean bContinuousScan = true;
	private static boolean bNeedSaveBitmap = false;
	private static int iCameraID = CAMERA_ID_BACK;
	
	public static void setContinuousScan(boolean value){
		bContinuousScan = value;
	}
	public static boolean getContinuousScan(){
		return bContinuousScan;
	}
	
	public static void setNeedSaveBitmap(boolean value){
		bNeedSaveBitmap = value;
	}
	public static boolean getNeedSaveBitmap(){
		return bNeedSaveBitmap;
	}
	
	public static void setCameraID(int value){
		iCameraID = value;
	}
	public static int getCameraID(){
		return iCameraID;
	}
}
