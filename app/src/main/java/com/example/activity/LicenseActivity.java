package com.example.activity;

import java.io.File;

import com.example.decodeTest.R;
import com.hd.decoder.Decoder_jni;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
 
public class LicenseActivity extends Activity{
	private static final String DEBUG_TAG = "MainActivity";
	public static final int REQUEST_CODE_ACTIVATE_LICENSE = 1005;
	private static final String SD_PATH = Environment.getExternalStorageDirectory().toString();
	private EditText m_editRecvData;
	private Handler mMainMessageHandler;
	private EditText path_EditText = null;
	private RadioGroup processGroup = null;
    private RadioButton sdPathButton = null;
	private RadioButton absolutePathButton = null;
	private boolean isRootSD = true;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(DEBUG_TAG,"onCreate()");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_license);
        setResult(Activity.RESULT_CANCELED);
       
        ((Button) findViewById(R.id.button_activate_license)).setOnClickListener(btnClick);
        path_EditText = (EditText)findViewById(R.id.editText_pincode);
        
        m_editRecvData=(EditText)findViewById(R.id.activity_main_editRecvData);
        mMainMessageHandler = new MessageHandler(Looper.myLooper());
        
        processGroup = (RadioGroup)findViewById(R.id.processGroup);
        sdPathButton = (RadioButton)findViewById(R.id.sdPath);
	    absolutePathButton = (RadioButton)findViewById(R.id.absolutePath);
        //为RadioGroup设置监听器，需要注意的是，这里的监听器和Button控件的监听器有所不同
        processGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(sdPathButton.getId() == checkedId){
					Log.d(DEBUG_TAG, "select sd Path");
					showLogMessage("select sd Path");
					isRootSD = true;
				}else if(absolutePathButton.getId() == checkedId){
					Log.d(DEBUG_TAG, "select absolute Path");
					showLogMessage("select absolute Path");
					isRootSD = false;
				}
			}
		});
        
        
        showLogMessage("备注1：请打开WIFI.");
        showLogMessage("备注2：存放license的目录必须保证权限是应用可读可写, 默认存放在SD卡根目录的hdcrt.lic文件里, 用户可以改变存放路径.");
        showLogMessage("SD_ROOT_PATH="+SD_PATH);
        Log.d(DEBUG_TAG,"SD_ROOT_PATH="+SD_PATH);
        //sdPath2absolutePath("/test/13.txt");
    }
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.e(DEBUG_TAG,"onStart()");
	}
	@Override
	protected void onStop(){
		super.onStop();
		Log.e(DEBUG_TAG,"onStop()");
	}
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.e(DEBUG_TAG,"onRestart()");
	}
	@Override
	protected void onResume(){
		super.onResume();
		Log.e(DEBUG_TAG,"onResume()");
	}
	@Override
	protected void onPause(){
		super.onPause();
		Log.e(DEBUG_TAG,"onPause()");
		
	}
	@Override 
	protected void onDestroy(){
		super.onDestroy();		
		Log.e(DEBUG_TAG, "onDestroy...");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        Log.e(DEBUG_TAG, "requestCode="+requestCode+",onActivityResult=" + resultCode);
	}
	
	
	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_activate_license: {
				int ret = 0;
				String filePath = path_EditText.getText().toString();
				String finalPath = null;
				if(isRootSD==true){
					finalPath = sdPath2absolutePath(filePath);
				}else{
					finalPath = filePath;
				}
				ret = Decoder_jni.getInstance().generate_license(getApplicationContext(), finalPath, 
													"47.107.124.158", 8001, "ZW001_test", "ux69T3Re");
				if(ret!=0){
					Log.e(DEBUG_TAG,"generate_license failed,ret="+ret);
					if(ret==-23){
						showLogMessage("激活失败,ret="+ret+",请打开wifi.");
					}else{
						showLogMessage("激活失败,ret="+ret);
					}
				}else{
					Log.e(DEBUG_TAG,"generate_license successful");
					showLogMessage("license激活成功, license文件存放在： " + finalPath);
				}
				
				break;
			}
			
			default:
				break;
			}
		}
	};
	
	public void showLogMessage(String msg) {
		Message updateMessage = mMainMessageHandler.obtainMessage();
		updateMessage.obj=msg;
		updateMessage.what=R.id.activity_main_editRecvData;
		updateMessage.sendToTarget();
	}
	
	class MessageHandler extends Handler{
		private long mLogCount = 0;
		public MessageHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg){
			switch(msg.what){
			case R.id.activity_main_editRecvData:
				if(mLogCount>100){
					mLogCount=0;
					m_editRecvData.setText("");
				}
				String messageString = (String) (msg.obj);
				int cursor = m_editRecvData.getSelectionStart();
				m_editRecvData.getText().insert(cursor, messageString + "\n");
				++mLogCount;
				break;
			}

		}
	}
	
	public static String sdPath2absolutePath(String path) {
		Log.d(DEBUG_TAG,"path="+path);
		byte[] tmp = path.getBytes();
		int i = 0;
		for(i=path.length()-1;i>=0;i--){
			//Log.d(DEBUG_TAG,"i="+ i + ",tmp["+i+"]="+tmp[i]);
			if(tmp[i]=='/'){
				break;
			}
		}

		byte[] Dir = new byte[i+1];
		for(i=0;i<Dir.length;i++){
			Dir[i] = tmp[i];
			//Log.d(DEBUG_TAG,"i="+ i + ",Dir["+i+"]="+Dir[i]);
		}
	
		String DirStr = new String(Dir);
		Log.d(DEBUG_TAG,"DirStr="+DirStr);
		
		String absolueteDir = null;
		if(tmp[0]=='/'){
			absolueteDir = SD_PATH + DirStr;
		}else{
			absolueteDir = SD_PATH + "/" + DirStr;
		}
		
		Log.d(DEBUG_TAG,"absolueteDir="+absolueteDir);
		File savedDir = new File(absolueteDir);
		if (!savedDir.exists()) {
			savedDir.mkdirs();
		}
		
		String absoluetePathStr = null;
		if(tmp[0]=='/'){
			absoluetePathStr = SD_PATH + path;
		}else{
			absoluetePathStr = SD_PATH + "/" + path;
		}
		Log.d(DEBUG_TAG,"absoluetePathStr="+absoluetePathStr);
		return absoluetePathStr;
		
	}
}
