package com.example.decode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.activity.CaptureActivity;
import com.example.camera.CameraManager;
import com.example.decodeTest.testSetting;
import com.example.util.LogUtils;
import com.example.util.MessageID;
import com.example.util.Util;

public final class CaptureActivityHandler extends Handler {
	private final static String TAG = "decodeTest_CaptureActivityHandler";
	public final static String KEY_SUCCESS_COUNTS = "KEY_SUCCESS_COUNTS";
	public final static String KEY_FAIL_COUNTS = "KEY_FAIL_COUNTS";
	public final static String KEY_PREVIEW_COUNTS = "KEY_PREVIEW_COUNTS";
	public final static String KEY_JNI_COST_TIME = "KEY_JNI_COST_TIME";
	public final static String KEY_BITMAP = "KEY_BITMAP";
	public final static String KEY_CODE_TYPE = "KEY_CODE_TYPE";
	DecodeThread decodeThread = null;
	CaptureActivity activity = null;
	private State state;


	private enum State {
		INITED, PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(CaptureActivity activity) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity);
		decodeThread.start();
		state = State.INITED;		
		restartPreview();
	}

	@Override
	public void handleMessage(Message message) {
		//LogUtils.i(TAG, Util.getMethodLine()+"message.what="+message.what);
		switch (message.what) {
		case MessageID.MSG_ID_AUTO_FOCUS:
			LogUtils.i(TAG, Util.getMethodLine()+"message.what=MSG_ID_AUTO_FOCUS");
			//if (state == State.PREVIEW) {
				CameraManager.get().requestAutoFocus(this, MessageID.MSG_ID_AUTO_FOCUS);
			//}
			break;
		case MessageID.MSG_ID_RESTART_PREVIEW:			
			LogUtils.i(TAG, Util.getMethodLine()+"message.what=MSG_ID_RESTART_PREVIEW");
			restartPreview();
			break;
		case MessageID.MSG_ID_STOP_PREVIEW:		
			LogUtils.i(TAG, Util.getMethodLine()+"message.what=MSG_ID_STOP_PREVIEW");			
			stopPreviewAndFocus();
			break;
		case MessageID.MSG_ID_DECODE_SUCCEEDED:
			LogUtils.i(TAG, Util.getMethodLine()+"Decode successful");
			state = State.SUCCESS;
			//这里stopPreviewAndFocus会影响连续扫描的速率,仅在单次扫描时stop�??			
			if(testSetting.getContinuousScan()==false){			
				stopPreviewAndFocus();
			}else{
				request_PreviewFrame();
			}
			String resultText = (String)message.obj;
			Bundle data = message.getData();
			int jni_cost_time = data.getInt(CaptureActivityHandler.KEY_JNI_COST_TIME);
			int codeType = data.getInt(CaptureActivityHandler.KEY_CODE_TYPE);
			LogUtils.i(TAG, Util.getMethodLine()+"resultText="+resultText);
			//activity.decodeSuccessful_Callback(true, resultText, jni_cost_time, codeType);// 解析成功
			activity.displayDecodeResult(true, resultText, jni_cost_time, codeType, true);
			break;
		case MessageID.MSG_ID_DECODE_FAILED:
			LogUtils.i(TAG, Util.getMethodLine()+"Decode failed");
			state = State.PREVIEW;		
			request_PreviewFrame();
			data = message.getData();
			jni_cost_time = data.getInt(CaptureActivityHandler.KEY_JNI_COST_TIME);
			activity.displayDecodeResult(false, null, jni_cost_time, 0, false);
			break;
		}

	}

	public void quitSynchronously() {
		state = State.DONE;
		stopPreviewAndFocus();
		Message quit = Message.obtain(decodeThread.getHandler(), MessageID.MSG_ID_QUIT);
		quit.sendToTarget();
		removeMessages(MessageID.MSG_ID_DECODE_SUCCEEDED);
		removeMessages(MessageID.MSG_ID_DECODE_FAILED);
		removeMessages(MessageID.MSG_ID_DECODE);
		removeMessages(MessageID.MSG_ID_AUTO_FOCUS);
	}
	
	private void stopPreviewAndFocus() {
		CameraManager.get().cancelAutoFocus();//停止预览前要取消对焦，否则下次开启预览时会无法对�??		
		CameraManager.get().stopPreview();
		state = State.INITED;
	}

	private void restartPreview() {				
		CameraManager.get().startPreview();
		LogUtils.i(TAG, Util.getMethodLine()+"state ="+state);
		if ((state == State.INITED) || (state == State.PREVIEW) || (state == State.SUCCESS)) {
			request_PreviewFrame();
			CameraManager.get().requestAutoFocus(this, MessageID.MSG_ID_AUTO_FOCUS);

		}
		state = State.PREVIEW;
	}
	
	private void request_PreviewFrame() {
		LogUtils.i(TAG, Util.getMethodLine()+"begin requestPreviewFrame");
		CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),MessageID.MSG_ID_DECODE);		
	}

}
