/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Map;

import android.util.Log;

/**
 * Common string-related functions.
 *
 * @author Sean Owen
 * @author Alex Dupre
 */
public final class StringUtils {
  private final static String TAG = "decodeTest_StringUtils";
  private static final String PLATFORM_DEFAULT_ENCODING = Charset.defaultCharset().name();
  private static final String SHIFT_JIS = "SHIFT_JIS";//"SJIS";
  private static final String GB18030 = "GB18030";//GB2312 = "GB2312";//@landicorp: zhangml chg
  private static final String EUC_JP = "EUC-JP";//"EUC_JP";
  private static final String UTF8 = "UTF-8";
  private static final String ISO88591 = "ISO-8859-1";//"ISO8859_1";
  private static final boolean ASSUME_SHIFT_JIS =
      SHIFT_JIS.equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING) ||
      EUC_JP.equalsIgnoreCase(PLATFORM_DEFAULT_ENCODING);

  private StringUtils() {}

  /**
   * @param bytes bytes encoding a string, whose encoding should be guessed
   * @param hints decode hints if applicable
   * @return name of guessed encoding; at the moment will only guess one of:
   *  {@link #SHIFT_JIS}, {@link #UTF8}, {@link #ISO88591}, or the platform
   *  default encoding if none of these can possibly be correct
   */
  public static String guessEncoding(byte[] bytes, Map<DecodeHintType,?> hints) {
    if (hints != null) {
      String characterSet = (String) hints.get(DecodeHintType.CHARACTER_SET);
      if (characterSet != null) {
        return characterSet;
      }
    }
    // For now, merely tries to distinguish ISO-8859-1, UTF-8 and Shift_JIS,
    // which should be by far the most common encodings.
    int length = bytes.length;
    boolean canBeISO88591 = true;
    boolean canBeShiftJIS = true;
    boolean canBeUTF8 = true;
    //@landicorp: zhangml add
    boolean canBeGB18030 = true;
    int gb18030Bytes = 0;
    //zhangml add end
    int utf8BytesLeft = 0;
    //int utf8LowChars = 0;
    int utf2BytesChars = 0;
    int utf3BytesChars = 0;
    int utf4BytesChars = 0;
    int sjisBytesLeft = 0;
    //int sjisLowChars = 0;
    int sjisKatakanaChars = 0;
    //int sjisDoubleBytesChars = 0;
    int sjisCurKatakanaWordLength = 0;
    int sjisCurDoubleBytesWordLength = 0;
    int sjisMaxKatakanaWordLength = 0;
    int sjisMaxDoubleBytesWordLength = 0;
    //int isoLowChars = 0;
    //int isoHighChars = 0;
    int isoHighOther = 0;

    boolean utf8bom = bytes.length > 3 &&
        bytes[0] == (byte) 0xEF &&
        bytes[1] == (byte) 0xBB &&
        bytes[2] == (byte) 0xBF;

    for (int i = 0;
         i < length && (canBeISO88591 || canBeShiftJIS || canBeUTF8 || canBeGB18030);//@landicorp: zhangml add
         i++) {

      int value = bytes[i] & 0xFF;

      // UTF-8 stuff
      if (canBeUTF8) {
        if (utf8BytesLeft > 0) {
          if ((value & 0x80) == 0) {
            canBeUTF8 = false;
          } else {
            utf8BytesLeft--;
          }
        } else if ((value & 0x80) != 0) {
          if ((value & 0x40) == 0) {
            canBeUTF8 = false;
          } else {
            utf8BytesLeft++;
            if ((value & 0x20) == 0) {
              utf2BytesChars++;
            } else {
              utf8BytesLeft++;
              if ((value & 0x10) == 0) {
                utf3BytesChars++;
              } else {
                utf8BytesLeft++;
                if ((value & 0x08) == 0) {
                  utf4BytesChars++;
                } else {
                  canBeUTF8 = false;
                }
              }
            }
          }
        } //else {
          //utf8LowChars++;
        //}
      }
      Log.i(TAG,Util.getMethodLine()+"canBeUTF8="+canBeUTF8);

      // ISO-8859-1 stuff
      if (canBeISO88591) {
        if (value > 0x7F && value < 0xA0) {
          canBeISO88591 = false;
        } else if (value > 0x9F) {
          if (value < 0xC0 || value == 0xD7 || value == 0xF7) {
            isoHighOther++;
          } //else {
            //isoHighChars++;
          //}
        } //else {
          //isoLowChars++;
        //}
      }

      //@landicorp: zhangml add begin
      if (canBeGB18030) {
          if(gb18030Bytes > 0){
              switch(gb18030Bytes){
                  case 2:
                      // 2 byte or 4 byte
                      if((value >= 0x40 && value <= 0x7e) || (value >= 0x80 && value <= 0xfe)){
                          // Is 2 byte, continue
                          gb18030Bytes = 0;
                      }else if(value >= 0x30 && value <= 0x39){
                          // maybe 4 byte, read the third byte first
                          gb18030Bytes = 3;
                      }else{
                          canBeGB18030 = false;
                      }
                      break;
                  case 3:
                      // 4 byte, this is the third byte
                      if(value >= 0x81 && value <= 0xfe3){
                          // maybe 4 byte, read the final byte
                          gb18030Bytes = 4;
                      }else{
                          canBeGB18030 = false;
                      }
                      break;
                  case 4:
                      // 4 byte, final byte
                      if(value >= 0x30 && value <= 0x39){
                          // Is 4 byte, continue
                          gb18030Bytes = 0;
                      }else{
                          canBeGB18030 = false;
                      }
                      break;
                  default:
                      canBeGB18030 = false;
                      break;
              }
          }else{
              if(value >= 0x00 && value <= 0x7f){
                  // Is 1 byte, continue
              }else if(value >= 0x81 && value <= 0xfe){
                  // maybe 2 byte or 4 byte, read next byte
                  gb18030Bytes = 2;
              }else{
                  canBeGB18030 = false;
              }
          }
      }
      //zhangml add end
      Log.i(TAG,Util.getMethodLine()+"canBeGB18030="+canBeGB18030);

      // Shift_JIS stuff
      //@landicorp: zhangml chg
      if(canBeShiftJIS){
          if(sjisBytesLeft > 0){
              if((0x40 <= value && value <= 0x7E || 0x80 <= value && value <= 0xFC)){
                  // maybe 2 byte, check the code
                  int code = (((int)(bytes[i-1] & 0xFF)) << 8) | (int)(bytes[i] & 0xFF);
                  if(0x81AD<=code && code<=0x81B7
                          || 0x81C0<=code && code<=0x81C7
                          || 0x81CF<=code && code<=0x81D9
                          || 0x81E9<=code && code<=0x81EF
                          || 0x81F8<=code && code<=0x81FC
                          || 0x8240<=code && code<=0x824e
                          || 0x8259<=code && code<=0x825f
                          || 0x827A<=code && code<=0x8280
                          || 0x829B<=code && code<=0x829E
                          || 0x82F2<=code && code<=0x82FC
                          || 0x8397<=code && code<=0x839E
                          || 0x83B7<=code && code<=0x83BE
                          || 0x83D7<=code && code<=0x83FC
                          || 0x8461<=code && code<=0x846F
                          || 0x8492<=code && code<=0x849E
                          || 0x84BF<=code && code<=0x84FC
                          || 0x8540<=code && code<=0x889E
                          || 0x9873<=code && code<=0x989E
                          || 0xEBA5<=code && code<=0xEBFC
                          || 0xEB40<=code && code<=0xEFFC){
                      canBeShiftJIS = false;
                  }else{
                      // Is 2 byte, continue
                      sjisBytesLeft = 0;

                      sjisCurKatakanaWordLength = 0;
                      sjisCurDoubleBytesWordLength++;
                      if (sjisCurDoubleBytesWordLength > sjisMaxDoubleBytesWordLength) {
                        sjisMaxDoubleBytesWordLength = sjisCurDoubleBytesWordLength;
                      }
                  }
              }else{
                  canBeShiftJIS = false;
              }
          }else{
              if(((0x00 <= value && value <= 0x1F) || value == 0x7F)// control characters
                      || (0x20 <= value && value <= 0x7E)// ASCII characters
                      || (0xA1 <= value && value <= 0xDF)// Katakana characters
                      ){
                  // Is 1 byte, continue
                  if(0xA1 <= value && value <= 0xDF){
                      sjisKatakanaChars++;
                      sjisCurDoubleBytesWordLength = 0;
                      sjisCurKatakanaWordLength++;
                      if (sjisCurKatakanaWordLength > sjisMaxKatakanaWordLength) {
                        sjisMaxKatakanaWordLength = sjisCurKatakanaWordLength;
                      }
                  }
              }else if((0x81 <= value && value <= 0x9F)
                      || (0xE0 <= value && value <= 0xEF)){
                  // 2 byte, read the next byte
                  sjisBytesLeft = 1;
              }else{
                  canBeShiftJIS = false;
              }
          }
      }
      Log.i(TAG,Util.getMethodLine()+"canBeShiftJIS="+canBeShiftJIS);
      ///*
      if (canBeShiftJIS) {
        if (sjisBytesLeft > 0) {
          if (value < 0x40 || value == 0x7F || value > 0xFC) {
            canBeShiftJIS = false;
          } else {
            sjisBytesLeft--;
          }
        } else if (value == 0x80 || value == 0xA0 || value > 0xEF) {
          canBeShiftJIS = false;
        } else if (value > 0xA0 && value < 0xE0) {
          sjisKatakanaChars++;
          sjisCurDoubleBytesWordLength = 0;
          sjisCurKatakanaWordLength++;
          if (sjisCurKatakanaWordLength > sjisMaxKatakanaWordLength) {
            sjisMaxKatakanaWordLength = sjisCurKatakanaWordLength;
          }
        } else if (value > 0x7F) {
          sjisBytesLeft++;
          //sjisDoubleBytesChars++;
          sjisCurKatakanaWordLength = 0;
          sjisCurDoubleBytesWordLength++;
          if (sjisCurDoubleBytesWordLength > sjisMaxDoubleBytesWordLength) {
            sjisMaxDoubleBytesWordLength = sjisCurDoubleBytesWordLength;
          }
        } else {
          //sjisLowChars++;
          sjisCurKatakanaWordLength = 0;
          sjisCurDoubleBytesWordLength = 0;
        }
      }
      //*/
      //zhangml chg end
    }

    if (canBeUTF8 && utf8BytesLeft > 0) {
    	Log.i(TAG,Util.getMethodLine()+"canBeUTF8 && utf8BytesLeft > 0");
      canBeUTF8 = false;
    }
    if (canBeShiftJIS && sjisBytesLeft > 0) {
    	Log.i(TAG,Util.getMethodLine()+"canBeShiftJIS && sjisBytesLeft > 0");
      canBeShiftJIS = false;
    }
    //@landicorp: zhangml add
    if (canBeGB18030 && gb18030Bytes > 0){
    	Log.i(TAG,Util.getMethodLine()+"canBeGB18030 && gb18030Bytes > 0");
        canBeGB18030 = false;
    }
    //zhangml end

    // Easy -- if there is BOM or at least 1 valid not-single byte character (and no evidence it can't be UTF-8), done
    if (canBeUTF8 && (utf8bom || utf2BytesChars + utf3BytesChars + utf4BytesChars > 0)) {
      return UTF8;
    }

    //@landicorp: zhangml add
    if (canBeGB18030) {
        if(canBeShiftJIS){
        	if(sjisMaxKatakanaWordLength > sjisMaxDoubleBytesWordLength){
        	}else{
        	}
            return (sjisMaxKatakanaWordLength > sjisMaxDoubleBytesWordLength) ? GB18030 : SHIFT_JIS;
        }
        return GB18030;
    }
    //zhangml add end
    
    // Easy -- if assuming Shift_JIS or at least 3 valid consecutive not-ascii characters (and no evidence it can't be), done
    if (canBeShiftJIS && (ASSUME_SHIFT_JIS || sjisMaxKatakanaWordLength >= 3 || sjisMaxDoubleBytesWordLength >= 3)) {
      return SHIFT_JIS;
    }
    
    // Distinguishing Shift_JIS and ISO-8859-1 can be a little tough for short words. The crude heuristic is:
    // - If we saw
    //   - only two consecutive katakana chars in the whole text, or
    //   - at least 10% of bytes that could be "upper" not-alphanumeric Latin1,
    // - then we conclude Shift_JIS, else ISO-8859-1
    if (canBeISO88591 && canBeShiftJIS) {
      return (sjisMaxKatakanaWordLength == 2 && sjisKatakanaChars == 2) || isoHighOther * 10 >= length
          ? SHIFT_JIS : ISO88591;
    }

    // Otherwise, try in order ISO-8859-1, Shift JIS, UTF-8 and fall back to default platform encoding
    if (canBeISO88591) {
      return ISO88591;
    }
    if (canBeShiftJIS) {
      return SHIFT_JIS;
    }
    if (canBeUTF8) {
      return UTF8;
    }
    // Otherwise, we take a wild guess with platform encoding
    return PLATFORM_DEFAULT_ENCODING;
  }
  
  /*****************************************************************************
   ***使用juniversalchardet-1.0.3.jar判断---***数据量很少时，判断GB18030不是很准确************
   ***************************begin************************************************/
  /*
  public static String getEncode(File file) {
	  
      if (!file.exists()) {
          Log.i(TAG,Util.getMethodLine()+"getFileIncode: file not exists!");
          return null;
      }

      byte[] buf = new byte[4096];
      FileInputStream fis = null;
      try {
          fis = new FileInputStream(file);
          // (1)
          UniversalDetector detector = new UniversalDetector(null);

          // (2)
          int nread;
          while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
              detector.handleData(buf, 0, nread);
          }
          // (3)
          detector.dataEnd();

          // (4)
          String encoding = detector.getDetectedCharset();
          if (encoding != null) {
              Log.i(TAG,Util.getMethodLine()+"Detected encoding = " + encoding);
          } else {
              Log.i(TAG,Util.getMethodLine()+"No encoding detected.");
          }

          // (5)
          detector.reset();
          fis.close();
          return encoding;
      } catch (Exception e) {
          e.printStackTrace();
      }

      return null;
  }
  
public static String getEncode(byte[] buf) {
	  Log.i(TAG,Util.getMethodLine()+"getEncode begin");
      try {
          // (1)
          UniversalDetector detector = new UniversalDetector(null);

          // (2)
          //while (!detector.isDone()) {
              detector.handleData(buf, 0, buf.length);
         // }
          // (3)
          detector.dataEnd();

          // (4)
          String encoding = detector.getDetectedCharset();
          if (encoding != null) {
              Log.i(TAG,Util.getMethodLine()+"Detected encoding = " + encoding);
          } else {
              Log.i(TAG,Util.getMethodLine()+"No encoding detected.");
          }

          // (5)
          detector.reset();
          return encoding;
      } catch (Exception e) {
          e.printStackTrace();
      }
      Log.i(TAG,Util.getMethodLine()+"getEncode end");
      return null;
  }
 */
/*****************************************************************************
 ***使用juniversalchardet-1.0.3.jar判断---***数据量很少时，判断GB18030不是很准确************
 ***************************end************************************************/

}
