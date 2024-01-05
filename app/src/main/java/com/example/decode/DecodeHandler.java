package com.example.decode;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.activity.CaptureActivity;
import com.example.util.FileEncodingDetect;
import com.example.util.LogUtils;
import com.example.util.MessageID;
import com.example.util.Util;
import com.hd.decoder.Decoder_jni;
import com.hd.decoder.Result;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * 解码Handler
 *
 */
public class DecodeHandler extends Handler {
	private final static String TAG = "decodeTest_DecodeHandler";
	private CaptureActivity activity = null;
	private int jni_cost_time=0;
	private long delay_scene_start = System.currentTimeMillis();
	private long delay_scene_end= System.currentTimeMillis();
	private long delay_samebar_start = System.currentTimeMillis();
	private long delay_samebar_end= System.currentTimeMillis();
	private int delay_scene_time;
	private int delay_samebar_time;
	private byte[] cur_data;
	private byte[] pre_data;

	DecodeHandler(CaptureActivity activity) {
		LogUtils.i(TAG, Util.getMethodLine()+"DecodeHandler create");
		this.activity = activity;

	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
			case MessageID.MSG_ID_DECODE:
//				decode((byte[]) message.obj, message.arg1, message.arg2);
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException ex) {
//					ex.printStackTrace();
//				}
				decode_after_SceneChangeDetection((byte[]) message.obj, message.arg1, message.arg2);
				break;
			case MessageID.MSG_ID_QUIT:
				LogUtils.i(TAG, Util.getMethodLine()+"handleMessage--MSG_ID_QUIT");

				LogUtils.i(TAG, Util.getMethodLine()+"begin Looper.myLooper().quit()");
				Looper.myLooper().quit();
				break;
		}
	}

	/**
	 *
	 * @param yuvdata
	 *            预览数据
	 * @param previewWidth
	 *            预览宽度
	 * @param previewHeight
	 *            预览高度R
	 */
	private void decode(byte[] yuvdata, int previewWidth, int previewHeight) {
		Handler handler = activity.getHandler();
		jni_cost_time=0;
		long start = System.currentTimeMillis();
		Result result = Decoder_jni.getInstance().decoder_iDecode(yuvdata, previewWidth, previewHeight);
		long end = System.currentTimeMillis();
		jni_cost_time = (int) (end - start);

		if(result!=null){
			delay_scene_start = System.currentTimeMillis();
			delay_samebar_end = System.currentTimeMillis();
			cur_data = result.getData();
			if(!Arrays.equals(cur_data, pre_data)){
				//Log.i(TAG, "cur_data != pre_data"+",cur_data="+Arrays.toString(cur_data)+",pre_data="+Arrays.toString(pre_data));
				pre_data = result.getData();
				delay_samebar_start = System.currentTimeMillis();
			}
			else{
				//Log.i(TAG, "cur_data == pre_data"+",cur_data="+Arrays.toString(cur_data)+",pre_data="+Arrays.toString(pre_data));
				delay_samebar_time = (int)(delay_samebar_end - delay_samebar_start);
				Log.i(TAG, "delay_samebar_time ="+delay_samebar_time);
				if(delay_samebar_time > 0)
					delay_samebar_start = System.currentTimeMillis();
				else{
					if (handler != null) {
						Message message = Message.obtain(handler, MessageID.MSG_ID_DECODE_FAILED,null);
						Bundle data = new Bundle();
						data.putInt(CaptureActivityHandler.KEY_JNI_COST_TIME,jni_cost_time);
						message.setData(data);
						message.sendToTarget();
						return;
					}
				}
			}
		}

		if (result!=null) {
			LogUtils.w(TAG,Util.getMethodLine()+",decode successful,data(hex):");
			LogUtils.printLargeData(TAG, Util.byte2string(result.getData(), true));
			//	activity.saveBitmap(yuvdata, previewWidth, previewHeight);
			if (handler != null) {
				/*******hex to string**begin**********/
				String strEncodeType = FileEncodingDetect.getInstance().detectEncoding(result.getData());
				Log.i(TAG,Util.getMethodLine()+"strEncodeType="+strEncodeType);
				if(strEncodeType==null){
					strEncodeType = "UTF-8";
				}
				String decodeDataStr = null;
				try {
					decodeDataStr = new String(result.getData(), strEncodeType);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "UnsupportedEncodingException catch");
					e.printStackTrace();
					decodeDataStr = null;
				}
				if(decodeDataStr!=null){
					Log.i(TAG,Util.getMethodLine()+"mBarcodeText="+decodeDataStr);
				}else {
					Log.i(TAG,Util.getMethodLine()+"mBarcodeText=null");
				}
				/*******hex to string**end**********/

				Message message = Message.obtain(handler, MessageID.MSG_ID_DECODE_SUCCEEDED, decodeDataStr);
				Bundle data = new Bundle();
				data.putInt(CaptureActivityHandler.KEY_JNI_COST_TIME,jni_cost_time);
				data.putInt(CaptureActivityHandler.KEY_CODE_TYPE,result.getCodeType());
				message.setData(data);
				message.sendToTarget();
			}
		} else {
			LogUtils.w(TAG,Util.getMethodLine()+",decode failed!!!!!");
			if (handler != null) {

				//存图示例
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String timeStamp = simpleDateFormat.format(new Date());
				String name = "decode0628_"+timeStamp;
				//saveData(yuvdata, name);


				Message message = Message.obtain(handler, MessageID.MSG_ID_DECODE_FAILED,null);
				Bundle data = new Bundle();
				data.putInt(CaptureActivityHandler.KEY_JNI_COST_TIME,jni_cost_time);
				message.setData(data);
				message.sendToTarget();
			}
		}
	}

	private void decode_after_SceneChangeDetection(byte[] yuvdata, int previewWidth, int previewHeight) {
		jni_cost_time = 0;
		long start = System.currentTimeMillis();
		int scene_change = Decoder_jni.getInstance().SceneChangeDetection(yuvdata, previewWidth, previewHeight);
		long end = System.currentTimeMillis();
		jni_cost_time = (int) (end - start);
		Log.i(TAG,Util.getMethodLine()+"scene_change = "+scene_change);
		if(scene_change > 0)
			delay_scene_end = System.currentTimeMillis();
		delay_scene_time = (int)(delay_scene_end - delay_scene_start);
		if(delay_scene_time > 0) {
            decode(yuvdata, previewWidth, previewHeight);
//            try {
//                Thread.sleep(150);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
        }
		else{
			Handler handler = activity.getHandler();
			if (handler != null) {
				Message message = Message.obtain(handler, MessageID.MSG_ID_DECODE_FAILED,null);
				Bundle data = new Bundle();
				data.putInt(CaptureActivityHandler.KEY_JNI_COST_TIME,jni_cost_time);
				message.setData(data);
				message.sendToTarget();
			}
		}
	}




	//存图函数
	public void saveData(byte[] yuvdata, String type){
		//Log.i(TAG, Util.getMethodLine()+"saveData");
		String FileName = "/sdcard/yuvdata/"  + type + ".yuvdata"; //存储路径修改
		if(yuvdata != null){
			File file = new File(FileName);
			if(file.exists()){
				file.delete();
			}
			try {
				FileOutputStream fos = new FileOutputStream(file);
				fos.write(yuvdata,0, yuvdata.length);
				fos.flush();
				fos.close();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "saveyuvdata Failed");
				e.printStackTrace();
			}
		}
	}
}
