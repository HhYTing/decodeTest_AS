package com.example.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;

import com.example.util.LogUtils;
import com.example.util.Util;

final class AutoFocusCallback implements Camera.AutoFocusCallback {

	private static final String TAG = "decodeTest_"+AutoFocusCallback.class.getSimpleName();

	private static final long AUTOFOCUS_INTERVAL_MS = 5000L;//2000L;//0L;//1800L;//2000L;

	private Handler autoFocusHandler;
	private int autoFocusMessage;

	void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
		this.autoFocusHandler = autoFocusHandler;
		this.autoFocusMessage = autoFocusMessage;
	}

	@Override
	public void onAutoFocus(boolean success, Camera arg1) {
		// TODO Auto-generated method stub
		LogUtils.d(TAG, Util.getMethodLine()+"begin");
		
		if (autoFocusHandler != null) {
			Message message = autoFocusHandler.obtainMessage(autoFocusMessage, success);
			autoFocusHandler.sendMessageDelayed(message, AUTOFOCUS_INTERVAL_MS);
			autoFocusHandler = null;
		} else {
			LogUtils.d(TAG, Util.getMethodLine()+"Got auto-focus callback, but no handler for it");
		}
	}


}
