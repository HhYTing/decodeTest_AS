package com.example.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.example.activity.CaptureActivity;
import com.example.util.LogUtils;
import com.example.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Camera参数配置
 * @author lizm
 *
 */
final class CameraConfigurationManager {

	private static final String TAG = "decodeTest_"+CameraConfigurationManager.class
			.getSimpleName();
	private static final Pattern COMMA_PATTERN = Pattern.compile(",");
	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;
	private Point cameraBestResolution;
	private int previewFormat;
	private String previewFormatString;
	CaptureActivity mActivity;
	private int displayOrientation = 0;
	private boolean bLight;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	CameraConfigurationManager(Context context, CaptureActivity activity) {
		this.context = context;
		mActivity = activity;
	}

	@SuppressWarnings("deprecation")
	void initFromCameraParameters(Camera camera,String defaultSize,boolean light) {
		Camera.Parameters parameters = camera.getParameters();
		previewFormat = parameters.getPreviewFormat();
		previewFormatString = parameters.get("preview-format");
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		screenResolution = new Point(display.getWidth(), display.getHeight());

		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;

		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}
		bLight = light;


		
		
//		defaultSize = "1280x720";
//		defaultSize = "1024x768";
//		defaultSize = "640x480";//分辨率
		
		
		Log.i(TAG, Util.getMethodLine()+"finally previewSize="+defaultSize);
		String size[] = defaultSize.split("x");
		cameraResolution = new Point(Integer.parseInt(size[0]),Integer.parseInt(size[1]));
	}

	void setDesiredCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		Log.d(TAG, "cameraResolution.x=" + cameraResolution.x + ",cameraResolution.y=" + cameraResolution.y);
		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		if (bLight) {
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(parameters);
		}
//		parameters.getMinExposureCompensation()
//		().getMaxExposureCompensation
//		int exposure = parameters.getMinExposureCompensation();
//		exposure = parameters.getMaxExposureCompensation()-exposure;
//		exposure = exposure/10 +  parameters.getMinExposureCompensation();
//		parameters.setExposureCompensation(exposure);
//		parameters.setAutoExposureLock(false);
//		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

		setFocusMode(parameters, camera);
		setCameraDisplayOrientation(mActivity, camera);
		camera.setParameters(parameters);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	private void setCameraDisplayOrientation(Activity activity, Camera camera) {
		@SuppressWarnings("static-access")
		int numberOfCameras = camera.getNumberOfCameras();
		int cameraId;
		CameraInfo cameraInfo = new CameraInfo();
		;
		for (int i = 0; i < numberOfCameras; i++) {
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = CameraInfo.CAMERA_FACING_BACK;
				break;
			} else {
				cameraId = CameraInfo.CAMERA_FACING_FRONT;
				break;
			}
		}
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			displayOrientation = (cameraInfo.orientation + degrees) % 360;
			displayOrientation = (360 - displayOrientation) % 360; // compensate
																	// the
																	// mirror
		} else { // back-facing
			displayOrientation = (cameraInfo.orientation - degrees + 360) % 360;
		}
		LogUtils.i(TAG, Util.getMethodLine()+"info.orientation = " + cameraInfo.orientation + " result="
				+ displayOrientation);
		camera.setDisplayOrientation(displayOrientation);
	}

	/**
	 * 获得当前预览方向
	 * 
	 * @return
	 */
	public int getCameraDisplayOrientation() {
		return displayOrientation;
	}

	Point getCameraResolution() {
		return cameraResolution;
	}
	
	public Point getBestCameraResolution(Camera camera){
		if(camera==null){
			Log.i(TAG, Util.getMethodLine()+"camera=null");
			return null;
		}
		Parameters parameters = camera.getParameters();
		Point screenResolutionForCamera = new Point();
		screenResolutionForCamera.x = screenResolution.x;
		screenResolutionForCamera.y = screenResolution.y;

		if (screenResolution.x < screenResolution.y) {
			screenResolutionForCamera.x = screenResolution.y;
			screenResolutionForCamera.y = screenResolution.x;
		}
		cameraBestResolution = findBestPreviewSizeValue(parameters,
				screenResolutionForCamera);
		return cameraBestResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	int getPreviewFormat() {
		return previewFormat;
	}

	String getPreviewFormatString() {
		return previewFormatString;
	}
	
	private static Point findBestPreviewSizeValue(Camera.Parameters parameters,
			Point screenResolution) {

		List<Camera.Size> rawSupportedSizes = parameters
				.getSupportedPreviewSizes();
		if (rawSupportedSizes == null) {
			Log.w(TAG,Util.getMethodLine()+
					"Device returned no supported preview sizes; using default");
			Camera.Size defaultSize = parameters.getPreviewSize();
			return new Point(defaultSize.width, defaultSize.height);
		}

		// Sort by size, descending
		List<Camera.Size> supportedPreviewSizes = new ArrayList<Camera.Size>(
				rawSupportedSizes);
		Collections.sort(supportedPreviewSizes, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size a, Camera.Size b) {
				int aPixels = a.height * a.width;
				int bPixels = b.height * b.width;
				if (bPixels < aPixels) {
					return -1;
				}
				if (bPixels > aPixels) {
					return 1;
				}
				return 0;
			}
		});

		StringBuilder previewSizesString = new StringBuilder();
		for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
			previewSizesString.append(supportedPreviewSize.width).append('x')
					.append(supportedPreviewSize.height).append(' ');
		}
		Log.i(TAG, Util.getMethodLine()+"Supported preview sizes: " + previewSizesString);

		Point bestSize = null;
		float screenAspectRatio = (float) screenResolution.x
				/ (float) screenResolution.y;

		float diff = Float.POSITIVE_INFINITY;
		for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
			int realWidth = supportedPreviewSize.width;
			int realHeight = supportedPreviewSize.height;
			int pixels = realWidth * realHeight;
			// if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
			// continue;
			// }
			boolean isCandidatePortrait = realWidth < realHeight;
			int maybeFlippedWidth = isCandidatePortrait ? realHeight
					: realWidth;
			int maybeFlippedHeight = isCandidatePortrait ? realWidth
					: realHeight;
			if (maybeFlippedWidth == screenResolution.x
					&& maybeFlippedHeight == screenResolution.y) {
				Point exactPoint = new Point(realWidth, realHeight);
				Log.i(TAG, Util.getMethodLine()+"Found preview size exactly matching screen size: "
						+ exactPoint);
				return exactPoint;
			}
			float aspectRatio = (float) maybeFlippedWidth/ (float) maybeFlippedHeight;
			float newDiff = Math.abs(aspectRatio - screenAspectRatio);
			if (newDiff < diff) {
				bestSize = new Point(realWidth, realHeight);
				diff = newDiff;
			}
		}

		if (bestSize == null) {
			Camera.Size defaultSize = parameters.getPreviewSize();
			bestSize = new Point(defaultSize.width, defaultSize.height);
			Log.i(TAG, Util.getMethodLine()+"No suitable preview sizes, using default: " + bestSize);
		}

		Log.i(TAG, Util.getMethodLine()+"Found best approximate preview size: " + bestSize);
		return bestSize;
	}

	private void setFocusMode(Camera.Parameters parameters,Camera camera) {
		String focusMode = Camera.Parameters.FOCUS_MODE_AUTO;	
		Log.i(TAG, Util.getMethodLine()+"focusMode="+focusMode);
		parameters.setFocusMode(focusMode);
		
	}
	
	

}
