package com.example.activity;

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

import com.example.decodeTest.R;
import com.hd.decoder.CodeType;
import com.hd.decoder.Decoder_jni;
 
public class MainActivity extends Activity{
	private static final String DEBUG_TAG = "MainActivity";
	private static final String VER = "1.2.20181026";
	private static final String SD_PATH = Environment.getExternalStorageDirectory().toString();
	private EditText m_editRecvData;
	private Handler mMainMessageHandler;

	private RadioGroup priview_rg;
	private RadioButton rb_preview_vga;
	private RadioButton rb_preview_hd;

	private RadioGroup flashSwitch;
	private RadioButton flash_on;
	private RadioButton flash_off;

    private RadioGroup oneshotSwitch;
    private RadioButton oneshot_on;
    private RadioButton oneshot_off;

	private String defaultSize;
	private boolean light,oneshot;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(DEBUG_TAG,"onCreate()");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
        setResult(Activity.RESULT_CANCELED);
       
        ((Button) findViewById(R.id.button_license_activate)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.button_decode_test)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.button_get_lib_ver)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.button_check_license)).setOnClickListener(btnClick);
        ((Button) findViewById(R.id.button_get_feature)).setOnClickListener(btnClick);

		rb_preview_vga = (RadioButton)findViewById(R.id.preview_vga);
		rb_preview_hd = (RadioButton)findViewById(R.id.preview_hd);
		priview_rg = (RadioGroup)findViewById(R.id.mode_rg_size);

		flash_off = (RadioButton)findViewById(R.id.flash_off);
		flash_on = (RadioButton)findViewById(R.id.flash_on);
		flashSwitch = (RadioGroup)findViewById(R.id.flashSwitch);

        oneshot_off = (RadioButton)findViewById(R.id.oneshot_off);
        oneshot_on = (RadioButton)findViewById(R.id.oneshot_on);
        oneshotSwitch = (RadioGroup)findViewById(R.id.oneshotSwitch);

        oneshotSwitch.setOnCheckedChangeListener(ls);
		priview_rg.setOnCheckedChangeListener(ls);
		flashSwitch.setOnCheckedChangeListener(ls);

        m_editRecvData=(EditText)findViewById(R.id.activity_main_editRecvData);
        mMainMessageHandler = new MessageHandler(Looper.myLooper());
        
        Log.d(DEBUG_TAG,"apk version="+VER);
        showLogMessage("version="+VER);
        showLogMessage("备注1：请打开WIFI  (a.激活license需开wifi；b.调用decoder_iOpen接口需开wifi，该接口返回后可关闭wifi).");
        showLogMessage("备注2：存放license的目录必须保证权限是应用可读可写, 默认存放在SD卡根目录的hdcrt.lic文件里, 用户可以改变存放路径.");
    }

	private RadioGroup.OnCheckedChangeListener ls = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

            if (checkedId == oneshot_on.getId()) {
                oneshot = true;
            }
            if (checkedId == oneshot_off.getId()) {
                oneshot = false;
            }

			if (checkedId == flash_on.getId()) {
				light = true;
			}
			if (checkedId == flash_off.getId()) {
				light = false;
			}

			if (checkedId == rb_preview_vga.getId()){
				defaultSize = "640x480";
			}
			if (checkedId == rb_preview_hd.getId()){
				defaultSize = "1280x720";
			}

		}
	};
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

        switch (requestCode) {
        case CaptureActivity.REQUEST_CODE_SCAN_DECODE:
        	Log.e(DEBUG_TAG,"return from CaptureActivity");
            if (resultCode == Activity.RESULT_OK) {
            	Log.e(DEBUG_TAG,"result OK");
            }else if(resultCode == Activity.RESULT_CANCELED){
            	Log.e(DEBUG_TAG,"result CANCELED");
            }
            
            //释放解码库资源
			Decoder_jni.getInstance().UnInitSceneChangeDetection();
            int ret = 0;
    		ret = Decoder_jni.getInstance().decoder_iClose(0);
    		if(ret!=0){
    			Log.e(DEBUG_TAG,"decoder_iClose failed,ret="+ret);
    		}else{
    			Log.d(DEBUG_TAG,"decoder_iClose successful");
    		}
            break;
		}
	}
	
	
	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_license_activate: {
				Log.d(DEBUG_TAG,"apk version="+VER);
				Intent intent = new Intent(MainActivity.this, LicenseActivity.class);
				MainActivity.this.startActivityForResult(intent, LicenseActivity.REQUEST_CODE_ACTIVATE_LICENSE);
				break;
			}
			case R.id.button_decode_test: {
				Log.d(DEBUG_TAG,"apk version="+VER);
				/*1. init decode lib*/
				int ret = 0;
				ret = initDecoderLib();
				if(ret!=0){
					Log.e(DEBUG_TAG,"initDecoderLib failed,ret="+ret);
					break;
				}else{
					Log.d(DEBUG_TAG,"decoder_iOpen successful");
				}
				//初始化移动检测参数
				Decoder_jni.getInstance().InitSceneChangeDetection(10,0.3f);

//				ret = Decoder_jni.getInstance().decoder_iClose(1);
//				if(ret!=0){
//					Log.e(DEBUG_TAG,"set qr enable failed,ret="+ret);
//				}else{
//					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
//				}

				//设置条码
				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_QR, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set qr enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}
				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_C128, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_M_PDF, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_PDF417, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_DATAMATRIX, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_AZTEC, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_CBAR, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_C93, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_HX, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_MAXICODE, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_M_QR, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_RSS_14, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_RSS_14_LIM, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_RSS_14_ST, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}
				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_RSS_EXP, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_RSS_EXP_ST, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}

				ret = Decoder_jni.getInstance().decoder_iSetParameter(CodeType.SET_CLASS_ENABLE, CodeType.SET_CODETYPE_C39, CodeType.SET_VAL_ENABLE);
				if(ret!=0){
					Log.e(DEBUG_TAG,"set c128 enable failed,ret="+ret);
				}else{
					Log.d(DEBUG_TAG,"decoder_iSetParameter successful");
				}
				/*2. start camere preview to decode*/
				Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
				if(priview_rg.getCheckedRadioButtonId()==rb_preview_vga.getId()){
					defaultSize = "640x480";
				}else{
					defaultSize = "1280x720";
				}

				if(flashSwitch.getCheckedRadioButtonId()==flash_on.getId()){
					light=true;
				}else{
					light=false;
				}

                if(oneshotSwitch.getCheckedRadioButtonId()==oneshot_on.getId()){
                    oneshot=true;
                }else{
                    oneshot=false;
                }

				intent.putExtra("scan_mode",defaultSize);
				intent.putExtra("flash_mode",light);
                intent.putExtra("oneshot_mode",oneshot);
				MainActivity.this.startActivityForResult(intent, CaptureActivity.REQUEST_CODE_SCAN_DECODE);
				break;
			}
			case R.id.button_get_lib_ver: {
				Log.d(DEBUG_TAG,"apk version="+VER);
				
				/*1. init decode lib*/
				int ret = 0;
				ret = initDecoderLib();
				if(ret!=0){
					Log.e(DEBUG_TAG,"initDecoderLib failed,ret="+ret);
					break;
				}else{
					Log.d(DEBUG_TAG,"decoder_iOpen successful");
				}
				
				/*2. get version*/
				String ver = Decoder_jni.getInstance().decoder_iGetDecoderVer();
				Log.e(DEBUG_TAG,"DecoderVer="+ver);
				showLogMessage("DecoderVer="+ver);
				break;
			}
			case R.id.button_check_license: {
				Log.d(DEBUG_TAG,"apk version="+VER);

				int ret = 0;
				ret = initDecoderLib();
				if(ret!=0){
					Log.e(DEBUG_TAG,"license is not valid");
					showLogMessage("license is not valid");
					break;
				}
				
				Log.e(DEBUG_TAG,"license is valid");
				showLogMessage("license is valid");
				break;
			}
			case R.id.button_get_feature: {
				Log.d(DEBUG_TAG,"get_feature");
				Intent intent = new Intent(MainActivity.this, GetFeatureActivity.class);
				MainActivity.this.startActivityForResult(intent, GetFeatureActivity.REQUEST_CODE_ACTIVATE_LICENSE);
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
	
	private String get_Arm64lib_path(){
		//64bit库的路径在：/data/app/com.example.decodeTest-v8miN_aYPWD7LdbnDNdYpA==/lib/arm64/libHDSD.so
		
		//1.获取apk路径：/data/app/com.example.decodeTest-v8miN_aYPWbnDNdYpA==/base.apk
		String PackageCodePath= getApplicationContext().getPackageCodePath();
		Log.d(DEBUG_TAG,"PackageCodePath="+ PackageCodePath);
		
		//2.去除base.apk后缀取得/data/app/com.example.decodeTest-v8miN_aYPWbnDNdYpA==/
		String s1 = "base.apk";
		String s2=PackageCodePath.substring(0,PackageCodePath.length()-s1.length());
		Log.d(DEBUG_TAG,"s2="+ s2);
		
		//3.拼接lib/arm64/libHDSD.so得到完整Lib路径
		String libPath=s2+"lib/arm64/libHDSD.so";
		Log.d(DEBUG_TAG,"libPath="+ libPath);
		
		return libPath;
	}
	
	
	private int initDecoderLib(){
		Log.d(DEBUG_TAG,"PackageCodePath="+ getApplicationContext().getPackageCodePath());
		Log.d(DEBUG_TAG,"CacheDir="+ getApplicationContext().getCacheDir());
		Log.d(DEBUG_TAG,"FilesDir="+ getApplicationContext().getFilesDir());
		Log.d(DEBUG_TAG,"PackageResourcePath="+ getApplicationContext().getPackageResourcePath());
		
		//库路径
		String libPath = "/data/data/com.example.decodeTest/lib";//32位库 路径
//		String libPath = get_Arm64lib_path();//64位库 路径
		
		//license路径
		//String licensePath = "/data/hdcrt.lic";
		String licensePath = SD_PATH + "/hdcrt.lic";
		
		int ret = 0;
		ret = Decoder_jni.getInstance().decoder_iOpen(getApplicationContext(), libPath, licensePath);
		if(ret!=0){
			Log.e(DEBUG_TAG,"decoder_iOpen failed,ret="+ret);
			showLogMessage("decoder_iOpen failed,ret="+ret);
			if((ret>=-108)&&(ret<=-101)){
				Log.e(DEBUG_TAG,"maybe you not do activate license");
				showLogMessage("maybe you not do activate license");
			}
			return ret;
		}else{
			Log.d(DEBUG_TAG,"decoder_iOpen successful");
		}
		return ret;
	}

}
