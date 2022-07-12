package com.example.decode;

import java.util.concurrent.CountDownLatch;

import com.example.activity.CaptureActivity;
import com.example.util.LogUtils;
import com.example.util.Util;

import android.os.Handler;
import android.os.Looper;

/**
 * 解码线程
 * @author lizm
 *
 */
final class DecodeThread extends Thread {
	private final static String TAG = "decodeTest_DecodeThread";
	private CaptureActivity activity;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	DecodeThread(CaptureActivity activity) {
		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		LogUtils.i(TAG, Util.getMethodLine()+"begin Looper.prepare()");
		Looper.prepare();
		handler = new DecodeHandler(activity);
		
		handlerInitLatch.countDown();
		Looper.loop();
		LogUtils.i(TAG, Util.getMethodLine()+"exit Looper");
	}

}
