package Constants;

/*Crawler Preferences*/
public class preferences
{
    /*Threads*/
    public static int maxThreadCount = 35;
    public static int threadAwakeTime = 1000;

    /*Queues*/
    public static int requestQueueReloadGap = 1000;
    public static int maxQueueSize = 5000;
    public static int minQueueSize = 500;
    public static int retryCount = 10;
    public static int retryMinutes = 1666;
    public static int maxUrlDepth = 3;

    /*Logs*/
    public static int maxLogLines = 10000;
    public static int maxLogFiles = 30;
    public static boolean isErrorEnabled = false;

    /*Backup*/
    public static int backupTimer = 3600000;
    public static int backupTimerForced = 1000;
    public static int max_backup_files_count = 30;

    /*Network*/
    public static int proxyPort = 9150;
    public static int maxDLinkUrlaSize = 30;
    public static String networkType = "Onion";
    public static int connectionTimeOut = 210000;
    public static int readTimeOut = 210000;
    public static int httpScannerTimeOut = 310000;

}
