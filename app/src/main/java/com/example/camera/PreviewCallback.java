package com.example.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import com.example.util.LogUtils;
import com.example.util.Util;

/**
 * Camera 预览回调
 * @author lizm
 *
 */
final class PreviewCallback implements Camera.PreviewCallback {

	private static final String TAG = "decodeTest_"+PreviewCallback.class.getSimpleName();

	private final CameraConfigurationManager configManager;
	private final boolean useOneShotPreviewCallback;
	private Handler previewHandler;
	private int previewMessage;

	PreviewCallback(CameraConfigurationManager configManager,
			boolean useOneShotPreviewCallback) {
		this.configManager = configManager;
		this.useOneShotPreviewCallback = useOneShotPreviewCallback;
	}

	void setHandler(Handler previewHandler, int previewMessage) {
		this.previewHandler = previewHandler;
		this.previewMessage = previewMessage;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera arg1) {
		// TODO Auto-generated method stub
		Point cameraResolution = configManager.getCameraResolution();
		if (previewHandler != null) {
			Message message = previewHandler.obtainMessage(previewMessage,
					cameraResolution.x, cameraResolution.y, data);
			message.sendToTarget();
			previewHandler = null;
		} else {
			LogUtils.d(TAG, Util.getMethodLine()+"Got preview callback, but no handler for it");
		}
	}
}
