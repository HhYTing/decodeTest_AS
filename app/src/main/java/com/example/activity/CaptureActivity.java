package com.example.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera.CameraManager;
import com.example.decode.CaptureActivityHandler;
import com.example.decode.CodeType;
import com.example.decodeTest.R;
import com.example.decodeTest.testSetting;
import com.example.util.LogUtils;
import com.example.util.MessageID;
import com.example.util.PlanarYUVLuminanceSource;
import com.example.util.Util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.common.face.api.FaceUtil;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {
	private final static String TAG = "decodeTest_CaptureActivity";
	public static final int REQUEST_CODE_SCAN_DECODE = 1005;
	public final static String RESULT_CODE_SCAN_DECODE_TEXT = "scan decode result text";
	private CaptureActivityHandler mCaptureActivityHandler;
	private boolean hasSurface;
	private View resultView;
	private View buttonView;
	private TextView TextView_resultText;
	private Bitmap mBitmap = null;
	private BeepManager beepManager;
	private long start = 0;
	private long end = 0;
	private long start_1 = 0;
	private long end_1 = 0;
	private long lTimes = 0;
	private long lMaxDecodeTime = 0;
	private long lMinDecodeTime = 0;
	private boolean oneshot;

	//输入框的扫描内容
	private String scanCode;
	private boolean isChecked;

	//误码次数
	private long errorTime = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, Util.getMethodLine()+"begin");
		start_1 = System.currentTimeMillis();
		setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		beepManager = new BeepManager(this);
		hasSurface = false;	

		super.onCreate(savedInstanceState);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		String mode="";
		boolean light;
		Log.i(TAG, Util.getMethodLine()+"begin");
		openCamera();
		Bundle bundle = getIntent().getExtras();

		if(bundle.getString("scan_mode")!= null)
		{
			mode = bundle.getString("scan_mode");
		}
		light = bundle.getBoolean("flash_mode",true);
		oneshot = bundle.getBoolean("oneshot_mode",false);
		scanCode = bundle.getString("editText_scancode");
		isChecked = bundle.getBoolean("isCheck");

		Log.d("CaptureActivity","scanCode:" + scanCode);
		Log.d("CaptureActivity","isChecked:" + isChecked);

		CameraManager.init(getApplication(),this,mode,light);
		doInit();
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.i(TAG,Util.getMethodLine()+"begin");
	}
	@Override
	protected void onStop(){
		super.onStop();
		Log.i(TAG,Util.getMethodLine()+"begin");
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.i(TAG,Util.getMethodLine()+"begin");
	}

	@Override
	protected void onPause() {
		super.onPause();	
		Log.i(TAG, Util.getMethodLine()+"begin");
		doUnInit();
		closeCamera();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, Util.getMethodLine()+"begin");
		beepManager.release();
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(TAG, Util.getMethodLine()+"begin");
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Log.i(TAG, Util.getMethodLine()+"KEYCODE_BACK");
				
				ExitCaptureActivity(Activity.RESULT_CANCELED,null);
				return true;
			case KeyEvent.KEYCODE_FOCUS:
			case KeyEvent.KEYCODE_CAMERA:
				// Handle these events so they don't launch the Camera app
				//return true;
				// Use volume up/down to turn on light
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				//cameraManager.setTorch(false);
				//return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				//cameraManager.setTorch(true);
				//return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private synchronized void ExitCaptureActivity(int resultCode, Intent data){
		if(isFinishing()){
			Log.i(TAG, Util.getMethodLine()+"CaptureActivity is finishing, so do nothing");
		}else{
			Log.i(TAG, Util.getMethodLine()+"begin finish CaptureActivity");
			setResult(resultCode, data);
	        finish();
		}
	}
	
	private View.OnClickListener btnClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnReScan:
				LogUtils.i(TAG, Util.getMethodLine()+"btnReScan");
				restartPreviewAfterDelay(0L);
				break;
			default:
				break;
			}
		}
	};
	
	private void resetStatusView() {
	
		if(testSetting.getContinuousScan()==false){
			buttonView.setVisibility(View.GONE);
		}

	}
	
	public void restartPreviewAfterDelay(long delayMS) {
	    if (mCaptureActivityHandler != null) {
	    	mCaptureActivityHandler.sendEmptyMessageDelayed(MessageID.MSG_ID_RESTART_PREVIEW, delayMS);
	    }
	    resetStatusView();
	}
	
	public void stopPreviewAfterDelay(long delayMS) {
	    if (mCaptureActivityHandler != null) {
	    	mCaptureActivityHandler.sendEmptyMessageDelayed(MessageID.MSG_ID_STOP_PREVIEW, delayMS);
	    }
	}

	/**
	 * 解码成功 重新扫码
	 * subclass别调用该接口
	 * @param result
	 */
	public void decodeSuccessful_Callback(Boolean decodeSuccess,String text, int jni_cost_time, int codeType) {
		beepManager.playBeepSoundAndVibrate();
	    displayDecodeResult(decodeSuccess,text, jni_cost_time, codeType, false);
	    //display mBitmap
	    
	    //连续扫描
	    if(testSetting.getContinuousScan()==true){
	    	restartPreviewAfterDelay(0L);
	    }
	}
	
	public void saveBitmap(byte[] yuvdata, int previewWidth, int previewHeight){
		Log.i(TAG, Util.getMethodLine()+"saveBitmap");
		PlanarYUVLuminanceSource planar = new PlanarYUVLuminanceSource();
		//转换bitmap会影响效率，�??以仅在需要保存图片时才传递bitmap�??
		if(testSetting.getNeedSaveBitmap() == true){				
			mBitmap = planar.toBitmap(yuvdata, previewWidth,previewHeight, 0, 0, previewWidth, previewHeight);
			//String parentPathRes = Environment.getExternalStorageDirectory().toString();
			String FileName = "/mnt/sdcard/HDPicture" + "/"  + "hdimage.jpg";
			Log.i(TAG, "saveYuvImage:jpegName = " + FileName);
			try {
				FileOutputStream fout = new FileOutputStream(FileName);
				BufferedOutputStream bos = new BufferedOutputStream(fout);
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				bos.flush();
				bos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.i(TAG, "savebmp Failed");
				e.printStackTrace();
			}
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return;
		}
		if (mCaptureActivityHandler == null) {
			mCaptureActivityHandler = new CaptureActivityHandler(CaptureActivity.this);
			
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return mCaptureActivityHandler;
	}
	
	public void displayDecodeResult(Boolean decodeSuccess,String text, int jni_cost_time, int codeType, boolean bPlayBeep){
		if(bPlayBeep==true){
			//FaceUtil.LedSet("led-blue", 1);
//			beepManager.playBeepSoundAndVibrate();
			lTimes++;
			if(lTimes%2==1){
				TextView_resultText.setTextColor(Color.parseColor("#FFFFFF"));
			}else{
				TextView_resultText.setTextColor(Color.parseColor("#FF0000"));
			}
		}
		end_1 = System.currentTimeMillis();
		LogUtils.i(TAG, Util.getMethodLine()+"startActivity time="+(end_1-start_1));
		
		if(testSetting.getContinuousScan()==false){
			//单次扫描仅在解码成功才显示按钮框
			if(decodeSuccess==true){
				buttonView.setVisibility(View.VISIBLE);
				
			}
		}

		resultView.setVisibility(View.VISIBLE);	  

		if(bPlayBeep==true) {
//			需要比对误码
			if (isChecked) {
				if (!scanCode.equals(text)) {
					errorTime++;
				}
				TextView_resultText.setText("text="+text);
				TextView_resultText.setText("result="+text+"\n"+
						"codeType="+CodeType.getCodeTypeString(codeType)+"\n"+
						"decodeTime="+jni_cost_time+" ms\n"+
						"总次数:"+lTimes + "\n" +
						"误码数：" + errorTime + "\n");
			} else {
				TextView_resultText.setText("text="+text);
				TextView_resultText.setText("result="+text+"\n"+
						"codeType="+CodeType.getCodeTypeString(codeType)+"\n"+
						"decodeTime="+jni_cost_time+" ms\n"+
						"总次数:"+lTimes );
			}

			if(lMaxDecodeTime == 0 || lMaxDecodeTime<jni_cost_time){
				lMaxDecodeTime = jni_cost_time;
			}
			if(lMinDecodeTime == 0 || lMinDecodeTime>jni_cost_time){
				lMinDecodeTime = jni_cost_time;
			}

			if (isChecked) {
				Log.i("testTime",
						"result="+text+"\n"+
								"codeType="+CodeType.getCodeTypeString(codeType)+"\n"+
								"decodeTime="+jni_cost_time+" ms\n"+
								"lMaxDecodeTime="+lMaxDecodeTime+" ms\n"+
								"lMinDecodeTime="+lMinDecodeTime+" ms\n"+
								"总次数:"+lTimes + "\n" +
								"误码数：" + errorTime);
			} else {
				Log.i("testTime",
						"result="+text+"\n"+
								"codeType="+CodeType.getCodeTypeString(codeType)+"\n"+
								"decodeTime="+jni_cost_time+" ms\n"+
								"lMaxDecodeTime="+lMaxDecodeTime+" ms\n"+
								"lMinDecodeTime="+lMinDecodeTime+" ms\n"+
								"总次数:"+lTimes);
			}

		//	FaceUtil.LedSet("led-blue", 0);
			if (oneshot) {
				Toast.makeText(CaptureActivity.this, "编码:" + text, Toast.LENGTH_SHORT).show();
				this.finish();
			}
		}
	}
	
	
	public void displayPreveiwCounts(int preview_counts){
		resultView.setVisibility(View.VISIBLE);	 
		long diff = 0;
		float speed = 0;	
		if(preview_counts==1){
			start = System.currentTimeMillis();
		}		
		if(preview_counts>1){
			end = System.currentTimeMillis();		
			diff = end - start;
			speed = (float) (((preview_counts*1.0-1)/diff)*1000);
		}
		
	    TextView_resultText.setText("preview_counts="+preview_counts+"\n"+
						"timeElapse="+diff+" ms"+ "\n"+
						"FramSpeed="+speed +" fps");
	
	}
	
	public static int openCamera() {
		int ret = 0;
		try {
			ret = CameraManager.openCamera();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = 1;
		}
		return ret;
	}
	public static void closeCamera() {
		CameraManager.closeDriver();
	}
	
	public void doInit() {
		beepManager.updatePrefs();
		Log.i(TAG, Util.getMethodLine()+"continuousScan="+testSetting.getContinuousScan());
		
		setContentView(R.layout.scan_layout);
		
		resultView = findViewById(R.id.result_view);
		buttonView = findViewById(R.id.button_view);
		TextView_resultText = (TextView) findViewById(R.id.result_text);
		((Button) findViewById(R.id.btnReScan)).setOnClickListener(btnClick);
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		
	}
	public void doUnInit() {
		if (mCaptureActivityHandler != null) {
			mCaptureActivityHandler.quitSynchronously();
			mCaptureActivityHandler = null;
		}
	}
}