package com.example.vlctest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.provider.MediaStore.MediaColumns;
import android.telephony.TelephonyManager;
import android.util.FloatMath;
import android.view.WindowManager;

public class PublicUtils
{
    private static final int RC_VENDOR_QKPHONE = 3;
    private static BufferedWriter output = null;
    private static ReentrantReadWriteLock rWLock = new ReentrantReadWriteLock();

    public static void fillByteArray(byte[] dataSrc, byte[] dataDes, int position, int length)
    {
        System.arraycopy(dataSrc, 0, dataDes, position, length);
    }

    public static byte[] intToByteArray1(int i)
    {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] intToByteReseverArray(int i)
    {
        byte[] result = new byte[4];
        result[3] = (byte) ((i >> 24) & 0xFF);
        result[2] = (byte) ((i >> 16) & 0xFF);
        result[1] = (byte) ((i >> 8) & 0xFF);
        result[0] = (byte) (i & 0xFF);
        return result;
    }

    public static byte[] shortToByteReseverArray(short s)
    {
        byte[] shortBuf = new byte[2];
        // for (int i = 0; i < 2; i++)
        // {
        // int offset = (shortBuf.length - 1 - i) * 8;
        // shortBuf[i] = (byte) ((s >>> offset) & 0xff);
        // }

        int i = 0;
        int offset = (shortBuf.length - 1 - i) * 8;
        shortBuf[1] = (byte) ((s >>> offset) & 0xff);

        i += 1;
        offset = (shortBuf.length - 1 - i) * 8;
        shortBuf[0] = (byte) ((s >>> offset) & 0xff);
        return shortBuf;
    }

    public static int byteArrayToInt(byte[] bytes)
    {
        int result = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            int tmpVal = (bytes[i] << (8 * (3 - i)));
            switch (i)
            {
            case 0:
                tmpVal = tmpVal & 0xFF000000;
                break;
            case 1:
                tmpVal = tmpVal & 0x00FF0000;
                break;
            case 2:
                tmpVal = tmpVal & 0x0000FF00;
                break;
            case 3:
                tmpVal = tmpVal & 0x000000FF;
                break;
            }
            result = result | tmpVal;
        }
        return result;
    }

    public static short byteArrayToShort(byte[] bytes)
    {
        return (short) ((bytes[0] & 0xff) | (bytes[1] & 0xff) << 8);
    }

    public static byte[] shortToByteArray1(short s)
    {
        byte[] shortBuf = new byte[2];
        for (int i = 0; i < 2; i++)
        {
            int offset = (shortBuf.length - 1 - i) * 8;
            shortBuf[i] = (byte) ((s >>> offset) & 0xff);
        }
        return shortBuf;
    }

    public static boolean matchMail(String mailAddr)
    {
        String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(mailAddr);
        boolean isMatched = matcher.matches();
        return isMatched;
    }

    public static ProgressDialog showModalDialog(Context context)
    {
        return ProgressDialog.show(context, "", "正在加载数据，请稍候......", true, false);
   }

 // 按寬高壓縮載入圖片
    public static Bitmap getBitmap(String path, int width, int heigh)
    {
        Options op = new Options();
        op.inJustDecodeBounds = true;
        Bitmap bt = BitmapFactory.decodeFile(path, op);
        int xScale = op.outWidth / width;
        int yScale = op.outHeight / heigh;
        op.inSampleSize = xScale > yScale ? xScale : yScale;
        op.inJustDecodeBounds = false;
        bt = BitmapFactory.decodeFile(path, op);
        if (bt != null)
        {
            bt = Bitmap.createScaledBitmap(bt, width, heigh, false);
        }
        return bt;
    }

    public static Bitmap getLoacalBitmap(String url)
    {
        try
        {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);// /把流转化为Bitmap图片

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

 // 按寬高壓縮載入圖片方法2
    public static Bitmap getBitmap2(String imageFilePath, int displayWidth, int displayHeight)
    {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imageFilePath, bitmapOptions);

     // 编码后bitmap的宽高,bitmap除以屏幕宽度得到压缩比
        int widthRatio = (int) Math.ceil(bitmapOptions.outWidth / (float) displayWidth);
        int heightRatio = (int) Math.ceil(bitmapOptions.outHeight / (float) displayHeight);

        if (widthRatio > 1 && heightRatio > 1)
        {
            if (widthRatio > heightRatio)
            {
            	// 压缩到原来的(1/widthRatios)
                bitmapOptions.inSampleSize = widthRatio;
            }
            else
            {
                bitmapOptions.inSampleSize = heightRatio;
            }
        }
        bitmapOptions.inJustDecodeBounds = false;
        bmp = BitmapFactory.decodeFile(imageFilePath, bitmapOptions);
        return bmp;
    }

    public static Bitmap getScaleBitmap(Bitmap bitmap, int nWidth, int nHeight)
    {
        Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap, nWidth, nHeight, true);
        return mBitmap;
    }

    public static void full(boolean enable, Activity context)
    {
        if (enable)
        {
            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            context.getWindow().setAttributes(lp);
            context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        else
        {
            WindowManager.LayoutParams attr = context.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            context.getWindow().setAttributes(attr);
            context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public static boolean isEqualNull(String name)
    {
        if ("null".equals(name))
        {
            return true;
        }
        return false;
    }

    /**
     * 获取当前日期是星期几<br>
     * 
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(int day)
    {
    	String[] weekDays =
            { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" };
            int w = day - 1;
            if (w < 0)
                w = 0;

            return weekDays[w];
    }

    /**
     * 释放bitmap
     * 
     * @param bitmap
     */
    public static void releaseBitmap(Bitmap bitmap)
    {
        if ((null != bitmap) && (!bitmap.isRecycled()))
        {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static void deleteFile(String sFilePath)
    {
        File file = null;
        try
        {
            file = new File(sFilePath);
            deleteFile(file);
        }
        catch (Exception e)
        {

        }
    }

    private static void deleteFile(File file)
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {

            	// 返回文件夹中有的数据

                File[] files = file.listFiles();

             // 先判断下有没有权限，如果没有权限的话，就不执行了

                if (null == files)
                    return;

                for (int i = files.length - 1; i >= 0; i--)
                {
                    files[i].delete();
                }
            }
            else
            {
                file.delete();
            }
        }
    }

    private static String getLogPath()
    {
        String logDir = ConfigurationConst.QUKAN_30_PATH + File.separator + "log";
        try
        {
            File filedir = new File(logDir);
            if (!filedir.exists())
            {
                filedir.mkdirs();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        String sFileName = logDir + File.separator + dateString + ".txt";

        return sFileName;
    }

    public static void createLogFile()
    {
        String logDir = ConfigurationConst.QUKAN_30_PATH + File.separator + "log";
        try
        {
            File filedir = new File(logDir);
            if (!filedir.exists())
            {
                filedir.mkdirs();
            }
            Date currentTime = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateString = formatter.format(currentTime);
            String sFileName = logDir + File.separator + "java" + dateString + ".txt";

            try
            {
                File f = new File(sFileName);
                output = new BufferedWriter(new FileWriter(f));
            }
            catch (Exception e)
            {
                output = null;
            }
        }
        catch (Exception e)
        {
            output = null;
        }

    }

    public static void writeLogFile(String str)
    {
        if (ConfigurationConst.VERSION_FLAG)
        {
            return;
        }
        if (null != output)
        {
            try
            {
                rWLock.writeLock().lock();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                Date currentTime = new Date();
                String dateString = formatter.format(currentTime);
                output.write(dateString + "  :  " + str + "\n");
                output.flush();
            }
            catch (IOException e)
            {
            }
            finally
            {
                rWLock.writeLock().unlock();
            }
        }
    }

    public static void outPutClose()
    {
        if (null != output)
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
            }
            output = null;
        }
    }

    public static boolean checkNetworkInfo(Activity activity)
    {
        ConnectivityManager conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        // mobile 3G Data Network
        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        if (mobile == State.CONNECTED || mobile == State.CONNECTING)
            return true;
        return false;
    }

    public static String getVersionName(Activity activity)
    {
        String versionName = "";
        try
        {
            versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            versionName = "";
        }
        return versionName;
    }

    public static String getDeviceId(Activity activity)
    {
        // return "352648052171485";
        // return "868128014817886";// "352648052171485";// "351554057402332";
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);//
        return tm.getDeviceId();
    }

    public static boolean getFilterPhoneModel()
    {
        // if (GlobalVar.getPhoneModel().equals("Galaxy Nexus"))
        // {
        // return true;
        // }
        return false;
    }

    public static double log(double value, double base)
    {
        return Math.log(value) / Math.log(base);
    }

    public static boolean isMobileNO(String mobiles)
    {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static String getTimerStr(int nTime, String replace)
    {
        String returnValue = "00" + replace + "00" + replace + "00";
        int nMin = nTime / 60;
        int nSec = nTime % 60;
        if (nMin >= 60)
        {
            int nHour = nMin / 60;
            nMin = nMin % 60;
            returnValue = String.format("%02d", nHour) + replace + String.format("%02d", nMin) + replace
                    + String.format("%02d", nSec);
        }
        else
        {
            returnValue = String.format("%02d", 0) + replace + String.format("%02d", nMin) + replace
                    + String.format("%02d", nSec);
        }
        return returnValue;
    }

    public static void saveBitmap(File file, Bitmap bitmap)
    {
        if (file.exists())
        {
            file.delete();
        }
        else
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        FileOutputStream fOut = null;
        try
        {
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fOut.flush();
                fOut.close();
                fOut = null;
            }
            catch (IOException e)
            {
            }
        }
    }

    private static long lastClickTime;

    public static boolean isFastDoubleClick()
    {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500)
        {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static int getAndroidSDKVersion()
    {
        int version = 0;
        try
        {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        }
        catch (NumberFormatException e)
        {

        }
        return version;
    }

    public static boolean isExistSDCard()
    {
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
            return true;
        }
        else
            return false;
    }

    public static long getSDFreeSize()
    {
    	// 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();
        // 返回SD卡空闲大小
        // return freeBlocks * blockSize; //单位Byte
        // return (freeBlocks * blockSize)/1024; //单位KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static long getSDAllSize()
    {
    	// 取得SD卡文件路径
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 获取所有数据块数
        long allBlocks = sf.getBlockCount();
        // 返回SD卡大小
        // return allBlocks * blockSize; //单位Byte
        // return (allBlocks * blockSize)/1024; //单位KB
        return (allBlocks * blockSize) / 1024 / 1024; // 单位MB
    }

    public static long getSDFreeSize(String path)
    {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long freeBlocks = sf.getAvailableBlocks();
        // 閿熸枻鎷烽敓鏂ゆ嫹SD閿熸枻鎷烽敓鏂ゆ嫹閿熷彨杈炬嫹灏�
        // return freeBlocks * blockSize; //閿熸枻鎷蜂綅Byte
        // return (freeBlocks * blockSize)/1024; //閿熸枻鎷蜂綅KB
        return (freeBlocks * blockSize) / 1024 / 1024; // 閿熸枻鎷蜂綅MB
    }

    public static long getSDAllSize(String path)
    {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long allBlocks = sf.getBlockCount();
        // return allBlocks * blockSize; //閿熸枻鎷蜂綅Byte
        // return (allBlocks * blockSize)/1024; //閿熸枻鎷蜂綅KB
        return (allBlocks * blockSize) / 1024 / 1024; // 閿熸枻鎷蜂綅MB
    }

    public static String[] getVolumePaths(Activity activity)
    {
        String[] paths =
        {};
        StorageManager sm = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);
        try
        {
            paths = (String[]) sm.getClass().getMethod("getVolumePaths", null).invoke(sm, null);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return paths;
    }

    public static String getVolumeState(Activity activity, String sPath)
    {
        String sReturn = "";
        StorageManager sm = (StorageManager) activity.getSystemService(Context.STORAGE_SERVICE);
        try
        {
            sReturn = (String) sm.getClass().getMethod("getVolumeState", String.class).invoke(sm, sPath);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        return sReturn;
    }

    public static String getDeviceType()
    {
        Build bd = new Build();
        return bd.MODEL;
    }

    public static void refreshPhotoFile(Context mContext, String filePath)
    {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        mContext.sendBroadcast(intent);
    }

    public static String getFilePathByContentResolver(Context context, Uri uri)
    {
        if (null == uri)
        {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c)
        {
            throw new IllegalArgumentException("Query on " + uri + " returns null result.");
        }
        try
        {
            if ((c.getCount() != 1) || !c.moveToFirst())
            {
            }
            else
            {
                filePath = c.getString(c.getColumnIndexOrThrow(MediaColumns.DATA));
            }
        }
        finally
        {
            c.close();
        }
        return filePath;
    }

    public static Bitmap getUrlBitMap(String url)
    {
        InputStream is = null;
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try
        {
            myFileUrl = new URL(url);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        try
        {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return bitmap;
    }

    public static String getDateStr(long timeMillions, String format)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillions);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(calendar.getTime());
        return dateString;
    }

    public static void full(Activity activity, boolean enable)
    {
        if (enable)
        {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(lp);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        else
        {
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attr);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
