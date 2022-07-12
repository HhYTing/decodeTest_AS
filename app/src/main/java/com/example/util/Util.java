/* NFCard is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFCard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.example.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;

import com.example.util.LogUtils;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public final class Util {
	private static final String DEBUG_TAG = "decodeTest_Util";
	
	private final static char[] HEX = { '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	private Util() {
	}

	public static boolean isNotBlank(String s) {
		return s != null && s.trim().length() > 0;
	}

	public static byte[] toBytes(int a) {
		return new byte[] { (byte) (0x000000ff & (a >>> 24)),
				(byte) (0x000000ff & (a >>> 16)),
				(byte) (0x000000ff & (a >>> 8)), (byte) (0x000000ff & (a)) };
	}

	public static boolean testBit(byte data, int bit) {
		final byte mask = (byte) ((1 << bit) & 0x000000FF);

		return (data & mask) == mask;
	}

	public static int toInt(byte[] b, int start, int sizeofint) {
		int ret = 0;

		final int e = start + sizeofint;
		for (int i = start; i < e; ++i) {
			ret <<= 8;
			ret |= b[i] & 0xFF;
		}
		return ret;
	}

	public static int toIntR(byte[] b, int s, int n) {
		int ret = 0;

		for (int i = s; (i >= 0 && n > 0); --i, --n) {
			ret <<= 8;
			ret |= b[i] & 0xFF;
		}
		return ret;
	}

	public static int toInt(byte... b) {
		int ret = 0;
		for (final byte a : b) {
			ret <<= 8;
			ret |= a & 0xFF;
		}
		return ret;
	}

	public static int toIntR(byte... b) {
		return toIntR(b, b.length - 1, b.length);
	}

	public static String toGBKString(byte[] data, int start, int length) {
		try {
			return new String(data, start, length, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String toBCDString(byte[] data) {
		return toBCDString(data, 0, data.length);
	}
	
	public static byte[] StringtoBCD(String str) {
		str.length();
		if(str.contains(".")){
			int dotIndex = str.indexOf('.');
			String newString = str.substring(0, dotIndex) + str.substring(dotIndex+1, str.length());
			byte[] data1 = newString.getBytes();
			//LogUtils.d(DEBUG_TAG,"byte[] data1="+Util.byte2string(data1,true));
			int len = 0;
			if(data1.length%2==0){
				len = data1.length/2;
			}else{
				len = data1.length/2+1;
			}
			byte[] data = new byte[len];
			for(int i=0;i<len;i++){
				if(data1.length%2==0){
					data[i] = (byte) (((data1[i*2]-0x30)<<4)|(data1[i*2+1]-0x30));
				}else{
					if(i==0){
						data[i] = (byte) (data1[i*2]-0x30);
					}else{
						data[i] = (byte) (((data1[i*2-1]-0x30)<<4)|(data1[i*2]-0x30));
					}
				}
			}
			//LogUtils.d(DEBUG_TAG,"byte[] data="+Util.byte2string(data,true));
			return data;
		}else{
			byte[] data1 = str.getBytes();
			//LogUtils.d(DEBUG_TAG,"byte[] data1="+Util.byte2string(data1,true));
			int len = 0;
			if(data1.length%2==0){
				len = data1.length/2;
			}else{
				len = data1.length/2+1;
			}
			byte[] data = new byte[len];
			for(int i=0;i<len;i++){
				if(data1.length%2==0){
					data[i] = (byte) (((data1[i*2]-0x30)<<4)|(data1[i*2+1]-0x30));
				}else{
					if(i==0){
						data[i] = (byte) (data1[i*2]-0x30);
					}else{
						data[i] = (byte) (((data1[i*2-1]-0x30)<<4)|(data1[i*2]-0x30));
					}
				}
			}
			//LogUtils.d(DEBUG_TAG,"byte[] data="+Util.byte2string(data,true));
			return data;
		}
		
	}

	public static String toBCDString(byte[] data, int start, int length) {
		StringBuffer buf = new StringBuffer();
		for (int i = start; i < start + length; i++) {
			byte b = data[i];
			char c1 = (char) (((b >> 4) & 0x0f) + 0x30);
			char c2 = (char) ((b & 0x0f) + 0x30);
			buf.append(c1);
			buf.append(c2);
		}
		return buf.toString();
	}

	public static String toGBKString(byte[] data) {
		try {
			return new String(data, 0, data.length, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String toHexString(byte... d) {
		return (d == null || d.length == 0) ? "" : toHexString(d, 0, d.length);
	}

	public static String toHexString(byte[] d, int s, int n) {
		final char[] ret = new char[n * 2];
		final int e = s + n;

		int x = 0;
		for (int i = s; i < e; ++i) {
			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}
		return new String(ret);
	}

	public static String toHexStringR(byte[] d, int s, int n) {
		final char[] ret = new char[n * 2];

		int x = 0;
		for (int i = s + n - 1; i >= s; --i) {
			final byte v = d[i];
			ret[x++] = HEX[0x0F & (v >> 4)];
			ret[x++] = HEX[0x0F & v];
		}
		return new String(ret);
	}

	public static String ensureString(String str) {
		return str == null ? "" : str;
	}

	public static String toStringR(int n) {
		final StringBuilder ret = new StringBuilder(16).append('0');

		long N = 0xFFFFFFFFL & n;
		while (N != 0) {
			ret.append((int) (N % 100));
			N /= 100;
		}

		return ret.toString();
	}

	public static int parseInt(String txt, int radix, int def) {
		int ret;
		try {
			ret = Integer.valueOf(txt, radix);
		} catch (Exception e) {
			ret = def;
		}

		return ret;
	}

	public static int BCDtoInt(byte[] b, int s, int n) {
		int ret = 0;

		final int e = s + n;
		for (int i = s; i < e; ++i) {
			int h = (b[i] >> 4) & 0x0F;
			int l = b[i] & 0x0F;

			if (h > 9 || l > 9)
				return -1;

			ret = ret * 100 + h * 10 + l;
		}

		return ret;
	}

	public static int BCDtoInt(byte... b) {
		return BCDtoInt(b, 0, b.length);
	}
	
	public static String byte2string(byte[] data,Boolean hexFormat){
		StringBuilder sb = new StringBuilder();
		String str = "";
		
		if(data==null){
			return str;
		}
		//long startTime  = System.currentTimeMillis();	
		if (hexFormat) {
        	for (int i = 0; i < data.length; ++i){
        		sb.append(Integer.toHexString(data[i] & 0xFF) + " ");
        		//变长度字符串不能使用string,它每次都�?��生成新对象，性能很差
        		//readMessage += Integer.toHexString(data[j] & 0xFF) + " ";
        	}
        	str = "0x"+ new String(sb.toString());
        } else{
        	str = new String(data, 0, data.length);
        }
		//long endTime  = System.currentTimeMillis();	
		//LogUtils.d(DEBUG_TAG, "byte2string--consume time:"+(endTime-startTime));
		
		sb = null;
		return str;
	}
	
	public static String getFileMethodLine() { 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
		StringBuffer toStringBuffer = new StringBuffer("[").append( 
							traceElement.getFileName()).append("|").append( 
							traceElement.getMethodName()).append("|").append( 
							traceElement.getLineNumber()).append("] "); 
		return toStringBuffer.toString(); 
	} 
	
	public static String getMethodLine() { 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
		StringBuffer toStringBuffer = new StringBuffer("[").append( 
							traceElement.getMethodName()).append("|").append( 
							traceElement.getLineNumber()).append("] "); 
		return toStringBuffer.toString(); 
	} 

	// ��ǰ�ļ��� 
	public static String _FILE_() { 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
		return traceElement.getFileName(); 
	} 

	// ��ǰ������ 
	public static String _FUNC_() { 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
		return traceElement.getMethodName(); 
	} 

	// ��ǰ�к� 
	public static int _LINE_() { 
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1]; 
		return traceElement.getLineNumber(); 
	} 

	// ��ǰʱ�� 
	@SuppressLint("SimpleDateFormat")
	public static String _TIME_() { 
		Date now = new Date(System.currentTimeMillis()); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		return sdf.format(now); 
	} 
	
	public static String getSDPath(){
		  File sdDir = null;
		  boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED); //�ж�sd���Ƿ����
		  if (sdCardExist)
		  {
			  sdDir = Environment.getExternalStorageDirectory();//��ȡ��Ŀ¼
			  LogUtils.e(DEBUG_TAG,"sdCard path="+sdDir.toString());
			  LogUtils.e(DEBUG_TAG,"DataDirectory path="+Environment.getDataDirectory().toString());
			  LogUtils.e(DEBUG_TAG,"RootDirectory path="+Environment.getRootDirectory().toString());
			  LogUtils.e(DEBUG_TAG,"DownloadCacheDirectory path="+Environment.getDownloadCacheDirectory().toString());
			  return sdDir.toString();
		  } else{
			  LogUtils.e(DEBUG_TAG,"sdCard is not Exist");
			  return null;
		  }
		  
		   
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public static void saveDataToFile(byte[] data, String dirName, String fileName){
		String sdPath = getSDPath();
		File fileDir = new File(sdPath + File.separator + dirName);
		File file = new File(sdPath + File.separator + dirName + File.separator + fileName);
		
		if (!fileDir.exists()) {
			LogUtils.w(DEBUG_TAG,"dir="+fileDir.getAbsolutePath()+"/ not exist,creat it");
			fileDir.mkdirs();
			fileDir.setReadable(true);
			fileDir.setWritable(true);
		}else{
			LogUtils.w(DEBUG_TAG,"dir="+fileDir.getAbsolutePath()+"/ exist");
		}
		
		if(!file.exists()){
			LogUtils.w(DEBUG_TAG,"file="+file.getAbsolutePath()+" not exist,creat it");
			try {
				file.createNewFile();
				file.setReadable(true);
				file.setWritable(true);
			} catch (IOException e) {
				e.printStackTrace();
				LogUtils.e(DEBUG_TAG,"File.createNewFile() failed!---IOException");
				return;
			}
		}else{
			LogUtils.w(DEBUG_TAG,"file="+file.getAbsolutePath()+" exist");
		}
	
		OutputStream  outStream = null;
		try {
			outStream = new FileOutputStream(file);
			outStream.write(data);
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	//去除最大最小值，再取平均值
	public static float getAverage_except_MinMax(int[] dataArray){
		if(dataArray==null)
			return -1;
		//去除最大最小值，再取平均值
		int maxValue = dataArray[0];
		int maxIndex = 0;
		int minValue = dataArray[0];
		int minIndex = 0;
		float sum=0;
		float average = 0;
		//找最大最小值
		for(int i=0;i<dataArray.length;i++){
			if(maxValue<dataArray[i]){
				maxValue = dataArray[i];
				maxIndex = i;
			}
			if(minValue>dataArray[i]){
				minValue = dataArray[i];
				minIndex = i;
			}
		}
		//求平均值
		for(int i=0;i<dataArray.length;i++){
			LogUtils.i(DEBUG_TAG, Util.getMethodLine()+"dataArray["+i+"]"+"="+dataArray[i]);
			if((i==maxIndex)||(i==minIndex)){
				LogUtils.i(DEBUG_TAG, Util.getMethodLine()+"index "+i+" is min or max, abandon it...");
				continue;
			}
			sum += dataArray[i];
		}
		if(maxIndex==minIndex){
			average = sum/(dataArray.length-1);
		}else{
			average = sum/(dataArray.length-2);
		}
		return average;
	}
	
	public static float getAverage(int[] dataArray){
		if(dataArray==null)
			return -1;
		float sum=0;
		float average = 0;
		//求平均值
		for(int i=0;i<dataArray.length;i++){
			LogUtils.i(DEBUG_TAG, Util.getMethodLine()+"dataArray["+i+"]"+"="+dataArray[i]);
			sum += dataArray[i];
		}
		average = sum/dataArray.length;
		return average;
	}
}
