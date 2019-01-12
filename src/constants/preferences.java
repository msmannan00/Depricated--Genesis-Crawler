package constants;

public class preferences
{

    /*Crawler Preferences*/
    public static int proxyPort = 9150;
    public static int maxThreadCount = 35;
    public static int requestTimeGap = 1;
    public static int requestQueueReloadGap = 1;
    public static int internetConsumption = 10;

    public static int maxLogLines = 10000;
    public static int maxLogFiles = 30;
    public static int maxQueueSize = 5000;
    public static int minQueueSize = 500;
    public static int maxDLinkUrlSize = 30;
    public static int retryCount = 10;
    public static int retryMinutes = 1666;
    public static int backupTimer = 3600;
    public static int max_backup_files_count = 30;
    public static boolean useTorFromOS = true;
    public static String networkType = "Onion";

    /*Session Preferences*/
    public static String filepath_url = "extracted_urls.txt";
    public static String filepath_dlink = "extracted_dlinks.txt";
    public static String filepath_queue_manager = "queue_manager";
    public static String filepath_queue_manager_backup = "queue_backup//queue_manager_";

    public static enumeration.classificationType classfier_type = enumeration.classificationType.finance;
}
