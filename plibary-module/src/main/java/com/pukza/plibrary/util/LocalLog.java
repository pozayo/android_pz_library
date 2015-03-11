package com.pukza.plibrary.util;

/**
 * @desc 
 * 로컬 로그 
 * @author hwaseopchoi
 *
 */

import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.pukza.plibrary.BuildConfig;

public class LocalLog {
    private static final String LOG_SUPPORT_PREFIX = "aztsupport_";
    private static final String LOG_PREFIX = "azt_";

    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;
    
	static public final int LOG_LEVEL_RELEASE = 1;
	static public final int LOG_LEVEL_DEBUG = 2;
	static public final int LOG_LEVEL_TEST = 3;


	static public final int LOG_DISPLAY_CONSOLE = 0x01;
	static public final int LOG_DISPLAY_FILE = 0x02;
	static public final int LOG_DISPLAY_ALL = 0x03;

	static private int mLogLevel = LOG_LEVEL_DEBUG;
	static private int mLogDisplay = LOG_DISPLAY_ALL;
	static private String mLogFilePath;
	
    public static String makeLogTag(Class cls) {
//        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
//            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
//        }
//
//        return LOG_PREFIX + str;
        return cls.getSimpleName();
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeSupportLogTag(Class cls) {
        return makeLogTag(cls);
    }


	private LocalLog()
	{
//		mLogLevel = BuildConfig.DEBUG ? LOG_LEVEL_TEST : LOG_LEVEL_RELEASE;
		mLogLevel = LOG_LEVEL_DEBUG;
		mLogDisplay = LOG_DISPLAY_CONSOLE;
		mLogFilePath = null;
	}
	

	static public int GetLogLevel()
	{
		return mLogLevel;
	}

	
	static public int GetLogDisplay()
	{
		return mLogDisplay;
	}

	
	static public String GetLogFilePath()
	{
		return mLogFilePath;
	}

	static public boolean ValidateLog(int Level)
	{
		if(Level > GetLogLevel())
			return false;

		return true;
	}
	
	
	static public void SetLogConfiguration(int Level, int Display, String FilePath)
	{
//		if(Level <= LOG_LEVEL_RELEASE)
//		{
//			LOGE(LocalLog.LOG_LEVEL_RELEASE, "LocalLog", "Can't Setting LOG_LEVEL_0");
//			return;
//		}
		mLogLevel = Level;
		mLogDisplay = Display;
		mLogFilePath = FilePath;
	}

	private static void FileWriteLog(int Level, String Tag, String Msg, String LogLevel)
	{
		BufferedWriter bfw;
		try
		{
			if(mLogFilePath == null)
				return;
			bfw = new BufferedWriter(new FileWriter(mLogFilePath, true));

			SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.KOREA);
			Date currentTime = new Date();
			String dTime = formatter.format(currentTime);
			// 시간 기록
			bfw.write(dTime + ": ");
			// VERBOSE, DEBUG, ..
			bfw.write(LogLevel + "/");
			// Tag 기록
			bfw.write(Tag + "(");
			// PID 기록
			bfw.write(android.os.Process.myPid() + "): ");
			// 메시지 기록
			bfw.write(Msg);
			bfw.newLine();
			bfw.close();
		} catch(IOException e)
		{
//			 Log.e("LocalLogError", e.getMessage());
//			 e.printStackTrace();
		}

	}
	
	static public void LOGE(int Level, String Tag, String Msg)
	{
		if(!ValidateLog(Level))
			return;
		
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.e(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(Level, Tag, Msg, "ERROR");
		}
	}
	
	static public void LOGD(int Level, String Tag, String Msg)
	{
		if(!ValidateLog(Level) || !BuildConfig.DEBUG)
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.d(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(Level, Tag, Msg, "DEBUG");
		}
	}
	
	
	static public void LOGV(int Level, String Tag, String Msg)
	{
		if(!ValidateLog(Level) || !BuildConfig.DEBUG)
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.v(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(Level, Tag, Msg, "VERBOSE");
		}
	}
	
	
	static public void LOGI(int Level, String Tag, String Msg)
	{
		if(!ValidateLog(Level) || !BuildConfig.DEBUG)
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.i(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(Level, Tag, Msg, "INFO");
		}
	}
	
	
	static public void LOGW(int Level, String Tag, String Msg)
	{
		if(!ValidateLog(Level) || !BuildConfig.DEBUG)
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.w(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(Level, Tag, Msg, "WARN");
		}
	}
	
	


	static public void LOGE(String Tag, String Msg)
	{
		if(!ValidateLog(LocalLog.LOG_LEVEL_DEBUG))
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.e(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(LocalLog.LOG_LEVEL_DEBUG, Tag, Msg, "ERROR");
		}
	}
	
	static public void LOGD(String Tag, String Msg)
	{
		if(!ValidateLog(LocalLog.LOG_LEVEL_DEBUG))
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.d(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(LocalLog.LOG_LEVEL_DEBUG, Tag, Msg, "DEBUG");
		}
	}
	
	
	static public void LOGV(String Tag, String Msg)
	{
		if(!ValidateLog(LocalLog.LOG_LEVEL_DEBUG))
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.v(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(LocalLog.LOG_LEVEL_DEBUG, Tag, Msg, "VERBOSE");
		}
	}
	
	
	static public void LOGI(String Tag, String Msg)
	{
		if(!ValidateLog(LocalLog.LOG_LEVEL_DEBUG))
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.i(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(LocalLog.LOG_LEVEL_DEBUG, Tag, Msg, "INFO");
		}
	}
	
	
	static public void LOGW(String Tag, String Msg)
	{
		if(!ValidateLog(LocalLog.LOG_LEVEL_DEBUG))
			return;
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_CONSOLE) == LOG_DISPLAY_CONSOLE)
			Log.w(Tag, Msg);
		if((LocalLog.GetLogDisplay() & LOG_DISPLAY_FILE) == LOG_DISPLAY_FILE)
		{
			FileWriteLog(LocalLog.LOG_LEVEL_DEBUG, Tag, Msg, "WARN");
		}
	}

	static public void w(String Tag, String Msg)
	{
		LOGW(Tag,Msg);
	}

	static public void e(String Tag, String Msg)
	{
		LOGE(Tag,Msg);
	}
	
	static public void i(String Tag, String Msg)
	{
		LOGI(Tag,Msg);
	}
	
	static public void d(String Tag, String Msg)
	{
		LOGD(Tag,Msg);
	}
	static public void v(String Tag, String Msg)
	{
		LOGV(Tag,Msg);
	}

}
