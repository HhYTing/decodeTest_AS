package com.example.camera;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;

import com.example.activity.CaptureActivity;
import com.example.decodeTest.testSetting;
import com.example.util.LogUtils;
import com.example.util.Util;

import android.util.Log;
import android.view.SurfaceHolder;

public final class CameraManager {
	private final static String TAG = "decodeTest_CameraManager";
	private static CameraManager cameraManager;

	static final int SDK_INT;
	static {
		int sdkInt;
		try {
			sdkInt = android.os.Build.VERSION.SDK_INT;
		} catch (NumberFormatException nfe) {
			sdkInt = 10000;
		}
		SDK_INT = sdkInt;
	}

	private final CameraConfigurationManager configManager;
	private static Camera mCamera;
	private boolean initialized = false;
	private boolean previewing = false;
	private boolean autoFocusing = true;
	private final boolean useOneShotPreviewCallback;
	private final PreviewCallback previewCallback;
	private final AutoFocusCallback autoFocusCallback;
	private static CaptureActivity mActivity;
	private static String defaultSize;
	private static boolean light;

	public static void init(Context context,CaptureActivity activity,String sDefaultSize,boolean bLight) {
		mActivity = activity;
		if (cameraManager == null) {
			cameraManager = new CameraManager(context);
		}
		defaultSize = sDefaultSize;
		light = bLight;
	}
	
	public CaptureActivity getCaptureActivity(){
		return mActivity;
	}

	public static CameraManager get() {
		return cameraManager;
	}

	private CameraManager(Context context) {
		this.configManager = new CameraConfigurationManager(context,mActivity);

		useOneShotPreviewCallback = SDK_INT > 3;
		previewCallback = new PreviewCallback(configManager, useOneShotPreviewCallback);
		autoFocusCallback = new AutoFocusCallback();
	}
	
		@SuppressLint("NewApi")
	public static int  openCamera()throws IOException  {
		try{
			if(mCamera==null){
				mCamera = Camera.open(testSetting.getCameraID());		
			}
		}catch (RuntimeException e) {
			Log.e(TAG, "open camera failed");
			return 1;
		}
		
		if (mCamera == null) {
			return 1;
		}
		return 0;
	}
	
	/**
	 * 打开camera
	 * @param holder
	 * @throws IOException
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public void openDriver(SurfaceHolder holder) throws IOException {
		if (mCamera == null) {

			Log.i(TAG, Util.getMethodLine()+"NumberOfCameras="+Camera.getNumberOfCameras());
			openCamera();
			//openCamera();
			if (mCamera == null) {
				throw new IOException();
			}
		}
		mCamera.setPreviewDisplay(holder);
		if (!initialized) {
			configManager.initFromCameraParameters(mCamera,defaultSize,light);
		}
		configManager.setDesiredCameraParameters(mCamera);
	}

	public Point getCameraResolution() {
		return configManager.getCameraResolution();
	}
	
	/**
	 * 获得预览方向
	 * @return
	 */
	public int getCameraOritation(){
		return configManager.getCameraDisplayOrientation();
	}

	/**
	 * 关闭camera
	 */
	public static void closeDriver() {
		if (mCamera != null) {
//			FlashlightManager.disableFlashlight();
			mCamera.release();
			mCamera = null;
		}
	}

	/**
	 * camera�??��预览
	 */
	public void startPreview() {
		if (mCamera != null && !previewing) {
			mCamera.startPreview();
			previewing = true;
		}
	}

	/**
	 * 停止预览
	 */
	public void stopPreview() {
		if (mCamera != null && previewing) {
			if (!useOneShotPreviewCallback) {
				mCamera.setPreviewCallback(null);
			}
			mCamera.stopPreview();
			previewCallback.setHandler(null, 0);
			autoFocusCallback.setHandler(null, 0);
			previewing = false;
		}
	}

	/**
	 * 请求预览�??	 * @param handler 
	 * @param message
	 */
	public void requestPreviewFrame(Handler handler, int message) {
		if (mCamera != null && previewing) {
			//LogUtils.i(TAG, Util.getMethodLine()+"set previewCallback");
			previewCallback.setHandler(handler, message);
			if (useOneShotPreviewCallback) {
				mCamera.setOneShotPreviewCallback(previewCallback);
			} else {
				mCamera.setPreviewCallback(previewCallback);
			}
		}else{
			if(mCamera == null)
				LogUtils.i(TAG, Util.getMethodLine()+"mCamera == null");
			if(previewing == false)
				LogUtils.i(TAG, Util.getMethodLine()+"previewing == false");
		}
	}

	/**
	 * 请求自动对焦 
	 * @param handler
	 * @param message
	 */
	public void requestAutoFocus(Handler handler, int message) {
		if (mCamera != null && previewing) {
			autoFocusCallback.setHandler(handler, message);		
			mCamera.autoFocus(autoFocusCallback);
			autoFocusing = true;		
		}
	}
	
	/**
	 * 取消自动对焦 
	 */
	public void cancelAutoFocus() {
		if (mCamera != null && previewing && autoFocusing==true) {
			mCamera.cancelAutoFocus();//停止预览前要取消自动对焦，否则下次开启预览时会无法对�??			autoFocusing = false;
		}
	}

	/**
	 * 获得 camera对象
	 * @return
	 */
	public Camera getCamera(){
		return mCamera;
	}
	/**
	 * 获得预览参数
	 * @return
	 */
	public Point getDefaultPreviewSize() {
		return configManager.getCameraResolution();
	}
	/**
	 * 获得�??��预览参数
	 * @return
	 */
	public Point getBestPreviewSize() {
		return configManager.getBestCameraResolution(mCamera);
	}
	
	/**
	 * 获得屏幕大小~
	 * @return
	 */
	public Point getScreenResolution(){
		return configManager.getScreenResolution();
	}
	
	/**
	 * 获得当前设备�???合分辨率
	 * @return
	 */
	public Point getBestCameraResolution(){
		return configManager.getBestCameraResolution(mCamera);
	}
	
}
