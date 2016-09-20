package com.example.vlctest;

import java.io.File;

import android.os.Environment;

public class ConfigurationConst
{
    public static String WEB_SERVER_ADDR = "WEB_SERVER_ADDR";

    public static String WEB_SERVER_KEY = "WEB_SERVER_KEY";

    public static int MAX_DATA = 100000;

    public static int ALPHA_VALUE = 100;

    public static boolean VERSION_FLAG = false; // false:内部测试版本 true: 市场外部版本

    public static int DIALOG_SHOW_TIME = 1000;

    public static String APP_NAME = "qukan2_android";

    public static final String PREFERENCES_NAME = "mobileipc_sharedprefrences";

    public static final String QUKAN_30_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "qukan" + File.separator + "qukan3";

    public static final String QUKAN_30_PATH_RECORD = "qukan" + File.separator + "qukan3" + File.separator + "record";

    public static final int LISTEN_TIME_OUT = 5000;

    public static final int FRAME_QUEUE_INIT_LENGTH = 10;

    public static final int QUEUE_MAX_TIME_LENGTH = 3000;

    public static final int RECOVER_MAX_DATA_LENGTH = 20;

    public static final int PCM_MAX_DATA_LENGTH = 30;

    public static final int VIDEO_MAX_LENGTH = 200000;

    public static final int AUDIO_MAX_LENGTH = 10000;

    public static final int TIME_INTERVAL_TIME = 100;

    public static final int TIME_INTERVAL_TIME_HUAWEI = 1000;

    public static final int QUEUE_MAX_TIME_LENGTH_HUAWEI = 15000;

    public static final int LIVE_PLAY_TRAIL_TIME = 60 * 5;

    public static final int SEEK_BAR_TIME = 3;

    public static final int LIVE_PALY_PAUSE_TIME = 60 * 3;

    public static final int SDCARD_STORAGE = 300;
    
    public static final int GET_COMMENT_COUNT = 10;
    
    public static final int COMMENT_MAX_COUNT = 10;

    static
    {
        File file = new File(QUKAN_30_PATH);
        file.mkdirs();
    }
}
