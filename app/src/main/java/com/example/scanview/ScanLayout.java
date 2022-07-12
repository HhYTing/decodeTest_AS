package com.example.scanview;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;



public class ScanLayout extends RelativeLayout {
	private final static String TAG = "decodeTest_ScanLayout";
	private boolean bNoPreview = true;
	private boolean bUseGifPicture = true;
	private Paint paint;
//	private Rect mRect;
	private ImageView mScanView;
	private LayoutParams mParam;
	private int mScreenWidth = 0;
	private int mScreenHeight = 0;
//	//private ScaleAnimation animation = null;
	private Context mContext;
//	private int cropWidth,cropHeight;
/*	private void initCropRect(){
		//String PreviewCutSize = PreferencesManager.getPreviewCutSize();		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		String PreviewCutSize = prefs.getString(PreferencesManager.KEY_PREVIEW_CUT_SIZE, null);
		String size[] = PreviewCutSize.split("x");
		cropWidth = Integer.parseInt(size[0]);
		cropHeight = Integer.parseInt(size[1]);
	}
*/
//	public ImageView getScanView() {
//		return mScanView;
//	}
//	
//	public Rect getPreviewRect(){
//		return mRect;
//	}
	
	//public ScaleAnimation getAnimation() {
	//	return animation;
	//}

	/**
	 * 璁剧疆鎵弿鍖哄�??
	 * 
	 * @param rect
	 *            鎵潰鍖哄煙鍧愭�??榛樿灞忓箷灞呬�??
	 */
//	public void setRect(Rect rect) {
//		mRect = rect;
//		mParam.width = rect.right - rect.left;
//		mParam.height = rect.bottom - rect.top;
//		mParam.leftMargin = rect.left;
//		mParam.topMargin = rect.top;
//		mScanView.setLayoutParams(mParam);
//		postInvalidate();
//	}

	/**
	 * 璁剧疆鎵弿鍖哄煙澶у�??
	 * 
	 * @param width
	 *            鎵弿鍖哄煙瀹藉�??
	 * @param height
	 *            鎵弿鍖哄煙楂樺�??
	 */
//	public void setScanRect(int width, int height) {
//		
//		int leftOffset = (mScreenWidth - width) / 2;
//		int topOffset = (mScreenHeight - height) / 2;
//		mRect.left = leftOffset;
//		mRect.top = topOffset;
//		mRect.right = leftOffset + width;
//		mRect.bottom = topOffset + height;
//		
//		mParam.leftMargin = leftOffset;
//		mParam.topMargin = topOffset;
//		mParam.width = width;
//		mParam.height = height;
//		mScanView.setLayoutParams(mParam);
//		postInvalidate();
//	}

	public ScanLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		this.setWillNotDraw(false);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		mScreenWidth = wm.getDefaultDisplay().getWidth();
		mScreenHeight = wm.getDefaultDisplay().getHeight();
		//initCropRect();
		initScanView(context, attrs);
		
	}
	
//	public void startScanViewAnimation(){
//		/*
//		 * 缂╂斁鍔ㄧ敾鏁堟�??
//		 * ScaleAnimation(float fromX, float toX, float fromY, float toY);
//銆��??		 * 绗竴涓弬鏁癴romX涓哄姩鐢昏捣濮嬫�??X鍧愭爣涓婄殑浼哥缉灏哄  0.0琛ㄧず鏀剁缉鍒版病鏈�??//銆��??	 * 绗簩涓弬鏁皌oX涓哄姩鐢荤粨鏉熸�??X鍧愭爣涓婄殑浼哥缉灏哄   1.0琛ㄧず姝ｅ父鏃犱几缂�??//銆��??	 * 绗笁涓弬鏁癴romY涓哄姩鐢昏捣濮嬫椂Y鍧愭爣涓婄殑浼哥缉灏哄  鍊煎皬浜�??0琛ㄧず鏀剁缉
//銆��??	 * 绗洓涓弬鏁皌oY涓哄姩鐢荤粨鏉熸椂Y鍧愭爣涓婄殑浼哥缉灏哄   鍊煎ぇ浜�??0琛ㄧず鏀惧ぇ
//		 * */
//		ScaleAnimation animation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f);
//		animation.setRepeatCount(-1);
//		animation.setRepeatMode(Animation.RESTART);
//		animation.setInterpolator(new LinearInterpolator());
//		animation.setDuration(1200);
//		mScanView.startAnimation(animation);
//	}
	
	/**
	 * 鍒濆鍖栨壂鎻忓尯鍩�??
	 * @param context
	 * @param attrs
	 */
	private void initScanView(Context context, AttributeSet attrs) {
		
	//		mScanView = new ImageView(context);
	//		mScanView.setBackgroundResource(R.drawable.scan_ray);
	//		mParam = new LayoutParams(cropWidth,cropHeight);
	//		int leftOffset = (mScreenWidth - cropWidth) / 2;
	//		int topOffset = (mScreenHeight - cropHeight) / 2;
	//		mParam.leftMargin = leftOffset;
	//		mParam.topMargin = topOffset;
	//		mRect = new Rect(leftOffset, topOffset, leftOffset + cropWidth,topOffset + cropHeight);
	//		LogUtils.i(TAG,"Rect area(" + mRect.left + "," + mRect.top + "," + mRect.right + "," + mRect.bottom);
	//		addView(mScanView, mParam);
		
			//鍚姩鍔ㄧ敾鏁堟�??
			//startScanViewAnimation();
		
		
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stubf

//		if (mRect != null) {
//			int width = canvas.getWidth();
//			int height = canvas.getHeight();
//			int color = getResources().getColor(R.color.viewfinder_mask);
//			paint.setColor(color);

			/** top area */
//			canvas.drawRect(0, 0, width, mRect.top, paint);
			/** left area */
//			canvas.drawRect(0, mRect.top, mRect.left, mRect.bottom, paint);
			/** right area */
//			canvas.drawRect(mRect.right, mRect.top, width, mRect.bottom, paint);
			/** bottom area */
//			canvas.drawRect(0, mRect.bottom, width, height, paint);
//		}
//		super.onDraw(canvas);

//	}

}
